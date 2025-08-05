package sophrosyne.core.executorservice.controller;

import java.util.LinkedHashMap;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sophrosyne.core.actionarchiveservice.dto.ActionArchiveDTO;
import sophrosyne.core.actionservice.dto.ActionDTO;
import sophrosyne.core.actionservice.service.ActionService;
import sophrosyne.core.commonservice.CommonAPIService;
import sophrosyne.core.dynamicactionservice.dto.DynamicActionDTO;
import sophrosyne.core.dynamicactionservice.service.DynamicActionService;
import sophrosyne.core.executorservice.service.ActionExecutorService;
import sophrosyne.core.executorservice.utils.Utils;
import sophrosyne_api.core.externalexecutorservice.api.ApiApi;

@RestController
public class ActionExecutorControllerExternal implements ApiApi {
  private final Logger logger = LogManager.getLogger(getClass());

  @Autowired private ActionExecutorService actionExecutorService;

  @Autowired private ActionService actionService;

  @Autowired private DynamicActionService dynamicActionService;

  @Autowired private CommonAPIService commonAPIService;

  @Autowired private Utils utils;

  @Override
  public ResponseEntity<Void> executeAction(String id, String apikey) {
    ActionDTO actionDTO;
    try {
      actionDTO = actionService.getActionDTOByID(id).get();
    } catch (NoSuchElementException e) {
      logger.error(e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    return commonAPIService.checkValidApikeyAction(actionDTO, apikey)
        ? utils.executeActionHelper(actionDTO, ActionArchiveDTO.TYPES.EXTERNAL.name())
        : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }

  @Override
  public ResponseEntity<Void> stopAction(String id, String apikey) {
    ActionDTO actionDTO;
    try {
      actionDTO = actionService.getActionDTOByID(id).get();
    } catch (NoSuchElementException e) {
      logger.error(e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    return commonAPIService.checkValidApikeyAction(actionDTO, apikey)
        ? utils.stopActionHelper(id)
        : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }

  @Override
  public ResponseEntity<Void> executeDynamicAction(
      String id, String parameters, String apikey, Object body) {
    DynamicActionDTO dynamicActionDTO;
    try {
      dynamicActionDTO = dynamicActionService.getDynamicActionDTOByID(id).get();
    } catch (NoSuchElementException e) {
      logger.error(e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    try {
      dynamicActionDTO.setCommand(
          dynamicActionService.createDynamicCommand(
              dynamicActionDTO, parameters, Optional.of((LinkedHashMap) body)));
    } catch (Exception e) {
      logger.error(e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    return commonAPIService.checkValidApikeyAction(dynamicActionDTO, apikey)
        ? utils.executeActionHelper(dynamicActionDTO, ActionArchiveDTO.TYPES.EXTERNAL.name())
        : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }

  @Override
  public ResponseEntity<Void> executeDynamicActionParamsInBody(
      String id, String apikey, Object body) {
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
    return commonAPIService.checkValidApikeyAction(dynamicActionDTO, apikey)
        ? utils.executeActionHelper(dynamicActionDTO, ActionArchiveDTO.TYPES.EXTERNAL.name())
        : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }

  @Override
  public ResponseEntity<Void> stopDynamicAction(String id, String runningId, String apikey) {
    DynamicActionDTO dynamicActionDTO;
    try {
      dynamicActionDTO = dynamicActionService.getDynamicActionDTO(id).get();
    } catch (NoSuchElementException e) {
      logger.error(e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    return commonAPIService.checkValidApikeyAction(dynamicActionDTO, apikey)
        ? utils.stopActionHelper(runningId)
        : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }

  @Override
  public ResponseEntity<Void> muteAction(String id, String apikey) {
    Optional<ActionDTO> actionDTO = actionService.getActionDTOByID(id);
    if (actionDTO.isPresent() && actionDTO.get() instanceof DynamicActionDTO dynamicActionDTO1) {
      dynamicActionDTO1.setMuted(1);
      dynamicActionService.updateDynamicAction(dynamicActionDTO1);
      return ResponseEntity.ok().build();
    } else if (actionDTO.isPresent()) {
      ActionDTO actionDTO1 = actionDTO.get();
      actionDTO1.setMuted(1);
      actionService.updateAction(actionDTO1);
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

  @Override
  public ResponseEntity<Void> unmuteAction(String id, String apikey) {
    Optional<ActionDTO> actionDTO = actionService.getActionDTOByID(id);
    if (actionDTO.isPresent() && actionDTO.get() instanceof DynamicActionDTO dynamicActionDTO1) {
      dynamicActionDTO1.setMuted(0);
      dynamicActionService.updateDynamicAction(dynamicActionDTO1);
      return ResponseEntity.ok().build();
    } else if (actionDTO.isPresent()) {
      ActionDTO actionDTO1 = actionDTO.get();
      actionDTO1.setMuted(0);
      actionService.updateAction(actionDTO1);
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }
}
