package sophrosyne.core.recommenderservice.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import sophrosyne.core.actionrecommendationservice.dto.ActionRecommendationDTO;
import sophrosyne.core.actionrecommendationservice.service.ActionRecommendationService;
import sophrosyne.core.recommenderservice.service.RecommenderService;
import sophrosyne_api.core.actionrecommendationservice.model.ActionRecommendation;
import sophrosyne_api.core.actionrecommendationservice.model.ActionRecommendationStatusInfo;
import sophrosyne_api.core.internalactionrecommendationservice.api.IntApi;
import sophrosyne_api.core.internalactionrecommendationservice.api.PullApi;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
public class InternalRecommenderController implements IntApi, PullApi {

    private final Logger logger = LogManager.getLogger(getClass());

    @Autowired
    private ActionRecommendationService
            actionRecommendationService;

    @Autowired
    private RecommenderService recommenderService;

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return IntApi.super.getRequest();
    }

    @Override
    public ResponseEntity<ActionRecommendationStatusInfo> getStatusOfActionRecommendation(String id) {
        ActionRecommendationDTO actionRecommendationDTO;
        try {
            actionRecommendationDTO = actionRecommendationService.getActionRecommendationByID(id).get();
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(
                new ActionRecommendationStatusInfo()
                        .isActive(
                                recommenderService.activeActionRecommendationDTOs.contains(
                                        actionRecommendationDTO.getId())));
    }

    public ResponseEntity<Void> deactivateActionRecommendationById(String id) {
        ActionRecommendationDTO actionRecommendationDTO;
        try {
            actionRecommendationDTO = actionRecommendationService.getActionRecommendationByID(id).get();
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        recommenderService.deactivateRecommendation(actionRecommendationDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<List<ActionRecommendation>> getActiveActionRecommendations() {
        return ResponseEntity.ok(
                recommenderService.getActiveActionRecommendationDTOs()
                        .stream().map(actionRecommendationDTO ->
                                actionRecommendationService.mapActionRecommendationDTOToActionRecommendation(actionRecommendationDTO)).toList());
    }
}
