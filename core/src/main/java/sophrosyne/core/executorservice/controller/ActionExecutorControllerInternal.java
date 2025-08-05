package sophrosyne.core.executorservice.controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import sophrosyne.core.actionarchiveservice.dto.ActionArchiveDTO;
import sophrosyne.core.actionservice.dto.ActionDTO;
import sophrosyne.core.actionservice.service.ActionService;
import sophrosyne.core.dynamicactionservice.dto.DynamicActionDTO;
import sophrosyne.core.dynamicactionservice.service.DynamicActionService;
import sophrosyne.core.executorservice.service.ActionExecutorService;
import sophrosyne.core.executorservice.utils.Utils;
import sophrosyne_api.core.internalexecutorservice.api.IntApi;
import sophrosyne_api.core.internalexecutorservice.api.PullApi;
import sophrosyne_api.core.internalexecutorservice.model.Action;
import sophrosyne_api.core.internalexecutorservice.model.ActionStatusInfo;
import sophrosyne_api.core.internalexecutorservice.model.DynamicAction;

@RestController
public class ActionExecutorControllerInternal implements IntApi, PullApi {

  private final Logger logger = LogManager.getLogger(getClass());

  @Autowired private ActionExecutorService actionExecutorService;

  @Autowired private ActionService actionService;

  @Autowired private DynamicActionService dynamicActionService;

  @Autowired private Utils utils;

  @Override
  public Optional<NativeWebRequest> getRequest() {
    return IntApi.super.getRequest();
  }

  @Override
  public ResponseEntity<Void> executeAction(String id) {
    ActionDTO actionDTO;
    try {
      actionDTO = actionService.getActionDTOByID(id).get();
    } catch (NoSuchElementException e) {
      logger.error(e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    return utils.executeActionHelper(actionDTO, ActionArchiveDTO.TYPES.INTERNAL.name());
  }

  @Override
  public ResponseEntity<Void> stopAction(String id) {
    return utils.stopActionHelper(id);
  }

  @Override
  public ResponseEntity<ActionStatusInfo> getStatusOfAction(String id) {
    boolean isRunning = false;
    try {
      isRunning = utils.checkActionRunning(id);
    } catch (Exception e) {
      logger.error(e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    return ResponseEntity.ok(new ActionStatusInfo().isRunning(isRunning));
  }

  @Override
  public ResponseEntity<Void> executeDynamicAction(String id, Object body) {
    DynamicActionDTO dynamicActionDTO;
    try {
      dynamicActionDTO = dynamicActionService.getDynamicActionDTOByID(id).get();
    } catch (NoSuchElementException e) {
      logger.error(e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    try {
      dynamicActionDTO.setCommand(
          dynamicActionService.createDynamicCommand(dynamicActionDTO, body));
    } catch (Exception e) {
      logger.error(e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    return utils.executeActionHelper(dynamicActionDTO, ActionArchiveDTO.TYPES.INTERNAL.name());
  }

  @Override
  public ResponseEntity<Void> stopDynamicAction(String id) {
    return utils.stopActionHelper(id);
  }

  @Override
  public ResponseEntity<List<DynamicAction>> getAllCurrentRunningDynamicActionsById(String id) {
    List<DynamicActionDTO> runningDynamicActions = utils.getAllRunningDynamicActions();
    return ResponseEntity.ok(
        runningDynamicActions.stream()
            .filter(dynamicActionDTO -> dynamicActionDTO.getId().equals(id))
            .map(
                dynamicActionDTO -> {
                  return dynamicActionService.mapDynamicActionDTOToDynamicAction(dynamicActionDTO);
                })
            .toList());
  }

  @Override
  public ResponseEntity<List<DynamicAction>> getAllCurrentRunningDynamicActions() {
    List<DynamicActionDTO> runningDynamicActions = utils.getAllRunningDynamicActions();
    return ResponseEntity.ok(
        runningDynamicActions.stream()
            .map(
                dynamicActionDTO -> {
                  return dynamicActionService.mapDynamicActionDTOToDynamicAction(dynamicActionDTO);
                })
            .toList());
  }

  @Override
  public ResponseEntity<List<Action>> getAllCurrentRunningActions() {
    List<ActionDTO> runningActions = utils.getAllRunningActions();
    return ResponseEntity.ok(
        runningActions.stream()
            .map(
                actionDTO -> {
                  return actionService.mapActionDTOToExecutorServiceAction(actionDTO);
                })
            .toList());
  }
}
