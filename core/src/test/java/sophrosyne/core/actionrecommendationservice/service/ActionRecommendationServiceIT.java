package sophrosyne.core.actionrecommendationservice.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import sophrosyne.core.PostgresIntegrationTestBase;
import sophrosyne.core.actionrecommendationservice.dto.ActionRecommendationDTO;
import sophrosyne.core.apikeyservice.dto.ApikeyDTO;
import sophrosyne.core.apikeyservice.service.ApikeyService;
import sophrosyne_api.core.actionrecommendationservice.model.ActionRecommendation;

@SpringBootTest
@TestPropertySource(locations = "/application-test.properties")
public class ActionRecommendationServiceIT extends PostgresIntegrationTestBase {

  @Autowired ActionRecommendationService sut_actionRecommendationService;

  @Autowired ApikeyService apikeyService;

  private ApikeyDTO apikeyDTO;

  @Value("${test.actionrecommendation.filepath}")
  private String testFile;

  @BeforeEach
  public void generateApikey() {
    apikeyService.deleteAllApikeys();
    sut_actionRecommendationService.deleteAllActionRecommendations();
    apikeyDTO = apikeyService.generateAPIKey("apikey_fictional", "test", 1);
  }

  @AfterEach
  public void cleanUp() {
    apikeyService.deleteAllApikeys();
    sut_actionRecommendationService.deleteAllActionRecommendations();
  }

  @Test
  public void test_createActionRecommendation() {
    ActionRecommendationDTO actionRecommendationDTO =
        sut_actionRecommendationService.createActionRecommendation(
            generateActionRecommendation("Test"));

    assertThat(
            sut_actionRecommendationService
                .getActionRecommendationByID(actionRecommendationDTO.getId())
                .get())
        .isEqualTo(actionRecommendationDTO);
    assertThat(sut_actionRecommendationService.getActionRecommendations())
        .hasSize(1)
        .contains(actionRecommendationDTO);
  }

  @Test
  public void test_getActionRecommendationById() {
    ActionRecommendationDTO actionRecommendationDTO =
        sut_actionRecommendationService.createActionRecommendation(
            generateActionRecommendation("Test"));

    ActionRecommendationDTO actionRecommendationDTO2 =
        sut_actionRecommendationService.createActionRecommendation(
            generateActionRecommendation("Test2"));

    assertThat(
            sut_actionRecommendationService
                .getActionRecommendationByID(actionRecommendationDTO.getId())
                .get())
        .isEqualTo(actionRecommendationDTO);
    assertThat(
            sut_actionRecommendationService
                .getActionRecommendationByID(actionRecommendationDTO2.getId())
                .get())
        .isEqualTo(actionRecommendationDTO2);
  }

  @Test
  public void test_getActionRecommendations() {
    ActionRecommendationDTO actionRecommendationDTO =
        sut_actionRecommendationService.createActionRecommendation(
            generateActionRecommendation("Test"));
    ActionRecommendationDTO actionRecommendationDTO2 =
        sut_actionRecommendationService.createActionRecommendation(
            generateActionRecommendation("Test2"));

    assertThat(sut_actionRecommendationService.getActionRecommendations())
        .hasSize(2)
        .contains(actionRecommendationDTO, actionRecommendationDTO2);
  }

  private ActionRecommendation generateActionRecommendation(String name) {

    return new ActionRecommendation()
        .name(name)
        .description("Testing")
        .responsibleEntity("Me")
        .contactInformation("Too")
        .addAllowedApikeysItem("apikey_fictional")
        .additionalDocumentation(testFile.getBytes(StandardCharsets.UTF_8));
  }
}
