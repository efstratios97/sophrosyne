package sophrosyne.core.actionconfirmationservice.service;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.Meter;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sophrosyne.core.actionservice.dto.ActionDTO;
import sophrosyne.core.actionservice.service.ActionService;
import sophrosyne.core.dynamicactionservice.dto.DynamicActionDTO;
import sophrosyne.core.dynamicactionservice.service.DynamicActionService;
import sophrosyne.core.executorservice.service.ActionExecutorService;
import sophrosyne.core.metricsservice.service.MetricsService;
import sophrosyne_api.core.actionconfirmationservice.model.Confirmation;
import sophrosyne_api.core.dashboardstatistics.model.Stats;

@Service
public class ActionConfirmationService {

  private final Logger logger = LogManager.getLogger(getClass());

  @Autowired ActionExecutorService actionExecutorService;

  @Autowired ActionService actionService;

  @Autowired DynamicActionService dynamicActionService;

  @Autowired MetricsService metricsService;

  public boolean evaluateConfirmationResponse(Confirmation confirmation) {
    if (confirmation.getConfirmed() == 1) {
      return confirmAction(confirmation.getRunningActionId());
    } else {
      return rejectAction(confirmation.getRunningActionId());
    }
  }

  public boolean confirmAction(String id) {
    HashMap<String, Object> confirmedActionProcessDataShared =
        actionExecutorService.actionProcessDataShared.get(id);
    if (confirmedActionProcessDataShared == null) {
      return false;
    }
    confirmedActionProcessDataShared.put("confirmed", true);
    actionExecutorService.actionProcessDataShared.put(id, confirmedActionProcessDataShared);
    return true;
  }

  public boolean rejectAction(String id) {
    HashMap<String, Object> confirmedActionProcessDataShared =
        actionExecutorService.actionProcessDataShared.get(id);
    if (confirmedActionProcessDataShared == null) {
      return false;
    }
    actionExecutorService.actionProcessDataShared.remove(id);
    return true;
  }

  public List<Confirmation> getConfirmations() {
    List<Confirmation> confirmationRequests = new ArrayList<>();
    actionExecutorService.actionProcessDataShared.forEach(
        (runningId, actionProcessData) -> {
          if (!(boolean) actionProcessData.get("confirmed")) {
            try {
              Confirmation confirmation =
                  new Confirmation()
                      .runningActionId(runningId)
                      .actionName(
                          getActionByAnyType(
                                  ((ActionDTO) actionProcessData.get("actionDTO")).getId())
                              .get()
                              .getName())
                      .actionDescription(
                          ((ActionDTO) actionProcessData.get("actionDTO")).getDescription())
                      .actionCommand(((ActionDTO) actionProcessData.get("actionDTO")).getCommand())
                      .confirmed((boolean) actionProcessData.get("confirmed") ? 1 : 0)
                      .triggeredTime(
                          ((LocalDateTime) actionProcessData.get("triggeredTime"))
                              .atZone(ZoneOffset.systemDefault())
                              .toOffsetDateTime());
              confirmationRequests.add(confirmation);
            } catch (NoSuchElementException e) {
              logger.error(e.getMessage());
            }
          }
        });
    return confirmationRequests;
  }

  public Optional<Confirmation> getConfirmationById(String id) {
    List<Confirmation> confirmations = getConfirmations();

    List<Confirmation> confirmationsFiltered =
        confirmations.stream()
            .filter((confirmation) -> confirmation.getRunningActionId().equals(id))
            .toList();

    if (confirmationsFiltered.size() > 1) {
      logger.warn("There should be only one confirmation with the id: {}", id);
    }
    return confirmationsFiltered.isEmpty()
        ? Optional.empty()
        : Optional.of(confirmations.getLast());
  }

  public Optional<ActionDTO> getActionByAnyType(String id) {
    ActionDTO actionDTOTmp = null;

    Optional<DynamicActionDTO> potentialDynamicActionDTO =
        dynamicActionService.getDynamicActionDTOByID(id);
    Optional<ActionDTO> potentialActionDTO = actionService.getActionDTOByID(id);

    if (potentialDynamicActionDTO.isPresent()) {
      actionDTOTmp = potentialDynamicActionDTO.get();
      return Optional.of(actionDTOTmp);
    } else if (potentialActionDTO.isPresent()) {
      actionDTOTmp = potentialActionDTO.get();
      return Optional.of(actionDTOTmp);
    } else {
      return Optional.empty();
    }
  }

  @PostConstruct
  private void registerMetrics() {
    try {
      Meter meter = metricsService.getSophrosyneStandardJobMeter();
      meter
          .gaugeBuilder("sophrosyne.amount_of_actions_requiring_confirmation")
          .setDescription("Amount of Actions requiring confirmation")
          .buildWithCallback(
              result -> result.record(getAmountActionsRequiringConfirmation(), Attributes.empty()));

      meter
          .gaugeBuilder("sophrosyne.active_action_confirmations")
          .setDescription(
              "Info regarding active Action Confirmations requiring manual rejection/confirmation")
          .buildWithCallback(
              result -> {
                getConfirmations()
                    .forEach(
                        confirmation -> {
                          result.record(1, getActionConfirmationAttributes(confirmation));
                        });
              });
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  public Stats countRequiringConfirmation(Stats stats) {
    stats.setRequiringConfirmation(
        actionExecutorService.actionProcessDataShared.keySet().stream()
            .filter(
                actionId ->
                    !(boolean)
                        actionExecutorService
                            .actionProcessDataShared
                            .get(actionId)
                            .get("confirmed"))
            .mapToLong(confirmed -> 1L)
            .sum());
    return stats;
  }

  public Long getAmountActionsRequiringConfirmation() {
    Stats stats = new Stats();
    stats = countRequiringConfirmation(stats);
    return stats.getRequiringConfirmation();
  }

  private Attributes getActionConfirmationAttributes(Confirmation confirmation) {
    return Attributes.builder()
        .put("id", confirmation.getRunningActionId())
        .put("command", confirmation.getActionCommand())
        .put("description", confirmation.getActionDescription())
        .put("name", confirmation.getActionName())
        .build();
  }
}
