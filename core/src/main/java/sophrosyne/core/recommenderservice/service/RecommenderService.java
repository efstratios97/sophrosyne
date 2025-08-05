package sophrosyne.core.recommenderservice.service;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.Meter;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sophrosyne.core.actionrecommendationservice.dto.ActionRecommendationDTO;
import sophrosyne.core.actionrecommendationservice.service.ActionRecommendationService;
import sophrosyne.core.commonservice.CommonAPIService;
import sophrosyne.core.metricsservice.service.MetricsService;

@Service
public class RecommenderService {

  private final Logger logger = LogManager.getLogger(getClass());

  public List<String> activeActionRecommendationDTOs = new ArrayList<>();
  @Autowired CommonAPIService commonAPIService;
  @Autowired MetricsService metricsService;

  @Autowired private ActionRecommendationService actionRecommendationService;

  public void activateRecommendation(ActionRecommendationDTO actionRecommendationDTO) {
    if (!activeActionRecommendationDTOs.contains(actionRecommendationDTO.getId())) {
      activeActionRecommendationDTOs.add(actionRecommendationDTO.getId());
    }
  }

  public void deactivateRecommendation(ActionRecommendationDTO actionRecommendationDTO) {
    activeActionRecommendationDTOs.remove(actionRecommendationDTO.getId());
  }

  public List<ActionRecommendationDTO> getActiveActionRecommendationDTOs() {
    cleanUpActiveRecommendations();
    return activeActionRecommendationDTOs.stream()
        .map(
            activeActionRecommendationID -> {
              return actionRecommendationService.getActionRecommendationByID(
                  activeActionRecommendationID);
            })
        .filter(Optional::isPresent)
        .map(Optional::get)
        .filter(
            actionRecommendationDTO ->
                activeActionRecommendationDTOs.contains(actionRecommendationDTO.getId()))
        .toList();
  }

  public List<ActionRecommendationDTO> getActiveActionRecommendationDTOsByApikey(String apikey) {
    return getActiveActionRecommendationDTOs().stream()
        .filter(
            actionRecommendationDTO ->
                commonAPIService.checkValidApikeyActionRecommendation(
                    actionRecommendationDTO, apikey))
        .toList();
  }

  public long getAmountActiveActionRecommendationDTOs() {
    cleanUpActiveRecommendations();
    return activeActionRecommendationDTOs.size();
  }

  public void cleanUpActiveRecommendations() {
    if (actionRecommendationService.getActionRecommendations().isEmpty()) {
      activeActionRecommendationDTOs = new ArrayList<>();
    } else {
      activeActionRecommendationDTOs.removeIf(
          actionRecommendationId ->
              actionRecommendationService.getActionRecommendations().stream()
                  .filter(
                      actionRecommendationDTO ->
                          actionRecommendationDTO.getId().equals(actionRecommendationId))
                  .toList()
                  .isEmpty());
    }
  }

  @PostConstruct
  private void registerMetrics() {
    try {
      Meter meter = metricsService.getSophrosyneStandardJobMeter();

      meter
          .gaugeBuilder("sophrosyne.amount_of_active_action_recommendations")
          .setDescription("Amount of active Action Recommendations")
          .buildWithCallback(
              result ->
                  result.record(getAmountActiveActionRecommendationDTOs(), Attributes.empty()));

      meter
          .gaugeBuilder("sophrosyne.active_action_recommendations")
          .setDescription("Info regarding active Action Recommendations requiring acknowledgement")
          .buildWithCallback(
              result ->
                  getActiveActionRecommendationDTOs()
                      .forEach(
                          actionRecommendationDTO -> {
                            result.record(
                                1, getActionRecommendationAttributes(actionRecommendationDTO));
                          }));
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  private Attributes getActionRecommendationAttributes(
      ActionRecommendationDTO actionRecommendationDTO) {
    return Attributes.builder()
        .put("id", actionRecommendationDTO.getId())
        .put("name", actionRecommendationDTO.getName())
        .put("description", actionRecommendationDTO.getDescription())
        .put("contact_information", actionRecommendationDTO.getContactInformation())
        .put("responsible_entity", actionRecommendationDTO.getResponsibleEntity())
        .build();
  }
}
