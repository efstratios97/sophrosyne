package sophrosyne.core.dynamicactionservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
import sophrosyne.core.dropdownoption.dto.DropdownOptionDTO;
import sophrosyne.core.dropdownoption.service.DropdownOptionService;
import sophrosyne.core.dynamicactionservice.dto.DynamicActionDTO;
import sophrosyne.core.dynamicactionservice.service.DynamicActionService;
import sophrosyne.core.utils.DropdownOptionUtils;
import sophrosyne_api.core.dynamicactionservice.model.DynamicAction;
import sophrosyne_api.core.dynamicactionservice.model.ParsedDynamicParametersParametersInner;
import sophrosyne_api.core.internaldropdownoptionservice.model.DropdownOption;

@SpringBootTest
@TestPropertySource(locations = "/application-test.properties")
public class DynamicActionServiceTest extends PostgresIntegrationTestBase {

  private final Logger logger = LogManager.getLogger(getClass());

  @Autowired private DynamicActionService sut_dynamicActionService;
  @Autowired private ApikeyService apikeyService;
  private ApikeyDTO apikeyDTO;
  @Autowired private DropdownOptionUtils dropdownOptionUtils;
  @Autowired private DropdownOptionService dropdownOptionService;

  @BeforeEach
  public void generateApikey() {
    apikeyService.deleteAllApikeys();
    dropdownOptionService.deleteAllDropdownOptions();
    apikeyDTO = apikeyService.generateAPIKey("apikey_fictional", "test", 1);
  }

  @AfterEach
  public void deleteApikey() {
    apikeyService.deleteApikey(apikeyDTO);
    dropdownOptionService.deleteAllDropdownOptions();
  }

  @AfterEach
  public void deleteDynamicAction() {
    try {
      sut_dynamicActionService.deleteAllDynamicActions();
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    dropdownOptionService.deleteAllDropdownOptions();
  }

  @Test
  public void test_createDynamicActionCommand() {
    String dynamicParameters = "inventory:/my/path/inventory.ini,playbook:shutdown";
    sut_dynamicActionService.createDynamicCommand(
        createDynamicAction(false), dynamicParameters, Optional.empty());
    String dynamicParameters2 = " inventory:/my/path/inventory.ini, playbook:shutdown ";
    DynamicActionDTO dynamicActionDTO2 = createDynamicAction(false);
    dynamicActionDTO2.setAssociatedDropdownOptions(null);
    sut_dynamicActionService.createDynamicCommand(
        dynamicActionDTO2, dynamicParameters2, Optional.empty());
    String dynamicParameters3 = " inventory:/my/path/inventory.ini, playbook:{{shutdown}}";
    DynamicActionDTO dynamicActionDTO3 = createDynamicAction(false);
    dynamicActionDTO3.setAssociatedDropdownOptions(null);
    sut_dynamicActionService.createDynamicCommand(
        dynamicActionDTO3, dynamicParameters3, Optional.empty());
  }

  @Test
  public void test_getParsedParameters() {
    List<String> dynamicParametersExtracted =
        sut_dynamicActionService
            .getParsedDynamicParameters(createDynamicAction(true))
            .getParameters()
            .stream()
            .map(ParsedDynamicParametersParametersInner::getParameter)
            .collect(Collectors.toList());
    assertThat(dynamicParametersExtracted)
        .containsAll(
            new ArrayList<>() {
              {
                add("inventory");
                add("playbook");
              }
            });
  }

  private DynamicActionDTO createDynamicAction(boolean createDropdown) {
    DynamicAction dynamicAction =
        new DynamicAction()
            .name("Test_Action")
            .description("Test_Description")
            .command("ansible-playbook")
            .dynamicParameters("-i {{inventory}} {{playbook}}.yml")
            .allowedApikeys(List.of("apikey_fictional"))
            .postExecutionLogFilePath("/etc/file/")
            .requiresConfirmation(0)
            .keepLatestConfirmationRequest(1)
            .muted(0)
            .onlySingleExecution(1);
    if (createDropdown) {
      DropdownOption dropdownOption1 = dropdownOptionUtils.createDropdownOption();
      dropdownOption1.setDynamicParameterToMatch("inventory");
      DropdownOption dropdownOption2 = dropdownOptionUtils.createDropdownOption();
      dropdownOption2.setDynamicParameterToMatch("playbook");
      dropdownOption2.setName("playbook_test");
      DropdownOptionDTO dropdownOptionDTO2;
      DropdownOptionDTO dropdownOptionDTO1;
      try {
        dropdownOptionDTO2 = dropdownOptionService.createDropdownOptionDTO(dropdownOption2);
        dropdownOptionDTO1 = dropdownOptionService.createDropdownOptionDTO(dropdownOption1);

        dynamicAction.associatedDropdownOptions(
            new ArrayList<>() {
              {
                add(dropdownOptionDTO1.getId());
                add(dropdownOptionDTO2.getId());
              }
            });
      } catch (RuntimeException _) {
      }
    }
    return sut_dynamicActionService.createDynamicActionDTOFromDynamicAction(dynamicAction);
  }
}
