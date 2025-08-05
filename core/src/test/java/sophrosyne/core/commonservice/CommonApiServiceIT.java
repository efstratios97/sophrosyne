package sophrosyne.core.commonservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import sophrosyne.core.PostgresIntegrationTestBase;
import sophrosyne.core.actionrecommendationservice.dto.ActionRecommendationDTO;
import sophrosyne.core.actionrecommendationservice.service.ActionRecommendationService;
import sophrosyne.core.actionservice.dto.ActionDTO;
import sophrosyne.core.actionservice.service.ActionService;
import sophrosyne.core.apikeyservice.dto.ApikeyDTO;
import sophrosyne.core.apikeyservice.service.ApikeyService;
import sophrosyne.core.dynamicactionservice.dto.DynamicActionDTO;
import sophrosyne.core.dynamicactionservice.service.DynamicActionService;
import sophrosyne_api.core.actionrecommendationservice.model.ActionRecommendation;
import sophrosyne_api.core.actionservice.model.Action;
import sophrosyne_api.core.dynamicactionservice.model.DynamicAction;

@SpringBootTest
@TestPropertySource(locations = "/application-test.properties")
public class CommonApiServiceIT extends PostgresIntegrationTestBase {
  private final Logger logger = LogManager.getLogger(getClass());

  @Autowired CommonAPIService sut_commonAPIService;

  @Autowired ActionRecommendationService actionRecommendationService;

  @Autowired ActionService actionService;

  @Autowired DynamicActionService dynamicActionService;

  @Autowired ApikeyService apikeyService;

  @Value("${test.actionrecommendation.filepath}")
  private String testFile;

  private ApikeyDTO apikeyDTO;

  @BeforeEach
  public void generateApikey() {
    try {
      actionService.deleteAction(actionService.getActionDTO(createAPIAction().getName()).get());
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    try {
      dynamicActionService.deleteAllDynamicActions();
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    apikeyService.deleteAllApikeys();
    apikeyDTO = apikeyService.generateAPIKey("apikey_fictional", "test", 1);
  }

  @AfterEach
  public void cleanUp() {
    apikeyService.deleteAllApikeys();
    try {
      actionService.deleteAllActions();
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    try {
      dynamicActionService.deleteAllDynamicActions();
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    actionRecommendationService.deleteAllActionRecommendations();
  }

  @Test
  public void test_checkValidApikeyActionRecommendation() throws IOException {
    ActionRecommendation actionRecommendation = generateActionRecommendation("Test");
    ActionRecommendationDTO actionRecommendationDTO =
        actionRecommendationService.createActionRecommendation(actionRecommendation);
    boolean checkTrue =
        sut_commonAPIService.checkValidApikeyActionRecommendation(
            actionRecommendationDTO, apikeyDTO.getApikey());
    boolean checkFalse =
        sut_commonAPIService.checkValidApikeyActionRecommendation(
            actionRecommendationDTO, "SOMETHING_WRONG");

    assertThat(checkTrue).isTrue();
    assertThat(checkFalse).isFalse();
  }

  @Test
  public void test_checkValidApikeyAction_WhenDynamicActionDTO() {
    ActionDTO actionDTO = dynamicActionService.createDynamicAction(createDynamicAction());
    boolean checkTrueTypeActionDTO =
        sut_commonAPIService.checkValidApikeyAction(actionDTO, apikeyDTO.getApikey());
    boolean checkFalseTypeActionDTO =
        sut_commonAPIService.checkValidApikeyAction(actionDTO, "SOMETHING_WRONG");

    assertThat(checkTrueTypeActionDTO).isTrue();
    assertThat(checkFalseTypeActionDTO).isFalse();

    DynamicActionDTO dynamicActionDTO = (DynamicActionDTO) actionDTO;
    boolean checkTrueTypeDynamicActionDTO =
        sut_commonAPIService.checkValidApikeyAction(dynamicActionDTO, apikeyDTO.getApikey());
    boolean checkFalseTypeDynamicActionDTO =
        sut_commonAPIService.checkValidApikeyAction(dynamicActionDTO, "SOMETHING_WRONG");

    assertThat(checkTrueTypeDynamicActionDTO).isTrue();
    assertThat(checkFalseTypeDynamicActionDTO).isFalse();
  }

  @Test
  public void test_checkValidApikeyAction_WhenActionDTO() {
    Action action = createAPIAction();
    ActionDTO actionDTO = actionService.createAction(action);
    boolean checkTrue =
        sut_commonAPIService.checkValidApikeyAction(actionDTO, apikeyDTO.getApikey());
    boolean checkFalse = sut_commonAPIService.checkValidApikeyAction(actionDTO, "SOMETHING_WRONG");

    assertThat(checkTrue).isTrue();
    assertThat(checkFalse).isFalse();
  }

  private Action createAPIAction() {
    return new Action()
        .name("Test_Action")
        .description("Test_Description")
        .command("ansible-playbook")
        .allowedApikeys(List.of("apikey_fictional"))
        .postExecutionLogFilePath("/etc/file/")
        .requiresConfirmation(0);
  }

  private DynamicAction createDynamicAction() {
    return new DynamicAction()
        .name("Test_Action")
        .description("Test_Description")
        .command("ansible-playbook")
        .dynamicParameters("-i {{inventory}} {{playbook}}")
        .allowedApikeys(List.of("apikey_fictional"))
        .postExecutionLogFilePath("/etc/file/")
        .requiresConfirmation(1);
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
