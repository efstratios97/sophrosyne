package sophrosyne.core.controlpanelservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import sophrosyne.core.PostgresIntegrationTestBase;
import sophrosyne.core.controlpanelservice.dto.ControlPanelDTO;
import sophrosyne.core.controlpanelservice.service.ControlPanelDashboardService;
import sophrosyne.core.controlpanelservice.service.ControlPanelService;
import sophrosyne.core.userservice.service.UserService;
import sophrosyne.core.utils.ControlPanelTestUtils;
import sophrosyne_api.core.controlpanelservice.model.ControlPanel;

@SpringBootTest
@TestPropertySource(locations = "/application-test.properties")
public class ControlPanelServiceIT extends PostgresIntegrationTestBase {

  @Autowired ControlPanelService sut_controlPanelService;

  @Autowired ControlPanelTestUtils controlPanelTestUtils;
  @Autowired UserService userService;

  @Autowired ControlPanelDashboardService controlPanelDashboardService;

  @BeforeEach
  @AfterEach
  public void setUp() {
    sut_controlPanelService.deleteAllControlPanels();
    controlPanelTestUtils.setUp();
  }

  @Test
  public void test_createControlPanel() {
    ControlPanel controlPanel = controlPanelTestUtils.createControlPanel();
    ControlPanelDTO controlPanelDTO = sut_controlPanelService.createControlPanel(controlPanel);

    assertThat(controlPanelDTO.getName()).isEqualTo(controlPanel.getName());
    assertThat(controlPanelDTO.getDescription()).isEqualTo(controlPanel.getDescription());
    org.assertj.core.api.Assertions.assertThat(controlPanelDTO.getAssociatedUsers())
        .hasSize(2)
        .doesNotContainNull();
    assertThat(controlPanelDTO.getAssociatedControlPanelDashboards())
        .hasSize(4)
        .doesNotContainNull();
  }

  @Test
  public void test_updateControlPanel() {
    ControlPanel controlPanel = controlPanelTestUtils.createControlPanel();
    ControlPanelDTO controlPanelDTO = sut_controlPanelService.createControlPanel(controlPanel);

    controlPanel.setName("NEW_NAME");
    controlPanel.setDescription("NEW_DESCRIPTION");
    controlPanel.setId(controlPanelDTO.getId());
    controlPanel.associatedUsers(
        new ArrayList<>() {
          {
            add("admin");
          }
        });

    controlPanelDTO = sut_controlPanelService.updateControlPanel(controlPanel);

    assertThat(controlPanelDTO.getId()).isEqualTo(controlPanel.getId());
    assertThat(controlPanelDTO.getName()).isEqualTo(controlPanel.getName());
    assertThat(controlPanelDTO.getDescription()).isEqualTo(controlPanel.getDescription());
    org.assertj.core.api.Assertions.assertThat(controlPanelDTO.getAssociatedUsers())
        .hasSize(controlPanel.getAssociatedUsers().size());
  }

  @Test
  public void test_getControlPanelById() {
    ControlPanelDTO controlPanelDTO =
        sut_controlPanelService.createControlPanel(controlPanelTestUtils.createControlPanel());

    Optional<ControlPanelDTO> sut_controlPanelDTO =
        sut_controlPanelService.getControlPanelById(controlPanelDTO.getId());

    assertThat(sut_controlPanelDTO.isPresent()).isTrue();
    assertThat(controlPanelDTO.getName()).isEqualTo(sut_controlPanelDTO.get().getName());
    assertThat(controlPanelDTO.getDescription())
        .isEqualTo(sut_controlPanelDTO.get().getDescription());
    org.assertj.core.api.Assertions.assertThat(sut_controlPanelDTO.get().getAssociatedUsers())
        .hasSize(2);
  }

  @Test
  public void test_deleteControlPanel() {
    ControlPanelDTO controlPanelDTO =
        sut_controlPanelService.createControlPanel(controlPanelTestUtils.createControlPanel());

    sut_controlPanelService.deleteControlPanel(controlPanelDTO);

    assertThat(sut_controlPanelService.getControlPanelById(controlPanelDTO.getId()).isPresent())
        .isFalse();
  }

  @Test
  public void test_getControlPanelById_WhenNotExists() {

    Optional<ControlPanelDTO> sut_controlPanelDTO =
        sut_controlPanelService.getControlPanelById("FAKE_ID");

    assertThat(sut_controlPanelDTO.isPresent()).isFalse();
  }
}
