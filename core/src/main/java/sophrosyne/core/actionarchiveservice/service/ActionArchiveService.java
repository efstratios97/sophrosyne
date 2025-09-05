package sophrosyne.core.actionarchiveservice.service;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.Meter;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sophrosyne.core.actionarchiveservice.dto.ActionArchiveDTO;
import sophrosyne.core.actionarchiveservice.repository.ActionArchiveMetadata;
import sophrosyne.core.actionarchiveservice.repository.ActionArchiveRepository;
import sophrosyne.core.actionservice.dto.ActionDTO;
import sophrosyne.core.globalservices.utilservice.UtilService;
import sophrosyne.core.metricsservice.service.MetricsService;
import sophrosyne_api.core.actionarchiveservice.model.ActionArchive;
import sophrosyne_api.core.dashboardstatistics.model.Stats;
import sophrosyne_api.core.dashboardstatistics.model.StatsTimeline;

@Service
public class ActionArchiveService {

  private final Logger logger = LogManager.getLogger(getClass());
  @Autowired MetricsService metricsService;
  @Autowired private ActionArchiveRepository actionArchiveRepository;
  @Autowired private UtilService utilService;

  public void purgeCompleteArchive() {
    actionArchiveRepository.deleteAll();
  }

  public ActionArchiveDTO createActionArchiveDTO(
      ActionDTO actionDTO,
      LocalDateTime executionStartPoint,
      LocalDateTime executionEndPoint,
      byte[] executionLogFileData,
      byte[] postExecutionLogFileData,
      int exitCode,
      String type,
      String version) {
    ActionArchiveDTO actionArchiveDTO =
        createActionArchiveDTOFromExecutionData(
            actionDTO,
            executionStartPoint,
            executionEndPoint,
            executionLogFileData,
            postExecutionLogFileData,
            exitCode,
            type,
            version);
    actionArchiveRepository.save(actionArchiveDTO);
    return actionArchiveDTO;
  }

  public List<ActionArchiveDTO> getActionArchives() {
    return actionArchiveRepository.findAll();
  }

  public List<ActionArchiveDTO> getActionArchivesByActionId(String actionId) {
    return actionArchiveRepository.findAllByActionId(actionId).stream()
        .sorted(Comparator.comparing(ActionArchiveDTO::getExecutionStartPoint))
        .toList();
  }

  public Optional<ActionArchiveDTO> getLastActionArchiveByActionId(String actionId) {
    return actionArchiveRepository.findTopByActionIdOrderByExecutionStartPointDesc(actionId);
  }

  public List<ActionArchiveMetadata> getActionArchiveMetadata() {
    return actionArchiveRepository.findAllBy().stream()
        .sorted(Comparator.comparing(ActionArchiveMetadata::getExecutionStartPoint).reversed())
        .toList();
  }

  public Optional<ActionArchiveDTO> getActionArchiveDTO(Long id) {
    return actionArchiveRepository.findById(id);
  }

  public sophrosyne_api.core.actionarchiveservice.model.ActionArchiveMetadata
      mapActionArchiveMetadataInterfaceToActionArchiveMetadata(
          ActionArchiveMetadata actionArchiveMetadata) {
    return new sophrosyne_api.core.actionarchiveservice.model.ActionArchiveMetadata()
        .id(actionArchiveMetadata.getId())
        .actionId(actionArchiveMetadata.getActionId())
        .actionName(actionArchiveMetadata.getActionName())
        .actionCommand(actionArchiveMetadata.getActionCommand())
        .executionStartPoint(
            actionArchiveMetadata
                .getExecutionStartPoint()
                .atZone(ZoneOffset.systemDefault())
                .toOffsetDateTime())
        .executionEndPoint(
            actionArchiveMetadata
                .getExecutionEndPoint()
                .atZone(ZoneOffset.systemDefault())
                .toOffsetDateTime())
        .exitCode(actionArchiveMetadata.getExitCode())
        .type(
            sophrosyne_api.core.actionarchiveservice.model.ActionArchiveMetadata.TypeEnum.valueOf(
                actionArchiveMetadata.getType()))
        .version(actionArchiveMetadata.getVersion());
  }

  public sophrosyne_api.core.actionarchiveservice.model.ActionArchiveFiledata
      mapActionArchiveDTOToActionArchiveFiledata(ActionArchiveDTO actionArchiveDTO) {
    return new sophrosyne_api.core.actionarchiveservice.model.ActionArchiveFiledata()
        .executionLogFileData(
            utilService.convertFileDataToString(actionArchiveDTO.getExecutionLogFileData()))
        .postExecutionLogFileData(
            utilService.convertFileDataToString(actionArchiveDTO.getPostExecutionLogFileData()));
  }

  public ActionArchive mapActionArchiveDtoToActionArchive(ActionArchiveDTO actionArchiveDTO) {
    return new ActionArchive()
        .actionId(actionArchiveDTO.getActionId())
        .actionName(actionArchiveDTO.getActionName())
        .actionCommand(actionArchiveDTO.getActionCommand())
        .executionStartPoint(
            actionArchiveDTO.getExecutionStartPoint() != null
                ? actionArchiveDTO
                    .getExecutionStartPoint()
                    .atZone(ZoneOffset.systemDefault())
                    .toOffsetDateTime()
                : OffsetDateTime.of(LocalDateTime.MIN, ZoneOffset.UTC))
        .executionEndPoint(
            actionArchiveDTO.getExecutionEndPoint() != null
                ? actionArchiveDTO
                    .getExecutionEndPoint()
                    .atZone(ZoneOffset.systemDefault())
                    .toOffsetDateTime()
                : OffsetDateTime.of(LocalDateTime.MAX, ZoneOffset.UTC))
        .exitCode(actionArchiveDTO.getExitCode())
        .executionLogFileData(
            utilService.convertFileDataToString(actionArchiveDTO.getExecutionLogFileData()))
        .postExecutionLogFileData(
            utilService.convertFileDataToString(actionArchiveDTO.getPostExecutionLogFileData()))
        .type(ActionArchive.TypeEnum.valueOf(actionArchiveDTO.getType()))
        .version(actionArchiveDTO.getVersion());
  }

  public Stats getAllStats(Stats stats) {
    List<ActionArchiveDTO> actionArchiveDTOS = getActionArchives();
    stats.setTotalRun((long) actionArchiveDTOS.size());
    stats.setTotalSuccess(
        actionArchiveDTOS.stream()
            .filter(actionArchiveDTO -> actionArchiveDTO.getExitCode() == 0)
            .count());
    stats.setTotalFail(
        actionArchiveDTOS.stream()
            .filter(actionArchiveDTO -> actionArchiveDTO.getExitCode() != 0)
            .count());
    stats.setTotalExternal(
        actionArchiveDTOS.stream()
            .filter(
                actionArchiveDTO ->
                    actionArchiveDTO
                        .getType()
                        .equals(
                            sophrosyne.core.actionarchiveservice.dto.ActionArchiveDTO.TYPES.EXTERNAL
                                .name()))
            .count());
    stats.setTotalInternal(
        actionArchiveDTOS.stream()
            .filter(
                actionArchiveDTO ->
                    actionArchiveDTO
                        .getType()
                        .equals(
                            sophrosyne.core.actionarchiveservice.dto.ActionArchiveDTO.TYPES.INTERNAL
                                .name()))
            .count());
    stats.setTimeline(
        new StatsTimeline()
            .totalXAxis(
                actionArchiveDTOS.stream()
                    .map(
                        actionArchiveDTO -> {
                          if (actionArchiveDTO.getExecutionStartPoint() != null) {
                            return actionArchiveDTO
                                .getExecutionStartPoint()
                                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                          } else {
                            logger.error("No ExecutionStartPoint available: {}", actionArchiveDTO);
                            return "unknown";
                          }
                        })
                    .toList())
            .totalSuccessYValues(
                actionArchiveDTOS.stream()
                    .map(actionArchiveDTO -> actionArchiveDTO.getExitCode() == 0 ? 1L : 0L)
                    .collect(Collectors.toList()))
            .totalYValues(
                actionArchiveDTOS.stream()
                    .map(actionArchiveDTO -> 1L)
                    .collect(Collectors.toList())));
    return stats;
  }

  private sophrosyne.core.actionarchiveservice.dto.ActionArchiveDTO
      createActionArchiveDTOFromExecutionData(
          ActionDTO actionDTO,
          LocalDateTime executionStartPoint,
          LocalDateTime executionEndPoint,
          byte[] executionLogFileData,
          byte[] postExecutionLogFileData,
          int exitCode,
          String type,
          String version) {
    return sophrosyne.core.actionarchiveservice.dto.ActionArchiveDTO.builder()
        .actionId(actionDTO.getId())
        .actionName(actionDTO.getName())
        .actionCommand(actionDTO.getCommand())
        .executionStartPoint(executionStartPoint)
        .executionEndPoint(executionEndPoint)
        .executionLogFileData(executionLogFileData)
        .postExecutionLogFileData(postExecutionLogFileData)
        .exitCode(exitCode)
        .type(type)
        .version(version)
        .build();
  }

  @PostConstruct
  private void exportCurrentArchive() {
    try {
      Meter meter = metricsService.getSophrosyneHighLoadsJobMeter();

      meter
          .gaugeBuilder("sophrosyne.action_archive")
          .setDescription(
              "Info regarding archived Actions. Be aware of the high loads reading this metric can cause. The value represents the exit code")
          .buildWithCallback(
              result -> {
                getActionArchives()
                    .forEach(
                        actionArchiveDTO -> {
                          result.record(
                              actionArchiveDTO.getExitCode(),
                              getActionArchiveDTOAttributes(actionArchiveDTO));
                        });
              });
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  private Attributes getActionArchiveDTOAttributes(ActionArchiveDTO actionArchiveDTO) {
    return Attributes.builder()
        .put("id", actionArchiveDTO.getId())
        .put("name", actionArchiveDTO.getActionName())
        .put("command", actionArchiveDTO.getActionCommand())
        .put("version", actionArchiveDTO.getVersion())
        .put("triggerType", actionArchiveDTO.getType())
        .put("exitCode", actionArchiveDTO.getExitCode())
        .put("executionStartPoint", actionArchiveDTO.getExecutionStartPoint().toString())
        .put("executionEndPoint", actionArchiveDTO.getExecutionEndPoint().toString())
        .put(
            "executionLog",
            utilService.convertFileDataToString(actionArchiveDTO.getExecutionLogFileData()))
        .put(
            "postExecutionLogFile",
            utilService.convertFileDataToString(actionArchiveDTO.getPostExecutionLogFileData()))
        .build();
  }
}
