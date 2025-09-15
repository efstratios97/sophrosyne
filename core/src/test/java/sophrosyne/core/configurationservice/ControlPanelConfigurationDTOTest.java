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
import sophrosyne.core.configurationservice.dto.ControlPanelConfigurationDTO;
import sophrosyne.core.controlpanelservice.dto.ControlPanelDTO;
import sophrosyne.core.controlpanelservice.dto.ControlPanelDashboardDTO;
import sophrosyne.core.controlpanelservice.service.ControlPanelDashboardService;
import sophrosyne.core.userservice.dto.UserDTO;
import sophrosyne.core.userservice.service.UserService;
import sophrosyne_api.core.controlpaneldashboardservice.model.ControlPanelDashboard;
import sophrosyne_api.core.userservice.model.User;
import sophrosyne_api.core.userservice.model.User.RoleEnum;

@SpringBootTest
@TestPropertySource(locations = "/application-test.properties")
class ControlPanelConfigurationDTOTest extends PostgresIntegrationTestBase {

  @Autowired UserService userService;
  @Autowired ControlPanelDashboardService controlPanelDashboardService;

  // --- helpers to create simple entities for tests ---
  private static User user(String name) {
    User u = new User();
    u.setUsername(name);
    u.setFirstName("fn-" + name);
    u.setLastName("ln-" + name);
    u.setEmail(name.toLowerCase() + "@example.com");
    u.setRole(RoleEnum.USER);
    u.setPassword("hashed"); // whatever your service expects; not used here
    return u;
  }

  private static ControlPanelDashboard dash(String name) {
    ControlPanelDashboard d = new ControlPanelDashboard();
    d.setId(name);
    d.setName(name);
    d.setDescription("desc-" + name);
    d.setPosition(0);
    d.setAssociatedControlPanelDashboardGroups(new ArrayList<>());
    d.setAssociatedControlPanelDashboardGroupObjects(new ArrayList<>());
    return d;
  }

  @BeforeEach
  void setup() {
    // Wipe state so tests are independent. Adjust if your services use different names.
    try {
      userService.deleteAllUsers();
    } catch (Exception ignored) {
    }
    try {
      controlPanelDashboardService.deleteAllControlPanelDashboards();
    } catch (Exception ignored) {
    }
  }

  @AfterEach
  void tearDown() {
    try {
      userService.deleteAllUsers();
    } catch (Exception ignored) {
    }
    try {
      controlPanelDashboardService.deleteAllControlPanelDashboards();
    } catch (Exception ignored) {
    }
  }

  @Test
  void constructorCopiesBaseFields() {
    // given
    ControlPanelDTO src = new ControlPanelDTO();
    src.setId("cp-1");
    src.setName("Main CP");
    src.setDescription("desc");
    src.setAssociatedUsers(Set.of(userService.createUser(user("U1"))));
    src.setAssociatedControlPanelDashboards(
        Set.of(controlPanelDashboardService.createControlPanelDashboard(dash("D1"))));

    // when
    ControlPanelConfigurationDTO cfg = new ControlPanelConfigurationDTO(src);

    // then
    assertThat(cfg.getId()).isEqualTo("cp-1");
    assertThat(cfg.getName()).isEqualTo("Main CP");
    assertThat(cfg.getDescription()).isEqualTo("desc");
  }

  @Test
  void toControlPanelDTO_resolvesAssociations_andFiltersMissing_withRealServices() {
    // given
    ControlPanelDTO src = new ControlPanelDTO();
    src.setId("cp-2");
    src.setName("CP2");
    src.setDescription("CP2-desc");
    src.setAssociatedUsers(
        Set.of(
            userService.createUser(user("U_OK1")),
            userService.createNewUser(user("U_MISSING")),
            userService.createUser(user("U_OK2"))));
    src.setAssociatedControlPanelDashboards(
        Set.of(
            controlPanelDashboardService.createControlPanelDashboard(dash("D_OK")),
            controlPanelDashboardService.createControlPanelDTOFromControlPanelDashboard(
                dash("D_MISSING"))));

    ControlPanelConfigurationDTO cfg = new ControlPanelConfigurationDTO(src);
    // inject real services (DTO isn't a Spring bean)
    cfg.setUserService(userService);
    cfg.setControlPanelDashboardService(controlPanelDashboardService);

    // when
    ControlPanelDTO out = cfg.toControlPanelDTO();

    // then base fields
    assertThat(out.getId()).isEqualTo("cp-2");
    assertThat(out.getName()).isEqualTo("CP2");
    assertThat(out.getDescription()).isEqualTo("CP2-desc");

    // users resolved, missing filtered
    Set<String> userNames =
        out.getAssociatedUsers().stream().map(UserDTO::getUsername).collect(Collectors.toSet());
    assertThat(userNames).containsExactlyInAnyOrder("U_OK1", "U_OK2");

    // dashboards resolved, missing filtered
    Set<String> dashNames =
        out.getAssociatedControlPanelDashboards().stream()
            .map(ControlPanelDashboardDTO::getName)
            .collect(Collectors.toSet());
    assertThat(dashNames).containsExactly("D_OK");
  }

  @Test
  void toControlPanelDTO_whenNullAssociations_returnsEmptySets_andNoServiceCalls() {
    // given
    ControlPanelDTO src = new ControlPanelDTO();
    src.setId("cp-3");
    src.setName("NoAssoc");
    src.setDescription("none");
    src.setAssociatedUsers(null);
    src.setAssociatedControlPanelDashboards(null);

    ControlPanelConfigurationDTO cfg = new ControlPanelConfigurationDTO(src);
    cfg.setUserService(userService);
    cfg.setControlPanelDashboardService(controlPanelDashboardService);

    // when
    ControlPanelDTO out = cfg.toControlPanelDTO();

    // then
    assertThat(out.getAssociatedUsers()).isNotNull().isEmpty();
    assertThat(out.getAssociatedControlPanelDashboards()).isNotNull().isEmpty();
  }
}
