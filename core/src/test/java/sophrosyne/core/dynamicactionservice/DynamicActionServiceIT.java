package sophrosyne.core.dynamicactionservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.NoSuchElementException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import sophrosyne.core.dynamicactionservice.dto.DynamicActionDTO;
import sophrosyne.core.dynamicactionservice.service.DynamicActionService;
import sophrosyne_api.core.dynamicactionservice.model.DynamicAction;

@SpringBootTest
@TestPropertySource(locations = "/application-test.properties")
public class DynamicActionServiceIT extends PostgresIntegrationTestBase {

  private final Logger logger = LogManager.getLogger(getClass());

  @Autowired private DynamicActionService sut_dynamicActionService;

  @Autowired private ApikeyService apikeyService;

  @BeforeEach
  @AfterEach
  public void generateApikey() {
    try {
      apikeyService.deleteAllApikeys();
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    apikeyService.generateAPIKey("apikey_fictional", "test", 1);
    try {
      sut_dynamicActionService.deleteAllDynamicActions();
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  @Test
  public void test_createActionDTOFromAction() {
    DynamicAction dynamicAction = createDynamicAction();
    DynamicActionDTO dynamicActionDTO =
        sut_dynamicActionService.createDynamicActionDTOFromDynamicAction(dynamicAction);
    assertThat(dynamicActionDTO.getName()).isEqualTo(dynamicAction.getName());
    assertThat(dynamicActionDTO.getCommand()).isEqualTo(dynamicAction.getCommand());
    assertThat(dynamicActionDTO.getDescription()).isEqualTo(dynamicAction.getDescription());
    assertThat(dynamicActionDTO.getPostExecutionLogFilePath())
        .isEqualTo(dynamicAction.getPostExecutionLogFilePath());
    org.assertj.core.api.Assertions.assertThat(
            dynamicActionDTO.getAllowedApikeysForDynamicActions().stream()
                .map(ApikeyDTO::getApikeyname))
        .containsAll(dynamicAction.getAllowedApikeys());
    assertThat(dynamicActionDTO.getRequiresConfirmation())
        .isEqualTo(dynamicAction.getRequiresConfirmation());
    assertThat(dynamicActionDTO.getVersion()).isEqualTo(dynamicAction.getVersion());

    dynamicAction.setPostExecutionLogFilePath(null);

    dynamicActionDTO =
        sut_dynamicActionService.createDynamicActionDTOFromDynamicAction(dynamicAction);
    assertThat(dynamicActionDTO.getPostExecutionLogFilePath())
        .isEqualTo(dynamicAction.getPostExecutionLogFilePath());
  }

  @Test
  public void test_createAction() {
    DynamicAction dynamicAction = createDynamicAction();
    DynamicActionDTO dynamicActionDTO = sut_dynamicActionService.createDynamicAction(dynamicAction);
    assertThat(dynamicActionDTO.getName()).isEqualTo(dynamicAction.getName());
    assertThat(dynamicActionDTO.getCommand()).isEqualTo(dynamicAction.getCommand());
    assertThat(dynamicActionDTO.getDynamicParameters())
        .isEqualTo(dynamicAction.getDynamicParameters());
    assertThat(dynamicActionDTO.getDescription()).isEqualTo(dynamicAction.getDescription());
    assertThat(dynamicActionDTO.getPostExecutionLogFilePath())
        .isEqualTo(dynamicAction.getPostExecutionLogFilePath());
    assertThat(
            dynamicActionDTO.getAllowedApikeysForDynamicActions().stream()
                .map(sophrosyne.core.apikeyservice.dto.ApikeyDTO::getApikeyname))
        .containsAll(dynamicAction.getAllowedApikeys());
    assertThat(dynamicActionDTO.getRequiresConfirmation())
        .isEqualTo(dynamicAction.getRequiresConfirmation());
    assertThat(dynamicActionDTO.getVersion()).isEqualTo(dynamicAction.getVersion());

    dynamicAction.setPostExecutionLogFilePath(null);
    dynamicAction.setName("test_2");

    dynamicActionDTO = sut_dynamicActionService.createDynamicAction(dynamicAction);

    assertThat(dynamicActionDTO.getPostExecutionLogFilePath())
        .isEqualTo(dynamicAction.getPostExecutionLogFilePath());
  }

  @Test
  public void test_updateAction() {
    DynamicAction dynamicAction = createDynamicAction();
    ActionDTO dynamicActionDTO = sut_dynamicActionService.createDynamicAction(dynamicAction);
    DynamicAction action2 = createDynamicAction();
    action2.setName("test_update");
    sut_dynamicActionService.updateDynamicAction(dynamicActionDTO.getId(), action2);

    dynamicActionDTO = sut_dynamicActionService.getDynamicActionDTO(dynamicActionDTO.getId()).get();

    assertThat(dynamicActionDTO.getName()).isEqualTo(action2.getName());
  }

  @Test
  public void test_updateAction_whenActionNOTExists() {
    assertThrows(
        NoSuchElementException.class,
        () ->
            sut_dynamicActionService.updateDynamicAction(
                createDynamicAction().getName(), createDynamicAction()));
  }

  @Test
  public void test_deleteAction() {
    DynamicAction dynamicAction = createDynamicAction();
    DynamicActionDTO dynamicActionDTO = sut_dynamicActionService.createDynamicAction(dynamicAction);
    sut_dynamicActionService.deleteDynamicAction(dynamicActionDTO);

    assertThat(sut_dynamicActionService.getDynamicActionDTO(dynamicActionDTO.getId()).isPresent())
        .isFalse();
  }

  @Test
  public void test_deleteAction_whenActionNOTExists() {
    assertThrows(
        NoSuchElementException.class,
        () ->
            sut_dynamicActionService.deleteDynamicAction(
                sut_dynamicActionService
                    .getDynamicActionDTO(createDynamicAction().getName())
                    .get()));
  }

  @Test
  public void test_checkActionDTOisDynamicActionDTO() {
    assertThat(
            sut_dynamicActionService.checkActionDTOisDynamicActionDTO(
                sut_dynamicActionService.createDynamicAction(createDynamicAction())))
        .isTrue();
  }

  private DynamicAction createDynamicAction() {
    return new DynamicAction()
        .name("Test_Action")
        .description("Test_Description")
        .command("ansible-playbook")
        .dynamicParameters("-i {{inventory}} {{playbook}}")
        .allowedApikeys(List.of("apikey_fictional"))
        .postExecutionLogFilePath("/etc/file/")
        .version("v.1.2.3")
        .requiresConfirmation(1)
        .keepLatestConfirmationRequest(1)
        .muted(0);
  }
}
