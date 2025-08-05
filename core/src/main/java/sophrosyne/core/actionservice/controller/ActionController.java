package sophrosyne.core.actionservice.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sophrosyne.core.actionservice.dto.ActionDTO;
import sophrosyne.core.actionservice.service.ActionService;
import sophrosyne_api.core.actionservice.api.IntApi;
import sophrosyne_api.core.actionservice.model.Action;

import java.util.List;
import java.util.Optional;

@RestController
public class ActionController implements IntApi {

    private final Logger logger = LogManager.getLogger(getClass());

    @Autowired
    private ActionService actionService;

    @Override
    public ResponseEntity<Void> createAction(Action action) {
        try {
            actionService.createAction(action);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Action> getActionById(String id) {
        Optional<ActionDTO> actionDTO =
                actionService.getActionDTO(id);
        return actionDTO
                .map(dto -> ResponseEntity.ok(actionService.mapActionDTOToAction(dto)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Override
    public ResponseEntity<List<Action>> getActions() {
        try {
            List<Action> actions =
                    actionService.getActions().stream()
                            .map(
                                    actionDTO -> {
                                        return actionService.mapActionDTOToAction(actionDTO);
                                    })
                            .toList();
            return ResponseEntity.ok(actions);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<Void> updateAction(String id, Action newAction) {
        Optional<ActionDTO> actionDTO =
                actionService.getActionDTO(id);
        if (actionDTO.isPresent()) {
            actionService.updateAction(actionService.mapActionToActionDTO(newAction, actionDTO.get()));
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Override
    public ResponseEntity<Void> deleteAction(String id) {
        Optional<ActionDTO> actionDTO =
                actionService.getActionDTO(id);
        if (actionDTO.isPresent()) {
            actionService.deleteAction(actionDTO.get());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
