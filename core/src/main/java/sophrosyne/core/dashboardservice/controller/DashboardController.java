package sophrosyne.core.dashboardservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sophrosyne.core.actionarchiveservice.service.ActionArchiveService;
import sophrosyne.core.actionconfirmationservice.service.ActionConfirmationService;
import sophrosyne.core.executorservice.service.ActionExecutorService;
import sophrosyne.core.recommenderservice.service.RecommenderService;
import sophrosyne_api.core.dashboardstatistics.api.PullApi;
import sophrosyne_api.core.dashboardstatistics.model.Stats;

@RestController
public class DashboardController implements PullApi {

  @Autowired ActionConfirmationService actionConfirmationService;
  @Autowired ActionExecutorService actionExecutorService;
  @Autowired ActionArchiveService actionArchiveService;
  @Autowired RecommenderService recommenderService;

  @Override
  public ResponseEntity<Stats> getAllStats() {
    Stats stats = new Stats();
    stats.setActiveRecommendations(recommenderService.getAmountActiveActionRecommendationDTOs());
    stats = actionExecutorService.countRunningActions(stats);
    stats = actionConfirmationService.countRequiringConfirmation(stats);
    stats = actionArchiveService.getAllStats(stats);
    return ResponseEntity.ok(stats);
  }
}
