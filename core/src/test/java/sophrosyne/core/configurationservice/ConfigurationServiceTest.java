package sophrosyne.core.configurationservice;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
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
import sophrosyne.core.actionrecommendationservice.service.ActionRecommendationService;
import sophrosyne.core.actionservice.service.ActionService;
import sophrosyne.core.apikeyservice.service.ApikeyService;
import sophrosyne.core.configurationservice.service.ConfigurationService;
import sophrosyne.core.dynamicactionservice.service.DynamicActionService;
import sophrosyne.core.userservice.service.UserService;
import sophrosyne_api.core.actionrecommendationservice.model.ActionRecommendation;
import sophrosyne_api.core.actionservice.model.Action;
import sophrosyne_api.core.dynamicactionservice.model.DynamicAction;
import sophrosyne_api.core.userservice.model.User;

@SpringBootTest
@TestPropertySource(locations = "/application-test.properties")
public class ConfigurationServiceTest extends PostgresIntegrationTestBase {
  private final Logger logger = LogManager.getLogger(getClass());

  @Autowired private ConfigurationService sut_configurationService;
  @Autowired private UserService userService;
  @Autowired private ApikeyService apikeyService;
  @Autowired private ActionService actionService;

  @Autowired private DynamicActionService dynamicActionService;

  @Autowired private ActionRecommendationService actionRecommendationService;

  @Value("${test.actionrecommendation.filepath}")
  private String testFile;

  @BeforeEach
  @AfterEach
  public void cleanUpEach() {
    try {
      userService.deleteUserByUsername("Lschmutzi18");
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    try {
      apikeyService.deleteAllApikeys();
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
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
    try {
      actionRecommendationService.deleteAllActionRecommendations();
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  @Test
  public void test_getConfigurationData() throws IOException {
    String usersKey = "users";
    String actionsKey = "actions";
    String dynamicActionsKey = "dynamic_actions";
    String apikeyKey = "apikeys";
    String actionRecommendationsKey = "action_recommendations";

    createTestSophrosyneData();

    Map<String, List<String>> res = sut_configurationService.getConfigurationData();

    assertThat(res)
        .containsKeys(usersKey, actionsKey, dynamicActionsKey, apikeyKey, actionRecommendationsKey);
    assertThat(res.get(usersKey)).hasSize(2); // + Standard Admin User
    assertThat(res.get(apikeyKey)).hasSize(1);
    assertThat(res.get(actionsKey)).hasSize(1);
    assertThat(res.get(dynamicActionsKey)).hasSize(2);
    assertThat(res.get(actionRecommendationsKey)).hasSize(1);
  }

  @Test
  public void test_getConfigurationDataAsJSON() {
    createTestSophrosyneData();
    assertThat(sut_configurationService.getConfigurationDataJSON()).isInstanceOf(String.class);
  }

  @Test
  public void test_createInitConfigurationFromFile() {
    sut_configurationService.importSophrosyneConfigurationFromFile();

    assertThat(apikeyService.getApiDTOs()).hasSize(1);
    assertThat(userService.getUsers()).hasSize(2);
    assertThat(actionService.getActions()).hasSize(1);
    assertThat(dynamicActionService.getDynamicActions()).hasSize(2);
    assertThat(actionRecommendationService.getActionRecommendations()).hasSize(1);
  }

  @Test
  public void test_createInitConfigurationFromFileForce() {
    sut_configurationService.importSophrosyneConfigurationFromFileForce();

    assertThat(apikeyService.getApiDTOs()).hasSize(1);
    assertThat(userService.getUsers()).hasSize(2);
    assertThat(actionService.getActions()).hasSize(1);
    assertThat(dynamicActionService.getDynamicActions()).hasSize(2);
    assertThat(actionRecommendationService.getActionRecommendations()).hasSize(1);
  }

  @Test
  public void test_createInitConfiguration() {
    createTestSophrosyneData();
    Map<String, List<String>> data = sut_configurationService.getConfigurationData();
    sut_configurationService.importSophrosyneConfiguration(data);

    assertThat(apikeyService.getApiDTOs()).hasSize(1);
    assertThat(userService.getUsers()).hasSize(2);
    assertThat(actionService.getActions()).hasSize(1);
    assertThat(dynamicActionService.getDynamicActions()).hasSize(2);
    assertThat(actionRecommendationService.getActionRecommendations()).hasSize(1);
  }

  @Test
  public void test_createInitConfigurationForce() {
    createTestSophrosyneData();
    Map<String, List<String>> data = sut_configurationService.getConfigurationData();
    sut_configurationService.importSophrosyneConfigurationForce(data);

    assertThat(apikeyService.getApiDTOs()).hasSize(1);
    assertThat(userService.getUsers()).hasSize(2);
    assertThat(actionService.getActions()).hasSize(1);
    assertThat(dynamicActionService.getDynamicActions()).hasSize(2);
    assertThat(actionRecommendationService.getActionRecommendations()).hasSize(1);
  }

  private void createTestSophrosyneData() {
    try {
      userService.createUser(
          new User()
              .email("test@test.de")
              .firstName("Lutz")
              .lastName("Schmutzfinger")
              .role(User.RoleEnum.ADMIN)
              .password("12345")
              .username("Lschmutzi18"));

      apikeyService.generateAPIKey("apikey_fictional", "test", 1);

      dynamicActionService.createDynamicAction(
          new DynamicAction()
              .name("Test_Action")
              .description("Test_Description")
              .command("ansible-playbook")
              .dynamicParameters("-i {{inventory}} {{playbook}}")
              .allowedApikeys(List.of("apikey_fictional"))
              .postExecutionLogFilePath("/etc/file/")
              .version("1.2.3")
              .requiresConfirmation(0)
              .muted(0)
              .keepLatestConfirmationRequest(1));

      dynamicActionService.createDynamicAction(
          new DynamicAction()
              .name("Test_Action_2")
              .description("Test_Description")
              .command("ansible-playbook")
              .dynamicParameters("-i {{inventory}} {{playbook}}")
              .allowedApikeys(List.of("apikey_fictional"))
              .postExecutionLogFilePath("/etc/file/")
              .version("1.2.3")
              .requiresConfirmation(1)
              .muted(0)
              .keepLatestConfirmationRequest(0));

      actionService.createAction(
          new Action()
              .name("Test_Action")
              .description("Test_Description")
              .command("ansible-playbook")
              .allowedApikeys(List.of("apikey_fictional"))
              .postExecutionLogFilePath("/etc/file/")
              .version("1.2.3")
              .muted(0)
              .requiresConfirmation(0));

      actionRecommendationService.createActionRecommendation(
          new ActionRecommendation()
              .name("Test")
              .description("Testing")
              .responsibleEntity("Me")
              .contactInformation("Too")
              .addAllowedApikeysItem("apikey_fictional")
              .additionalDocumentation(testFile.getBytes(StandardCharsets.UTF_8)));

    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }
}
