package sophrosyne.core.configurationservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.*;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import sophrosyne.core.PostgresIntegrationTestBase;
import sophrosyne.core.actionservice.dto.ActionDTO;
import sophrosyne.core.actionservice.service.ActionService;
import sophrosyne.core.configurationservice.dto.ControlPanelDashboardGroupConfigurationDTO;
import sophrosyne.core.controlpanelservice.dto.ControlPanelDashboardGroupDTO;
import sophrosyne.core.dynamicactionservice.dto.DynamicActionDTO;
import sophrosyne.core.dynamicactionservice.service.DynamicActionService;
import sophrosyne_api.core.actionservice.model.Action;
import sophrosyne_api.core.dynamicactionservice.model.DynamicAction;

@SpringBootTest
@TestPropertySource(locations = "/application-test.properties")
class ControlPanelDashboardGroupConfigurationDTOTest extends PostgresIntegrationTestBase {

  @Autowired ActionService actionService;
  @Autowired DynamicActionService dynamicActionService;

  // --- helpers: minimal API models for creation ---
  private static Action actionModel(String name) {
    Action a = new Action();
    a.setId(name);
    a.setName(name);
    a.setDescription("desc-" + name);
    a.setCommand("echo " + name);
    a.setVersion("1.0");
    a.setPostExecutionLogFilePath("/tmp/" + name + ".log");
    a.setRequiresConfirmation(0);
    a.setMuted(0);
    a.setAllowedApikeys(new ArrayList<>());
    return a;
  }

  private static DynamicAction dynModel(String name) {
    DynamicAction d = new DynamicAction();
    d.setId(name);
    d.setName(name);
    d.setDescription("dyn-" + name);
    d.setCommand("echo dyn-" + name);
    d.setVersion("1.0");
    d.setPostExecutionLogFilePath("/tmp/dyn-" + name + ".log");
    d.setRequiresConfirmation(0);
    d.setMuted(0);
    d.setDynamicParameters("{}");
    d.setKeepLatestConfirmationRequest(0);
    d.setAllowedApikeys(new ArrayList<>());
    return d;
  }

  @BeforeEach
  void setup() {
    try {
      actionService.deleteAllActions();
    } catch (Exception ignored) {
    }
    try {
      dynamicActionService.deleteAllDynamicActions();
    } catch (Exception ignored) {
    }
  }

  @AfterEach
  void tearDown() {
    try {
      actionService.deleteAllActions();
    } catch (Exception ignored) {
    }
    try {
      dynamicActionService.deleteAllDynamicActions();
    } catch (Exception ignored) {
    }
  }

  @Test
  void constructorCopiesBaseFields() {
    // given
    ControlPanelDashboardGroupDTO src = new ControlPanelDashboardGroupDTO();
    src.setId("grp-1");
    src.setName("Group Name");
    src.setDescription("Desc");
    src.setPosition(3);
    src.setAssociatedActions(Set.of(actionService.createAction(actionModel("A1"))));
    src.setAssociatedDynamicActions(
        Set.of(dynamicActionService.createDynamicAction(dynModel("D1"))));

    // when
    ControlPanelDashboardGroupConfigurationDTO cfg =
        new ControlPanelDashboardGroupConfigurationDTO(src);

    // then
    assertThat(cfg.getId()).isEqualTo("grp-1");
    assertThat(cfg.getName()).isEqualTo("Group Name");
    assertThat(cfg.getDescription()).isEqualTo("Desc");
    assertThat(cfg.getPosition()).isEqualTo(3);
  }

  @Test
  void toControlPanelDashboardGroupDTO_resolvesAssociations_andFiltersMissing_withRealServices() {
    // prepare DB: create resolvable ones; create "missing" only as non-persisted DTOs
    ActionDTO aOk1 = actionService.createAction(actionModel("A_OK1"));
    ActionDTO aOk2 = actionService.createAction(actionModel("A_OK2"));
    // Non-persisted DTO to act as missing:
    ActionDTO aMissing = actionService.createActionDTOFromAction(actionModel("A_MISSING"));

    DynamicActionDTO dOk = dynamicActionService.createDynamicAction(dynModel("D_OK"));
    DynamicActionDTO dMissing =
        dynamicActionService.createDynamicActionDTOFromDynamicAction(dynModel("D_MISSING"));

    // given
    ControlPanelDashboardGroupDTO src = new ControlPanelDashboardGroupDTO();
    src.setId("grp-2");
    src.setName("G2");
    src.setDescription("D2");
    src.setPosition(7);
    src.setAssociatedActions(Set.of(aOk1, aMissing, aOk2));
    src.setAssociatedDynamicActions(Set.of(dOk, dMissing));

    ControlPanelDashboardGroupConfigurationDTO cfg =
        new ControlPanelDashboardGroupConfigurationDTO(src);
    // inject real services (DTO isn't a Spring bean)
    cfg.setActionService(actionService);
    cfg.setDynamicActionService(dynamicActionService);

    // when
    ControlPanelDashboardGroupDTO out = cfg.toControlPanelDashboardGroupDTO();

    // then: base fields
    assertThat(out.getName()).isEqualTo("G2");
    assertThat(out.getDescription()).isEqualTo("D2");
    assertThat(out.getPosition()).isEqualTo(7);

    // associated actions resolved (missing filtered)
    Set<String> actionNames =
        out.getAssociatedActions().stream().map(ActionDTO::getName).collect(Collectors.toSet());
    assertThat(actionNames).containsExactlyInAnyOrder("A_OK1", "A_OK2");

    // associated dynamic actions resolved (missing filtered)
    Set<String> dynNames =
        out.getAssociatedDynamicActions().stream()
            .map(DynamicActionDTO::getName)
            .collect(Collectors.toSet());
    assertThat(dynNames).containsExactly("D_OK");
  }

  @Test
  void toControlPanelDashboardGroupDTO_whenNullAssociations_returnsEmptySets() {
    // given source with null association sets
    ControlPanelDashboardGroupDTO src = new ControlPanelDashboardGroupDTO();
    src.setId("grp-3");
    src.setName("Nulls");
    src.setDescription("None");
    src.setPosition(0);
    src.setAssociatedActions(null);
    src.setAssociatedDynamicActions(null);

    ControlPanelDashboardGroupConfigurationDTO cfg =
        new ControlPanelDashboardGroupConfigurationDTO(src);
    cfg.setActionService(actionService);
    cfg.setDynamicActionService(dynamicActionService);

    // when
    ControlPanelDashboardGroupDTO out = cfg.toControlPanelDashboardGroupDTO();

    // then
    assertThat(out.getAssociatedActions()).isNotNull().isEmpty();
    assertThat(out.getAssociatedDynamicActions()).isNotNull().isEmpty();
  }
}
