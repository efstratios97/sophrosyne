package sophrosyne.core.dynamicactionservice.controller;

import java.util.List;
import java.util.Optional;
import sophrosyne.core.dynamicactionservice.dto.DynamicActionDTO;
import sophrosyne.core.dynamicactionservice.service.DynamicActionService;
import sophrosyne_api.core.dynamicactionservice.api.IntApi;
import sophrosyne_api.core.dynamicactionservice.model.DynamicAction;
import sophrosyne_api.core.dynamicactionservice.model.ParsedDynamicParameters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DynamicActionController implements IntApi {

  private final Logger logger = LogManager.getLogger(getClass());

  @Autowired private DynamicActionService dynamicActionService;

  @Override
  public ResponseEntity<Void> createDynamicAction(DynamicAction action) {
    try {
      dynamicActionService.createDynamicAction(action);
    } catch (Exception e) {
      logger.error(e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<DynamicAction> getDynamicActionById(String id) {
    Optional<DynamicActionDTO> dynamicActionDTO = dynamicActionService.getDynamicActionDTO(id);
    return dynamicActionDTO
        .map(
            dto ->
                ResponseEntity.ok(
                    dynamicActionService.mapDynamicActionDTOToDynamicActionService(dto)))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  @Override
  public ResponseEntity<List<DynamicAction>> getDynamicActions() {
    try {
      List<DynamicAction> actions =
          dynamicActionService.getDynamicActions().stream()
              .map(
                  dynamicActionDTO -> {
                    return dynamicActionService.mapDynamicActionDTOToDynamicActionService(
                        dynamicActionDTO);
                  })
              .toList();
      return ResponseEntity.ok(actions);
    } catch (Exception e) {
      logger.error(e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @Override
  public ResponseEntity<Void> updateDynamicAction(String id, DynamicAction dynamicAction) {
    Optional<DynamicActionDTO> dynamicActionDTO = dynamicActionService.getDynamicActionDTO(id);
    if (dynamicActionDTO.isPresent()) {
      dynamicActionService.updateDynamicAction(
          dynamicActionService.mapDynamicActionToDynamicActionDTO(
              dynamicAction, dynamicActionDTO.get()));
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  @Override
  public ResponseEntity<Void> deleteDynamicAction(String id) {
    Optional<DynamicActionDTO> dynamicActionDTO = dynamicActionService.getDynamicActionDTO(id);
    if (dynamicActionDTO.isPresent()) {
      dynamicActionService.deleteDynamicAction(dynamicActionDTO.get());
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  @Override
  public ResponseEntity<ParsedDynamicParameters> getDynamicActionParsedParameters(String id) {
    Optional<DynamicActionDTO> dynamicActionDTO = dynamicActionService.getDynamicActionDTO(id);
    if (dynamicActionDTO.isPresent()) {
      ParsedDynamicParameters parsedDynamicParameters =
          new ParsedDynamicParameters()
              .parameters(dynamicActionService.getParsedDynamicParameters(dynamicActionDTO.get()));
      return ResponseEntity.ok(parsedDynamicParameters);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  @Override
  public ResponseEntity<String> getDynamicActionCommandPreview(
      String id, String dynamicParametersStringPreview) {
    Optional<DynamicActionDTO> dynamicActionDTO = dynamicActionService.getDynamicActionDTO(id);
    return dynamicActionDTO
        .map(
            actionDTO ->
                ResponseEntity.ok(
                    dynamicActionService.createDynamicCommand(
                        actionDTO, dynamicParametersStringPreview, Optional.empty())))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }
}
