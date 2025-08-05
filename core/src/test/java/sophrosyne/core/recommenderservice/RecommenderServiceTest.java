package sophrosyne.core.recommenderservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import sophrosyne.core.PostgresIntegrationTestBase;
import sophrosyne.core.actionrecommendationservice.dto.ActionRecommendationDTO;
import sophrosyne.core.actionrecommendationservice.service.ActionRecommendationService;
import sophrosyne.core.recommenderservice.service.RecommenderService;
import sophrosyne_api.core.actionrecommendationservice.model.ActionRecommendation;

@SpringBootTest
@TestPropertySource(locations = "/application-test.properties")
public class RecommenderServiceTest extends PostgresIntegrationTestBase {

  @Autowired RecommenderService recommenderService;

  @Autowired ActionRecommendationService actionRecommendationService;

  @AfterEach
  @BeforeEach
  public void cleanUp() {
    actionRecommendationService.deleteAllActionRecommendations();
    recommenderService.cleanUpActiveRecommendations();
  }

  @Test
  public void test_activateRecommendation() {
    ActionRecommendationDTO actionRecommendationDTO =
        actionRecommendationService.createActionRecommendation(
            generateActionRecommendation("test"));
    recommenderService.activateRecommendation(actionRecommendationDTO);
    assertThat(recommenderService.activeActionRecommendationDTOs)
        .hasSize(1)
        .contains(actionRecommendationDTO.getId());
  }

  @Test
  public void test_deactivateRecommendation() {
    ActionRecommendationDTO actionRecommendationDTO =
        actionRecommendationService.createActionRecommendation(
            generateActionRecommendation("test"));
    recommenderService.activateRecommendation(actionRecommendationDTO);

    recommenderService.deactivateRecommendation(actionRecommendationDTO);
    assertThat(recommenderService.activeActionRecommendationDTOs).hasSize(0).isEmpty();
  }

  @Test
  public void test_getAmountActiveActionRecommendationDTOs() {
    ActionRecommendationDTO actionRecommendationDTO =
        actionRecommendationService.createActionRecommendation(
            generateActionRecommendation("test"));
    recommenderService.activateRecommendation(actionRecommendationDTO);
    ActionRecommendationDTO actionRecommendationDTO2 =
        actionRecommendationService.createActionRecommendation(
            generateActionRecommendation("test2"));
    recommenderService.activateRecommendation(actionRecommendationDTO2);
    // Not activated
    actionRecommendationService.createActionRecommendation(generateActionRecommendation("test3"));
    // Activated and Deactivated
    ActionRecommendationDTO actionRecommendationDTO4 =
        actionRecommendationService.createActionRecommendation(
            generateActionRecommendation("test4"));
    recommenderService.activateRecommendation(actionRecommendationDTO4);
    recommenderService.deactivateRecommendation(actionRecommendationDTO4);

    assertThat(recommenderService.activeActionRecommendationDTOs).hasSize(2).isNotEmpty();
  }

  @Test
  public void test_getActiveActionRecommendationDTOs() {
    ActionRecommendationDTO actionRecommendationDTO =
        actionRecommendationService.createActionRecommendation(
            generateActionRecommendation("test"));
    recommenderService.activateRecommendation(actionRecommendationDTO);

    ActionRecommendationDTO actionRecommendationDTO2 =
        actionRecommendationService.createActionRecommendation(
            generateActionRecommendation("test2"));
    recommenderService.activateRecommendation(actionRecommendationDTO2);

    assertThat(recommenderService.getActiveActionRecommendationDTOs())
        .hasSize(2)
        .isNotEmpty()
        .containsAll(
            new ArrayList<>() {
              {
                add(actionRecommendationDTO);
                add(actionRecommendationDTO2);
              }
            });
  }

  @Test
  public void test_cleanUpActiveRecommendations() {
    ActionRecommendationDTO actionRecommendationDTO =
        actionRecommendationService.createActionRecommendation(
            generateActionRecommendation("test"));
    recommenderService.activateRecommendation(actionRecommendationDTO);

    ActionRecommendationDTO actionRecommendationDTO2 =
        actionRecommendationService.createActionRecommendation(
            generateActionRecommendation("test2"));
    recommenderService.activateRecommendation(actionRecommendationDTO2);

    recommenderService.cleanUpActiveRecommendations();
    assertThat(recommenderService.activeActionRecommendationDTOs).hasSize(2).isNotEmpty();

    actionRecommendationService.deleteAllActionRecommendations();
    recommenderService.cleanUpActiveRecommendations();
    assertThat(recommenderService.activeActionRecommendationDTOs).hasSize(0).isEmpty();
  }

  private ActionRecommendation generateActionRecommendation(String name) {
    return new ActionRecommendation()
        .name(name)
        .description("Testing")
        .responsibleEntity("Me")
        .contactInformation("Too")
        .allowedApikeys(new ArrayList<>())
        .additionalDocumentation(null);
  }
}
