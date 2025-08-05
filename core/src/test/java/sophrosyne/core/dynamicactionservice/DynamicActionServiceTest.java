package sophrosyne.core.dynamicactionservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import sophrosyne.core.PostgresIntegrationTestBase;
import sophrosyne.core.apikeyservice.dto.ApikeyDTO;
import sophrosyne.core.apikeyservice.service.ApikeyService;
import sophrosyne.core.dynamicactionservice.dto.DynamicActionDTO;
import sophrosyne.core.dynamicactionservice.service.DynamicActionService;
import sophrosyne_api.core.dynamicactionservice.model.DynamicAction;

@SpringBootTest
@TestPropertySource(locations = "/application-test.properties")
public class DynamicActionServiceTest extends PostgresIntegrationTestBase {

  private final Logger logger = LogManager.getLogger(getClass());

  @Autowired private DynamicActionService sut_dynamicActionService;
  @Autowired private ApikeyService apikeyService;
  private ApikeyDTO apikeyDTO;

  @BeforeEach
  public void generateApikey() {
    apikeyService.deleteAllApikeys();
    apikeyDTO = apikeyService.generateAPIKey("apikey_fictional", "test", 1);
  }

  @AfterEach
  public void deleteApikey() {
    apikeyService.deleteApikey(apikeyDTO);
  }

  @AfterEach
  public void deleteDynamicAction() {
    try {
      sut_dynamicActionService.deleteAllDynamicActions();
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  @Test
  public void test_createDynamicActionCommand() {
    String dynamicParameters = "inventory:/my/path/inventory.ini,playbook:shutdown";
    sut_dynamicActionService.createDynamicCommand(
        createDynamicAction(), dynamicParameters, Optional.empty());
    String dynamicParameters2 = " inventory:/my/path/inventory.ini, playbook:shutdown ";
    sut_dynamicActionService.createDynamicCommand(
        createDynamicAction(), dynamicParameters2, Optional.empty());
    String dynamicParameters3 = " inventory:/my/path/inventory.ini, playbook:{{shutdown}}";
    sut_dynamicActionService.createDynamicCommand(
        createDynamicAction(), dynamicParameters3, Optional.empty());
  }

  @Test
  public void test_getParsedParameters() {
    List<String> dynamicParametersExtracted =
        sut_dynamicActionService.getParsedDynamicParameters(createDynamicAction());
    assertThat(dynamicParametersExtracted)
        .containsAll(
            new ArrayList<>() {
              {
                add("inventory");
                add("playbook");
              }
            });
  }

  private DynamicActionDTO createDynamicAction() {
    return sut_dynamicActionService.createDynamicActionDTOFromDynamicAction(
        new DynamicAction()
            .name("Test_Action")
            .description("Test_Description")
            .command("ansible-playbook")
            .dynamicParameters("-i {{inventory}} {{playbook}}.yml")
            .allowedApikeys(List.of("apikey_fictional"))
            .postExecutionLogFilePath("/etc/file/")
            .requiresConfirmation(0)
            .keepLatestConfirmationRequest(1)
            .muted(0));
  }
}
