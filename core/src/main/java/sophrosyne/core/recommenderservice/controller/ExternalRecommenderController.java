package sophrosyne.core.recommenderservice.controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sophrosyne.core.actionrecommendationservice.dto.ActionRecommendationDTO;
import sophrosyne.core.actionrecommendationservice.service.ActionRecommendationService;
import sophrosyne.core.commonservice.CommonAPIService;
import sophrosyne.core.recommenderservice.service.RecommenderService;
import sophrosyne_api.core.actionrecommendationservice.model.ActionRecommendation;
import sophrosyne_api.core.externalactionrecommendationservice.api.ApiApi;

@RestController
public class ExternalRecommenderController implements ApiApi {

  private final Logger logger = LogManager.getLogger(getClass());

  @Autowired ActionRecommendationService actionRecommendationService;

  @Autowired RecommenderService recommenderService;

  @Autowired CommonAPIService commonAPIService;

  public ResponseEntity<Void> triggerActionRecommendationById(String id, String apikey) {
    ActionRecommendationDTO actionRecommendationDTO;
    try {
      actionRecommendationDTO = actionRecommendationService.getActionRecommendationByID(id).get();
    } catch (NoSuchElementException e) {
      logger.error(e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    if (!commonAPIService.checkValidApikeyActionRecommendation(actionRecommendationDTO, apikey)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    recommenderService.activateRecommendation(actionRecommendationDTO);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Override
  public ResponseEntity<Void> stopActionRecommendationById(String id, String apikey) {
    ActionRecommendationDTO actionRecommendationDTO;
    try {
      actionRecommendationDTO = actionRecommendationService.getActionRecommendationByID(id).get();
    } catch (NoSuchElementException e) {
      logger.error(e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    if (!commonAPIService.checkValidApikeyActionRecommendation(actionRecommendationDTO, apikey)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    recommenderService.deactivateRecommendation(actionRecommendationDTO);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Override
  public ResponseEntity<List<ActionRecommendation>> getAllActionRecommendationsByApikey(
      String apikey) {
    return ResponseEntity.ok(
        recommenderService.getActiveActionRecommendationDTOsByApikey(apikey).stream()
            .map(
                actionRecommendationDTO ->
                    actionRecommendationService.mapActionRecommendationDTOToActionRecommendation(
                        actionRecommendationDTO))
            .toList());
  }

  @Override
  public ResponseEntity<ActionRecommendation> getActionRecommendationById(
      String id, String apikey) {
    Optional<ActionRecommendationDTO> potentialActionRecommendationDTO =
        actionRecommendationService.getActionRecommendationByID(id);
    if (potentialActionRecommendationDTO.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.valueOf(404));
    }
    Optional<ActionRecommendationDTO> actionRecommendationDTO =
        recommenderService.getActiveActionRecommendationDTOs().stream()
            .filter(
                activeActionRecommendationDTO ->
                    potentialActionRecommendationDTO.get() == activeActionRecommendationDTO)
            .findAny();
    return actionRecommendationDTO
        .map(
            recommendationDTO ->
                ResponseEntity.ok(
                    actionRecommendationService.mapActionRecommendationDTOToActionRecommendation(
                        recommendationDTO)))
        .orElseGet(() -> ResponseEntity.status(204).build());
  }
}
