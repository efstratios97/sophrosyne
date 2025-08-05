package sophrosyne.core.executorservice.service;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.Meter;
import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sophrosyne.core.actionarchiveservice.service.ActionArchiveService;
import sophrosyne.core.actionservice.dto.ActionDTO;
import sophrosyne.core.broadcastservice.SophrosyneBroadcaster;
import sophrosyne.core.dynamicactionservice.dto.DynamicActionDTO;
import sophrosyne.core.executorservice.handler.ActionHandler;
import sophrosyne.core.executorservice.utils.Utils;
import sophrosyne.core.globalservices.utilservice.UtilService;
import sophrosyne.core.metricsservice.service.MetricsService;
import sophrosyne_api.core.dashboardstatistics.model.Stats;

@Service
@Getter
public class ActionExecutorService {

  public final HashMap<String, Process> actionProcess = new HashMap<>();
  public final HashMap<String, List<Process>> allRunningActionProcess = new HashMap<>();
  public final HashMap<String, HashMap<String, Object>> actionProcessDataShared = new HashMap<>();
  private final Logger logger = LogManager.getLogger(getClass());
  @Autowired MetricsService metricsService;
  @Autowired private SophrosyneBroadcaster sophrosyneBroadcaster;
  @Autowired private ActionHandler actionHandler;
  @Autowired private ActionArchiveService actionArchiveService;
  @Autowired private UtilService utilService;
  @Autowired private Utils utils;

  public void registerAction(ActionDTO actionDTO, String triggerType, boolean confirmed) {
    try {
      String runningId;
      if (actionDTO instanceof DynamicActionDTO) {
        // Check if same DynamicAction already queued and if it should be kept
        try {
          removeDynamicActionFromConfirmationQueueByKeepLatestConfirmationRequest(
              (DynamicActionDTO) actionDTO);
        } catch (Exception e) {
          logger.error(e.getMessage());
        }
        runningId = utils.getRunningId(actionDTO);
        ((DynamicActionDTO) actionDTO).setRunningActionId(runningId);
      } else {
        runningId = actionDTO.getId();
      }
      LocalDateTime triggerTime = LocalDateTime.now();
      actionProcessDataShared.put(
          runningId,
          new HashMap<>() {
            {
              put("executionLogFileData", new byte[] {});
              put("postExecutionLogFileData", new byte[] {});
              put("exitCode", -1);
              put("version", actionDTO.getVersion());
              put("type", triggerType);
              put("confirmed", confirmed);
              put("triggeredTime", triggerTime);
              put("actionDTO", actionDTO);
            }
          });
      CompletableFuture<HashMap<String, HashMap<String, Object>>> actionFuture =
          CompletableFuture.supplyAsync(
              () -> executeAction(runningId, actionDTO, actionProcessDataShared));
      actionHandler.getRunningActions().put(runningId, actionFuture);
      actionFuture.handle(
          (result, throwable) -> {
            try {
              archiveExecution(actionDTO);
            } catch (Exception e) {
              logger.error(e.getMessage());
            }
            try {
              allRunningActionProcess.remove(runningId);
            } catch (Exception e) {
              logger.error(e.getMessage());
            }
            try {
              actionProcessDataShared.remove(runningId);
            } catch (Exception e) {
              logger.error(e.getMessage());
            }
            try {
              actionProcess.remove(runningId);
            } catch (Exception e) {
              logger.error(e.getMessage());
            }
            return result;
          });
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  private HashMap<String, HashMap<String, Object>> executeAction(
      String runningId,
      ActionDTO actionDTO,
      HashMap<String, HashMap<String, Object>> actionProcessDataShared) {
    // Wait until it is confirmed
    while (!((boolean) this.actionProcessDataShared.get(runningId).get("confirmed"))) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        logger.error(e.getMessage());
        return actionProcessDataShared;
      }
    }
    try {
      // Set Execution Time-Points
      HashMap<String, Object> actionProcessDataSharedTmp =
          this.actionProcessDataShared.get(runningId);
      actionProcessDataSharedTmp.put("executionStartPoint", LocalDateTime.now());
      actionProcessDataSharedTmp.put("executionEndPoint", LocalDateTime.now());
      this.actionProcessDataShared.put(runningId, actionProcessDataSharedTmp);

      try {
        // Wait for UI to connect socket for streaming
        Thread.sleep(750);
      } catch (InterruptedException e) {
        logger.error(e.getMessage());
      }

      // Start Command Preparation & Execution
      List<String> command = prepareCommandExecution(actionDTO.getCommand());
      // Build Process
      ProcessBuilder processBuilder = new ProcessBuilder(command);
      processBuilder.redirectErrorStream(true);
      Optional<String> processExecutionError = Optional.empty();
      // Start Process
      try {
        actionProcess.put(runningId, processBuilder.start());
        if (allRunningActionProcess.containsKey(runningId)) {
          allRunningActionProcess.get(runningId).add(actionProcess.get(runningId));
        } else {
          List<Process> processList = new ArrayList<>();
          processList.add(actionProcess.get(runningId));
          allRunningActionProcess.put(runningId, processList);
        }
      } catch (Exception e) {
        logger.error(e.getMessage());
        processExecutionError = Optional.of(e.getMessage());
      }

      if (processExecutionError.isEmpty()) {
        // Stream Command prompt live
        try (BufferedReader reader =
            new BufferedReader(
                new InputStreamReader(actionProcess.get(runningId).getInputStream()))) {
          actionProcessDataShared =
              executionStreamHandler(
                  "Sophrosyne Streamer: Starting Action Execution...\n",
                  runningId,
                  actionProcessDataShared);
          String line;
          while ((line = reader.readLine()) != null) {
            actionProcessDataShared =
                executionStreamHandler(line, runningId, actionProcessDataShared);
          }
          actionProcessDataShared =
              executionStreamHandler(
                  "\nSophrosyne Streamer: Finished Action Execution...",
                  runningId,
                  actionProcessDataShared);
        } catch (Exception e) {
          logger.error(e.getMessage());
          actionProcessDataShared =
              executionStreamHandler(
                  "\nStream interrupted unexpectedly", runningId, actionProcessDataShared);
        }
      } else {
        actionProcessDataShared =
            executionStreamHandler(processExecutionError.get(), runningId, actionProcessDataShared);
      }

      int exitCode = actionProcess.get(runningId).waitFor();
      // Retrieve & Save exit code
      actionProcessDataShared.get(runningId).put("exitCode", exitCode);

      // Get & Save End-time
      actionProcessDataShared.get(runningId).put("executionEndPoint", LocalDateTime.now());

      // Read & Save postExecutionLogfile
      actionProcessDataShared
          .get(runningId)
          .put(
              "postExecutionLogFileData",
              utilService.readFile(actionDTO.getPostExecutionLogFilePath()));

    } catch (InterruptedException e) {
      logger.error(e.getMessage());
      actionProcessDataShared.get(runningId).put("executionEndPoint", LocalDateTime.now());
      return actionProcessDataShared;
    }
    return actionProcessDataShared;
  }

  public boolean stopExecution(String id) {
    for (Process actionProcess : allRunningActionProcess.getOrDefault(id, new ArrayList<>())) {
      Optional<ProcessHandle> processHandle = ProcessHandle.of(actionProcess.pid());
      processHandle.ifPresent(ph -> ph.descendants().forEach(ProcessHandle::destroy));
      processHandle.ifPresent(ph -> ph.children().forEach(ProcessHandle::destroy));
      actionProcess.destroy();
    }
    actionHandler.getRunningActions().get(id).cancel(true);
    return true;
  }

  private HashMap<String, HashMap<String, Object>> executionStreamHandler(
      String text,
      String runningId,
      HashMap<String, HashMap<String, Object>> actionProcessDataShared) {
    sophrosyneBroadcaster.sendText(runningId, text);
    text += "\n";
    actionProcessDataShared
        .get(runningId)
        .put(
            "executionLogFileData",
            ArrayUtils.addAll(
                (byte[]) actionProcessDataShared.get(runningId).get("executionLogFileData"),
                text.getBytes()));
    return actionProcessDataShared;
  }

  private void archiveExecution(ActionDTO actionDTO) {
    String id;
    if (actionDTO instanceof DynamicActionDTO) {
      id = ((DynamicActionDTO) actionDTO).getRunningActionId();
    } else {
      id = actionDTO.getId();
    }
    if ((boolean) actionProcessDataShared.get(id).get("confirmed")) {
      LocalDateTime executionStartPoint =
          (LocalDateTime) actionProcessDataShared.get(id).get("executionStartPoint");
      LocalDateTime executionEndPoint =
          (LocalDateTime) actionProcessDataShared.get(id).get("executionEndPoint");
      byte[] executionLogFileData =
          (byte[]) actionProcessDataShared.get(id).get("executionLogFileData");
      byte[] postExecutionLogFileData =
          (byte[]) actionProcessDataShared.get(id).get("postExecutionLogFileData");
      Integer exitCode = (Integer) actionProcessDataShared.get(id).get("exitCode");
      String type = (String) actionProcessDataShared.get(id).get("type");
      String version = (String) actionProcessDataShared.get(id).get("version");

      actionArchiveService.createActionArchiveDTO(
          actionDTO,
          executionStartPoint,
          executionEndPoint,
          executionLogFileData,
          postExecutionLogFileData,
          exitCode,
          type,
          version);

      try {
        exportFinishedRunningInformation(
            actionDTO,
            executionStartPoint,
            executionEndPoint,
            executionLogFileData,
            postExecutionLogFileData,
            exitCode,
            type,
            version);
      } catch (Exception e) {
        logger.error(e.getMessage());
      }
    }
  }

  public Stats countRunningActions(Stats stats) {
    stats.setCurrentRunning(
        allRunningActionProcess.values().stream()
            .flatMap(List::stream)
            .filter(Process::isAlive)
            .count());
    return stats;
  }

  private List<String> prepareCommandExecution(String command) {
    List<String> commands = new ArrayList<>();

    // Regex pattern to match double-quoted, single-quoted, and non-quoted parts
    Pattern pattern = Pattern.compile("\"([^\"]*)\"|'([^']*)'|(\\S+)");
    Matcher matcher = pattern.matcher(command);

    while (matcher.find()) {
      if (matcher.group(1) != null) {
        // Double-quoted part (retain the surrounding quotes)
        commands.add(matcher.group(1));
      } else if (matcher.group(2) != null) {
        // Single-quoted part (retain the surrounding quotes)
        commands.add(matcher.group(2));
      } else if (matcher.group(3) != null) {
        // Non-quoted part
        commands.add(matcher.group(3));
      }
    }
    return commands;
  }

  @PostConstruct
  private void exportCurrentRunningInformation() {
    try {
      Meter meter = metricsService.getSophrosyneStandardJobMeter();

      meter
          .gaugeBuilder("sophrosyne.running_actions")
          .setDescription("Info regarding running Actions. Exit Code -1 means running/unknown yet")
          .buildWithCallback(
              result -> {
                this.actionProcessDataShared.forEach(
                    (actionRunningId, stringObjectHashMap) -> {
                      ActionDTO actionDTO = (ActionDTO) stringObjectHashMap.get("actionDTO");
                      try {
                        result.record(
                            -1,
                            getActionAttributes(actionDTO).toBuilder()
                                .put(
                                    "ExecutionStartPoint",
                                    stringObjectHashMap.get("executionStartPoint").toString())
                                .put(
                                    "TriggeredTime",
                                    stringObjectHashMap.get("triggeredTime").toString())
                                .put("TriggerType", stringObjectHashMap.get("type").toString())
                                .put("ActionRunningId", actionRunningId)
                                .build());
                      } catch (Exception e) {
                        logger.error(e.getMessage());
                      }
                    });
              });
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  private void exportFinishedRunningInformation(
      ActionDTO actionDTO,
      LocalDateTime executionStartPoint,
      LocalDateTime executionEndPoint,
      byte[] executionLogFileData,
      byte[] postExecutionLogFileData,
      Integer exitCode,
      String type,
      String version) {
    try {
      Meter meter = metricsService.getSophrosyneStandardJobMeter();

      meter
          .gaugeBuilder("sophrosyne.finished_actions")
          .setDescription("Info regarding finished Actions")
          .buildWithCallback(
              result ->
                  result.record(
                      exitCode,
                      getFinishedActionAttributes(
                          actionDTO,
                          executionStartPoint,
                          executionEndPoint,
                          executionLogFileData,
                          postExecutionLogFileData,
                          exitCode,
                          type,
                          version)));
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  private Attributes getFinishedActionAttributes(
      ActionDTO actionDTO,
      LocalDateTime executionStartPoint,
      LocalDateTime executionEndPoint,
      byte[] executionLogFileData,
      byte[] postExecutionLogFileData,
      Integer exitCode,
      String type,
      String version)
      throws RuntimeException {
    try {
      return getActionAttributes(actionDTO).toBuilder()
          .put("TriggerType", type)
          .put("ExecutionStartPoint", executionStartPoint.toString())
          .put("ExecutionEndPoint", executionEndPoint.toString())
          .put("ExecutionLogs", new String(executionLogFileData, StandardCharsets.UTF_8))
          .put("PostExecutionLogFile", new String(postExecutionLogFileData, StandardCharsets.UTF_8))
          .put("ExitCode", exitCode)
          .put("Version", version)
          .build();
    } catch (Exception e) {
      logger.error(e.getMessage());
      throw new RuntimeException(e);
    }
  }

  private Attributes getActionAttributes(ActionDTO actionDTO) {
    return Attributes.builder()
        .put("Id", actionDTO.getId())
        .put("Name", actionDTO.getName())
        .put("Description", actionDTO.getDescription())
        .put("Command", actionDTO.getCommand())
        .put("RequiresConfirmation", actionDTO.getRequiresConfirmation())
        .put("Version", actionDTO.getVersion())
        .build();
  }

  private void removeDynamicActionFromConfirmationQueueByKeepLatestConfirmationRequest(
      DynamicActionDTO dynamicActionDTO) {
    this.actionProcessDataShared.forEach(
        (actionRunningId, stringObjectHashMap) -> {
          ActionDTO actionDTOInQueue = (ActionDTO) stringObjectHashMap.get("actionDTO");
          if (actionDTOInQueue.getId().equals(dynamicActionDTO.getId())) {
            if (dynamicActionDTO.getKeepLatestConfirmationRequest() == 1
                && !(boolean) stringObjectHashMap.get("confirmed")) {
              this.actionHandler
                  .getRunningActions()
                  .forEach(
                      (runningId, actionFuture) -> {
                        if (actionRunningId.equals(runningId)) {
                          actionFuture.cancel(true);
                        }
                      });
            }
          }
        });
  }
}
