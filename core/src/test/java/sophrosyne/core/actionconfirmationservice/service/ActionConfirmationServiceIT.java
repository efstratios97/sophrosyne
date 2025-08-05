package sophrosyne.core.actionconfirmationservice.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import sophrosyne.core.PostgresIntegrationTestBase;
import sophrosyne.core.actionarchiveservice.dto.ActionArchiveDTO;
import sophrosyne.core.actionarchiveservice.service.ActionArchiveService;
import sophrosyne.core.actionservice.dto.ActionDTO;
import sophrosyne.core.actionservice.service.ActionService;
import sophrosyne.core.apikeyservice.service.ApikeyService;
import sophrosyne.core.dynamicactionservice.dto.DynamicActionDTO;
import sophrosyne.core.dynamicactionservice.service.DynamicActionService;
import sophrosyne.core.executorservice.service.ActionExecutorService;
import sophrosyne_api.core.actionconfirmationservice.model.Confirmation;
import sophrosyne_api.core.actionservice.model.Action;
import sophrosyne_api.core.dynamicactionservice.model.DynamicAction;

@SpringBootTest
@TestPropertySource(locations = "/application-test.properties")
public class ActionConfirmationServiceIT extends PostgresIntegrationTestBase {

  private final Logger logger = LogManager.getLogger(getClass());

  @Autowired private ActionExecutorService actionExecutorService;

  @Autowired private ActionService actionService;

  @Autowired private DynamicActionService dynamicActionService;

  @Autowired private ApikeyService apikeyService;

  @Autowired private ActionArchiveService actionArchiveService;

  @Autowired private ActionConfirmationService sut_actionConfirmationService;

  private ActionDTO actionDTO;

  private DynamicActionDTO dynamicActionDTO;

  @BeforeEach
  public void generateApikey() {
    actionArchiveService.purgeCompleteArchive();
    actionService.deleteAllActions();
    dynamicActionService.deleteAllDynamicActions();
    apikeyService.deleteAllApikeys();
    apikeyService.generateAPIKey("apikey_fictional", "test", 1);
  }

  @AfterEach
  public void deleteAction() {
    try {
      actionArchiveService.purgeCompleteArchive();
      actionService.deleteAllActions();
      dynamicActionService.deleteAllDynamicActions();
      apikeyService.deleteAllApikeys();
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  @Test
  public void test_getConfirmations() {
    Action action = createAPIAction("sleep 1", "ping -n 1 127.0.0.1");
    actionDTO = actionService.createAction(action);
    actionExecutorService.registerAction(actionDTO, ActionArchiveDTO.TYPES.INTERNAL.name(), false);

    DynamicAction dynamicAction = createAPIDynamicAction("sleep 1", "ping");
    DynamicActionDTO dynamicActionDTO = dynamicActionService.createDynamicAction(dynamicAction);
    actionExecutorService.registerAction(
        dynamicActionDTO, ActionArchiveDTO.TYPES.INTERNAL.name(), false);

    // Control already confirmed action
    Action actionAlreadyConfirmed = createAPIAction("sleep 1", "ping -n 1 127.0.0.1");
    actionAlreadyConfirmed.setName("Already Confirmed Action");
    ActionDTO actionDTOAlreadyConfirmed = actionService.createAction(actionAlreadyConfirmed);
    actionExecutorService.registerAction(
        actionDTOAlreadyConfirmed, ActionArchiveDTO.TYPES.INTERNAL.name(), true);

    assertThat(sut_actionConfirmationService.getConfirmations()).hasSize(2);
    assertThat(
            sut_actionConfirmationService.getConfirmations().stream()
                .filter(
                    confirmation -> {
                      return confirmation.getRunningActionId().equals(actionDTO.getId());
                    })
                .toList()
                .getFirst()
                .getActionCommand())
        .isEqualTo(actionDTO.getCommand());
    assertThat(
            sut_actionConfirmationService.getConfirmations().stream()
                .filter(
                    confirmation -> {
                      return confirmation.getRunningActionId().equals(actionDTO.getId());
                    })
                .toList()
                .getFirst()
                .getActionDescription())
        .isEqualTo(actionDTO.getDescription());
  }

  @Test
  public void test_getConfirmations_WhenActionDoesNotExist() {
    Action action = createAPIAction("sleep 1", "ping -n 1 127.0.0.1");
    actionDTO = actionService.createAction(action);
    // E.g: User deletes Action before Action requires confirmation
    actionService.deleteAction(actionDTO);
    actionExecutorService.registerAction(actionDTO, ActionArchiveDTO.TYPES.INTERNAL.name(), false);

    assertThat(sut_actionConfirmationService.getConfirmations()).hasSize(0);
  }

  @Test
  public void test_evaluateConfirmationResponse() {
    Action action = createAPIAction("sleep 1", "ping -n 1 127.0.0.1");
    actionDTO = actionService.createAction(action);

    // Positive case
    actionExecutorService.registerAction(actionDTO, ActionArchiveDTO.TYPES.INTERNAL.name(), false);

    Confirmation positiveConfirmation =
        new Confirmation()
            .runningActionId(actionDTO.getId())
            .actionName(actionDTO.getName())
            .confirmed(1);

    assertThat(sut_actionConfirmationService.getConfirmations()).hasSize(1);

    sut_actionConfirmationService.evaluateConfirmationResponse(positiveConfirmation);
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      logger.error(e.getMessage());
      e.printStackTrace();
    }
    List<ActionArchiveDTO> archives = actionArchiveService.getActionArchives();

    assertThat(sut_actionConfirmationService.getConfirmations()).hasSize(0);
    assertThat(archives).hasSize(1);

    // Negative Case
    actionExecutorService.registerAction(actionDTO, ActionArchiveDTO.TYPES.INTERNAL.name(), false);

    Confirmation negativeConfirmation =
        new Confirmation()
            .runningActionId(actionDTO.getId())
            .actionName(actionDTO.getName())
            .confirmed(0);
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      logger.error(e.getMessage());
      e.printStackTrace();
    }
    assertThat(sut_actionConfirmationService.getConfirmations()).hasSize(1);

    sut_actionConfirmationService.evaluateConfirmationResponse(negativeConfirmation);

    assertThat(sut_actionConfirmationService.getConfirmations()).hasSize(0);
    // No increase
    assertThat(archives).hasSize(1);
  }

  private Action createAPIAction(String commandLinux, String commandWindows) {
    String command = "";

    String osName = System.getProperty("os.name").toLowerCase();
    if (osName.contains("nix") || osName.contains("nux") || osName.contains("mac")) {
      command = commandLinux;
    } else if (osName.contains("win")) {
      command = commandWindows;
    }
    return new Action()
        .name("Test_Action_1")
        .description("Test_Description")
        .command(command)
        .allowedApikeys(List.of("apikey_fictional"))
        .postExecutionLogFilePath(null)
        .requiresConfirmation(1);
  }

  private DynamicAction createAPIDynamicAction(String commandLinux, String commandWindows) {
    String command = "";

    String osName = System.getProperty("os.name").toLowerCase();
    if (osName.contains("nix") || osName.contains("nux") || osName.contains("mac")) {
      command = commandLinux;
    } else if (osName.contains("win")) {
      command = commandWindows;
    }

    String dynamicParameters = "";
    if (osName.contains("nix") || osName.contains("nux") || osName.contains("mac")) {
      dynamicParameters = "1";
    } else if (osName.contains("win")) {
      dynamicParameters = " -n {{execution_times}} 127.0.0.1";
    }
    return new DynamicAction()
        .name("Test_Action_1")
        .description("Test_Description")
        .command(command)
        .dynamicParameters(dynamicParameters)
        .allowedApikeys(List.of("apikey_fictional"))
        .postExecutionLogFilePath(null)
        .requiresConfirmation(1);
  }
}
