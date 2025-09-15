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
import sophrosyne.core.configurationservice.dto.ControlPanelDashboardConfigurationDTO;
import sophrosyne.core.controlpanelservice.dto.ControlPanelDashboardDTO;
import sophrosyne.core.controlpanelservice.dto.ControlPanelDashboardGroupDTO;
import sophrosyne.core.controlpanelservice.service.ControlPanelDashboardGroupService;
import sophrosyne_api.core.controlpaneldashboardgroupservice.model.ControlPanelDashboardGroup;

@SpringBootTest
@TestPropertySource(locations = "/application-test.properties")
class ControlPanelDashboardConfigurationDTOTest extends PostgresIntegrationTestBase {

  @Autowired ControlPanelDashboardGroupService controlPanelDashboardGroupService;

  // --- helpers to create simple API models for tests ---
  private static ControlPanelDashboardGroup group(String name) {
    ControlPanelDashboardGroup g = new ControlPanelDashboardGroup();
    g.setId(name);
    g.setName(name);
    g.setDescription("desc-" + name);
    g.setPosition(0);
    g.setAssociatedActions(new ArrayList<>());
    g.setAssociatedDynamicActions(new ArrayList<>());
    return g;
  }

  @BeforeEach
  void setup() {
    try {
      controlPanelDashboardGroupService.deleteAllControlPanelDashboardGroups();
    } catch (Exception ignored) {
    }
  }

  @AfterEach
  void tearDown() {
    try {
      controlPanelDashboardGroupService.deleteAllControlPanelDashboardGroups();
    } catch (Exception ignored) {
    }
  }

  @Test
  void constructorCopiesBaseFields() {
    // given
    ControlPanelDashboardDTO src = new ControlPanelDashboardDTO();
    src.setId("dash-1");
    src.setName("Main Dashboard");
    src.setDescription("desc");
    src.setPosition(10);
    src.setAssociatedControlPanelGroups(
        Set.of(controlPanelDashboardGroupService.createControlPanelDashboardGroup(group("G1"))));

    // when
    ControlPanelDashboardConfigurationDTO cfg = new ControlPanelDashboardConfigurationDTO(src);

    // then
    assertThat(cfg.getId()).isEqualTo("dash-1");
    assertThat(cfg.getName()).isEqualTo("Main Dashboard");
    assertThat(cfg.getDescription()).isEqualTo("desc");
    assertThat(cfg.getPosition()).isEqualTo(10);
  }

  @Test
  void toControlPanelDashboardDTO_resolvesGroups_andFiltersMissing_withRealService() {
    // prepare DB: create the groups that should resolve; leave the “missing” one unpersisted
    ControlPanelDashboardGroupDTO gOk1 =
        controlPanelDashboardGroupService.createControlPanelDashboardGroup(group("G_OK1"));
    ControlPanelDashboardGroupDTO gOk2 =
        controlPanelDashboardGroupService.createControlPanelDashboardGroup(group("G_OK2"));
    // create a DTO for a non-persisted group (acts as “missing”)
    ControlPanelDashboardGroupDTO gMissing =
        controlPanelDashboardGroupService.createControlPanelGroupDTOFromControlPanelDashboardGroup(
            group("G_MISSING"));

    // given
    ControlPanelDashboardDTO src = new ControlPanelDashboardDTO();
    src.setId("dash-2");
    src.setName("D2");
    src.setDescription("D2-desc");
    src.setPosition(3);
    src.setAssociatedControlPanelGroups(Set.of(gOk1, gMissing, gOk2));

    ControlPanelDashboardConfigurationDTO cfg = new ControlPanelDashboardConfigurationDTO(src);
    // inject real service (DTO isn’t a Spring bean)
    cfg.setControlPanelDashboardGroupService(controlPanelDashboardGroupService);

    // when
    ControlPanelDashboardDTO out = cfg.toControlPanelDashboardDTO();

    // then base fields
    assertThat(out.getId()).isEqualTo("dash-2");
    assertThat(out.getName()).isEqualTo("D2");
    assertThat(out.getDescription()).isEqualTo("D2-desc");
    assertThat(out.getPosition()).isEqualTo(3);

    // groups resolved, missing filtered
    Set<String> names =
        out.getAssociatedControlPanelGroups().stream()
            .map(ControlPanelDashboardGroupDTO::getName)
            .collect(Collectors.toSet());
    assertThat(names).containsExactlyInAnyOrder("G_OK1", "G_OK2");
  }

  @Test
  void toControlPanelDashboardDTO_whenNullGroups_returnsEmptySet() {
    // given
    ControlPanelDashboardDTO src = new ControlPanelDashboardDTO();
    src.setId("dash-3");
    src.setName("NoGroups");
    src.setDescription("none");
    src.setPosition(0);
    src.setAssociatedControlPanelGroups(null);

    ControlPanelDashboardConfigurationDTO cfg = new ControlPanelDashboardConfigurationDTO(src);
    cfg.setControlPanelDashboardGroupService(controlPanelDashboardGroupService);

    // when
    ControlPanelDashboardDTO out = cfg.toControlPanelDashboardDTO();

    // then
    assertThat(out.getAssociatedControlPanelGroups()).isNotNull().isEmpty();
  }
}
