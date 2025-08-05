package sophrosyne.core.actionservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import sophrosyne.core.PostgresIntegrationTestBase;
import sophrosyne.core.actionservice.dto.ActionDTO;
import sophrosyne.core.apikeyservice.dto.ApikeyDTO;
import sophrosyne.core.apikeyservice.service.ApikeyService;
import sophrosyne_api.core.actionservice.model.Action;

@SpringBootTest
@TestPropertySource(locations = "/application-test.properties")
public class ActionServiceIT extends PostgresIntegrationTestBase {

  private final org.apache.logging.log4j.Logger logger =
      org.apache.logging.log4j.LogManager.getLogger(getClass());

  @Autowired private ActionService sut_actionService;

  @Autowired private ApikeyService apikeyService;

  private ApikeyDTO apikeyDTO;

  @BeforeEach
  @AfterEach
  public void generateApikey() {
    apikeyService.deleteAllApikeys();
    apikeyDTO = apikeyService.generateAPIKey("apikey_fictional", "test", 1);
    try {
      sut_actionService.deleteAllActions();
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  @Test
  public void test_createActionDTOFromAction() {
    Action action = createAPIAction();
    ActionDTO actionDTO = sut_actionService.createActionDTOFromAction(action);
    assertThat(actionDTO.getName()).isEqualTo(action.getName());
    assertThat(actionDTO.getCommand()).isEqualTo(action.getCommand());
    assertThat(actionDTO.getDescription()).isEqualTo(action.getDescription());
    assertThat(actionDTO.getPostExecutionLogFilePath())
        .isEqualTo(action.getPostExecutionLogFilePath());
    assertThat(actionDTO.getAllowedApikeys().stream().map(ApikeyDTO::getApikeyname))
        .containsAll(action.getAllowedApikeys());
    assertThat(actionDTO.getRequiresConfirmation()).isEqualTo(action.getRequiresConfirmation());
    assertThat(actionDTO.getVersion()).isEqualTo(action.getVersion());
    assertThat(actionDTO.getMuted()).isEqualTo(action.getMuted());

    action.setPostExecutionLogFilePath(null);

    actionDTO = sut_actionService.createActionDTOFromAction(action);
    assertThat(actionDTO.getPostExecutionLogFilePath())
        .isEqualTo(action.getPostExecutionLogFilePath());
  }

  @Test
  public void test_createAction() {
    Action action = createAPIAction();
    ActionDTO actionDTO = sut_actionService.createAction(action);
    assertThat(actionDTO.getName()).isEqualTo(action.getName());
    assertThat(actionDTO.getCommand()).isEqualTo(action.getCommand());
    assertThat(actionDTO.getDescription()).isEqualTo(action.getDescription());
    assertThat(actionDTO.getPostExecutionLogFilePath())
        .isEqualTo(action.getPostExecutionLogFilePath());
    assertThat(actionDTO.getAllowedApikeys().stream().map(ApikeyDTO::getApikeyname))
        .containsAll(action.getAllowedApikeys());
    assertThat(actionDTO.getRequiresConfirmation()).isEqualTo(action.getRequiresConfirmation());
    assertThat(actionDTO.getVersion()).isEqualTo(action.getVersion());
    assertThat(actionDTO.getMuted()).isEqualTo(action.getMuted());

    action.setPostExecutionLogFilePath(null);
    action.setName("test_2");

    actionDTO = sut_actionService.createAction(action);

    assertThat(actionDTO.getPostExecutionLogFilePath())
        .isEqualTo(action.getPostExecutionLogFilePath());
  }

  @Test
  public void test_updateAction() {
    Action action = createAPIAction();
    ActionDTO actionDTO = sut_actionService.createAction(action);
    Action action2 = createAPIAction();
    action2.setName("test_update");
    sut_actionService.updateAction(actionDTO.getId(), action2);
    actionDTO = sut_actionService.getActionDTO(actionDTO.getId()).get();

    assertThat(actionDTO.getName()).isEqualTo(action2.getName());
  }

  @Test
  public void test_updateAction_whenActionNOTExists() {
    assertThrows(
        NoSuchElementException.class,
        () -> sut_actionService.updateAction(createAPIAction().getName(), createAPIAction()));
  }

  @Test
  public void test_deleteAction() {
    Action action = createAPIAction();
    ActionDTO actionDTO = sut_actionService.createAction(action);
    sut_actionService.deleteAction(actionDTO);

    assertThat(sut_actionService.getActionDTO(actionDTO.getId()).isPresent()).isFalse();
  }

  @Test
  public void test_deleteAction_whenActionNOTExists() {
    assertThrows(
        NoSuchElementException.class,
        () ->
            sut_actionService.deleteAction(
                sut_actionService.getActionDTO(createAPIAction().getDescription()).get()));
  }

  private Action createAPIAction() {
    return new Action()
        .name("Test_Action")
        .description("Test_Description")
        .command("ansible-playbook")
        .allowedApikeys(List.of("apikey_fictional"))
        .postExecutionLogFilePath("/etc/file/")
        .requiresConfirmation(0)
        .version("v.1.2.3")
        .muted(0);
  }
}
