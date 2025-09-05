package sophrosyne.core.executorservice.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sophrosyne.core.actionservice.dto.ActionDTO;
import sophrosyne.core.dynamicactionservice.dto.DynamicActionDTO;
import sophrosyne.core.dynamicactionservice.service.DynamicActionService;
import sophrosyne.core.executorservice.service.ActionExecutorService;

@Service
public class Utils {

  private final Logger logger = LogManager.getLogger(getClass());

  @Autowired @Lazy ActionExecutorService actionExecutorService;

  @Autowired DynamicActionService dynamicActionService;

  public boolean checkActionRunning(String id) {
    boolean isRunning = false;
    try {
      if (actionExecutorService.getActionProcess().containsKey(id)) {
        isRunning = actionExecutorService.getActionProcess().get(id).isAlive();
      }
    } catch (Exception e) {
      logger.error(e.getMessage());
      return isRunning;
    }
    return isRunning;
  }

  public boolean setInitialConfirmationStatus(ActionDTO actionDTO) {
    boolean initialConfirmationStatus = true;
    try {
      // If it needs confirmation then the initial confirmation status is false --> NOT Confirmed
      initialConfirmationStatus = actionDTO.getRequiresConfirmation() != 1;
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    return initialConfirmationStatus;
  }

  public ResponseEntity<Void> stopActionHelper(String id) {
    boolean cancelled = false;
    try {
      cancelled = actionExecutorService.stopExecution(id);
    } catch (Exception e) {
      logger.error(e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    if (!cancelled) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    return ResponseEntity.ok().build();
  }

  public ResponseEntity<Void> executeActionHelper(ActionDTO actionDTO, String triggerType) {
    boolean confirmed = true;
    if (actionDTO.getMuted() == 1) {
      return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
    }
    if (actionDTO instanceof DynamicActionDTO && checkActionWithSameCommandRunning(actionDTO)) {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    } else if (!(actionDTO instanceof DynamicActionDTO) && checkActionRunning(actionDTO.getId())) {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
    try {
      confirmed = setInitialConfirmationStatus(actionDTO);
      actionExecutorService.registerAction(actionDTO, triggerType, confirmed);
    } catch (Exception e) {
      logger.error(e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    return confirmed
        ? ResponseEntity.ok().build()
        : ResponseEntity.status(HttpStatus.CREATED).build();
  }

  public boolean checkActionWithSameCommandRunning(ActionDTO newActionDTO) {
    ActionDTO actionDTORunning;
    try {
      actionDTORunning =
          (ActionDTO)
              actionExecutorService
                  .getActionProcessDataShared()
                  .get(getRunningId(newActionDTO))
                  .get("actionDTO");
    } catch (Exception ignored) {
      return false;
    }
    return actionDTORunning.getCommand().equals(newActionDTO.getCommand());
  }

  public String getRunningId(ActionDTO actionDTO) {
    String id;
    try {
      byte[] bytesOfMessage = null;

      bytesOfMessage =
          (actionDTO.getId() + actionDTO.getCommand()).getBytes(StandardCharsets.UTF_8);
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] md5Digest = md.digest(bytesOfMessage);
      StringBuilder hexString = new StringBuilder();
      for (byte b : md5Digest) {
        String hex = Integer.toHexString(0xFF & b);
        if (hex.length() == 1) {
          hexString.append('0'); // Add leading zero if needed
        }
        hexString.append(hex);
      }
      id = hexString.toString();
    } catch (NoSuchAlgorithmException e) {
      id = actionDTO.getId() + actionDTO.getCommand();
    }
    return id;
  }

  public List<DynamicActionDTO> getAllRunningDynamicActions() {
    List<DynamicActionDTO> runningDynamicActions = new java.util.ArrayList<>(List.of());
    List<HashMap> runningDynamicActionData = new java.util.ArrayList<>(List.of());
    try {
      Thread.sleep(500);
    } catch (InterruptedException ignore) {
    }
    actionExecutorService
        .getActionProcessDataShared()
        .forEach(
            (id, actionData) -> {
              // Only if action is confirmed count as running
              if ((boolean) actionData.get("confirmed")) {
                ActionDTO actionDTO = (ActionDTO) actionData.get("actionDTO");
                if (dynamicActionService.checkActionDTOisDynamicActionDTO(actionDTO)) {
                  DynamicActionDTO dynamicActionDTO =
                      (DynamicActionDTO) actionData.get("actionDTO");
                  dynamicActionDTO.setRunningActionId(id);
                  if (runningDynamicActionData.isEmpty()
                      || ((LocalDateTime) runningDynamicActionData.getLast().get("triggeredTime"))
                          .isBefore((LocalDateTime) actionData.get("triggeredTime"))) {
                    runningDynamicActions.add(dynamicActionDTO);
                    runningDynamicActionData.add(actionData);
                  } else {
                    HashMap tmpActionData = runningDynamicActionData.removeLast();
                    DynamicActionDTO tmpDynamicActionDTO = runningDynamicActions.removeLast();
                    runningDynamicActions.add(dynamicActionDTO);
                    runningDynamicActionData.add(actionData);
                    runningDynamicActions.add(tmpDynamicActionDTO);
                    runningDynamicActionData.add(tmpActionData);
                  }
                }
              }
            });
    return runningDynamicActions;
  }

  public List<ActionDTO> getAllRunningActions() {
    List<ActionDTO> runningActions = new java.util.ArrayList<>(List.of());
    List<HashMap> runningActionData = new java.util.ArrayList<>(List.of());
    actionExecutorService
        .getActionProcessDataShared()
        .forEach(
            (id, actionData) -> {
              // Only if action is confirmed count as running
              if ((boolean) actionData.get("confirmed")) {
                ActionDTO actionDTO = (ActionDTO) actionData.get("actionDTO");
                if (!dynamicActionService.checkActionDTOisDynamicActionDTO(actionDTO)) {
                  actionDTO = (ActionDTO) actionData.get("actionDTO");
                  if (runningActionData.isEmpty()
                      || ((LocalDateTime) runningActionData.getLast().get("triggeredTime"))
                          .isBefore((LocalDateTime) actionData.get("triggeredTime"))) {
                    runningActions.add(actionDTO);
                    runningActionData.add(actionData);
                  } else {
                    HashMap tmpActionData = runningActionData.removeLast();
                    ActionDTO tmpActionDTO = runningActions.removeLast();
                    runningActions.add(actionDTO);
                    runningActionData.add(actionData);
                    runningActions.add(tmpActionDTO);
                    runningActionData.add(tmpActionData);
                  }
                }
              }
            });
    return runningActions;
  }
}
