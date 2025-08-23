package sophrosyne.core.executorservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import sophrosyne.core.PostgresIntegrationTestBase;
import sophrosyne.core.actionarchiveservice.dto.ActionArchiveDTO;
import sophrosyne.core.actionarchiveservice.service.ActionArchiveService;
import sophrosyne.core.actionservice.dto.ActionDTO;
import sophrosyne.core.actionservice.service.ActionService;
import sophrosyne.core.apikeyservice.service.ApikeyService;
import sophrosyne.core.executorservice.service.ActionExecutorService;
import sophrosyne.core.executorservice.utils.Utils;
import sophrosyne_api.core.actionservice.model.Action;

@SpringBootTest
@TestPropertySource(locations = "/application-test.properties")
public class ActionExecutionServiceIT extends PostgresIntegrationTestBase {

  private final Logger logger = LogManager.getLogger(getClass());

  private Action action;
  @Autowired private ActionExecutorService sut_actionExecutorService;

  @Autowired private Utils sut_actionExecutorServiceUtils;

  @Autowired private ActionService actionService;

  @Autowired private ApikeyService apikeyService;

  @Autowired private ActionArchiveService actionArchiveService;

  private ActionDTO actionDTO;

  @AfterEach
  @BeforeEach
  public void clean_prep() {
    try {
      actionService.deleteAllActions();
      apikeyService.deleteAllApikeys();
      actionArchiveService.purgeCompleteArchive();
      apikeyService.generateAPIKey("apikey_fictional", "test", 1);
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  @Test
  public void test_ExecuteAction() {
    action = createAPIAction("sleep 1", "ping -n 1 127.0.0.1");
    ActionDTO actionDTO = actionService.createAction(action);
    sut_actionExecutorService.registerAction(
        actionDTO, ActionArchiveDTO.TYPES.INTERNAL.name(), true);
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      logger.error(e.getMessage());
    }
    List<ActionArchiveDTO> archives = actionArchiveService.getActionArchives();
    assertThat(archives).hasSize(1);
    assertThat(actionDTO.getName()).isEqualTo(archives.getFirst().getActionName());
    assertThat(actionDTO.getVersion()).isEqualTo(archives.getFirst().getVersion());
    assertThat(actionDTO.getId()).isEqualTo(archives.getFirst().getActionId());
  }

  @Test
  public void test_ExecuteAction_whenRequiresConfirmation_Given() {
    action = createAPIAction("sleep 1", "ping -n 1 127.0.0.1");
    actionDTO = actionService.createAction(action);
    actionDTO.setRequiresConfirmation(1);
    sut_actionExecutorService.registerAction(
        actionDTO, ActionArchiveDTO.TYPES.INTERNAL.name(), false);
    HashMap<String, Object> confirmedActionProcessDataShared =
        sut_actionExecutorService.actionProcessDataShared.get(actionDTO.getId());
    confirmedActionProcessDataShared.put("confirmed", true);
    sut_actionExecutorService.actionProcessDataShared.put(
        actionDTO.getId(), confirmedActionProcessDataShared);
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      logger.error(e.getMessage());
    }
    List<ActionArchiveDTO> archives = actionArchiveService.getActionArchives();
    assertThat(archives).hasSize(1);
  }

  @Test
  public void test_ExecuteAction_whenRequiresConfirmation_NotGiven() {
    action = createAPIAction("sleep 1", "ping -n 1 127.0.0.1");
    actionDTO = actionService.createAction(action);
    actionDTO.setRequiresConfirmation(1);
    sut_actionExecutorService.registerAction(
        actionDTO, ActionArchiveDTO.TYPES.INTERNAL.name(), false);

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      logger.error(e.getMessage());
    }
    List<ActionArchiveDTO> archives = actionArchiveService.getActionArchives();
    assertThat(archives).hasSize(0);

    ResponseEntity<Void> response =
        sut_actionExecutorServiceUtils.executeActionHelper(actionDTO, "INTERNAL");

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      logger.error(e.getMessage());
    }

    assertThat(response.equals(ResponseEntity.status(HttpStatus.CREATED).build())).isTrue();
    archives = actionArchiveService.getActionArchives();
    assertThat(archives).hasSize(0);
  }

  @Test
  public void test_ExecuteAction_when_Muted() {
    action = createAPIAction("sleep 1", "ping -n 1 127.0.0.1");
    action.setMuted(1);
    actionDTO = actionService.createAction(action);

    ResponseEntity<Void> response =
        sut_actionExecutorServiceUtils.executeActionHelper(actionDTO, "INTERNAL");

    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      logger.error(e.getMessage());
    }
    assertThat(response.equals(ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build())).isTrue();
    List<ActionArchiveDTO> archives = actionArchiveService.getActionArchives();
    assertThat(archives).hasSize(0);
  }

  @Test
  public void test_stopAction() {
    action = createAPIAction("sleep 1920", "ping -n 99 127.0.0.1");
    actionDTO = actionService.createAction(action);
    sut_actionExecutorService.registerAction(
        actionDTO, ActionArchiveDTO.TYPES.EXTERNAL.name(), true);
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      logger.error(e.getMessage());
    }
    assertThat(sut_actionExecutorService.stopExecution(actionDTO.getId())).isTrue();
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
        .requiresConfirmation(0)
        .muted(0)
        .version("v.2.0.0");
  }
}
