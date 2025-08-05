package sophrosyne.core.actionrecommendationservice.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sophrosyne.core.actionrecommendationservice.dto.ActionRecommendationDTO;
import sophrosyne.core.actionrecommendationservice.service.ActionRecommendationService;
import sophrosyne_api.core.actionrecommendationservice.api.IntApi;
import sophrosyne_api.core.actionrecommendationservice.model.ActionRecommendation;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
public class ActionRecommendationController implements IntApi {

    private final Logger logger = LogManager.getLogger(getClass());

    @Autowired
    private ActionRecommendationService actionRecommendationService;

    public ResponseEntity<Void> createActionRecommendation(
            ActionRecommendation actionRecommendation) {
        actionRecommendationService.createActionRecommendation(actionRecommendation);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<Void> updateActionRecommendation(
            String id, ActionRecommendation actionRecommendation) {
        try {
            actionRecommendationService.updateActionRecommendation(actionRecommendation);
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<ActionRecommendation> getActionRecommendationByActionId(String id) {
        ActionRecommendationDTO actionRecommendationDTO;
        try {
            actionRecommendationDTO = actionRecommendationService.getActionRecommendationByID(id).get();
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(
                actionRecommendationService.mapActionRecommendationDTOToActionRecommendation(
                        actionRecommendationDTO));
    }

    public ResponseEntity<List<ActionRecommendation>> getActionRecommendations() {
        return ResponseEntity.ok(
                actionRecommendationService.getActionRecommendations().stream()
                        .map(
                                actionRecommendationDTO -> {
                                    return actionRecommendationService
                                            .mapActionRecommendationDTOToActionRecommendation(actionRecommendationDTO);
                                })
                        .toList());
    }

    public ResponseEntity<Void> deleteActionRecommendation(String id) {
        ActionRecommendationDTO actionRecommendationDTO;
        try {
            actionRecommendationDTO = actionRecommendationService.getActionRecommendationByID(id).get();
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        actionRecommendationService.deleteActionRecommendation(actionRecommendationDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
