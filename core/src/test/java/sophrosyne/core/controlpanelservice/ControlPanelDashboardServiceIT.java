package sophrosyne.core.controlpanelservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import sophrosyne.core.PostgresIntegrationTestBase;
import sophrosyne.core.controlpanelservice.dto.ControlPanelDashboardDTO;
import sophrosyne.core.controlpanelservice.service.ControlPanelDashboardService;
import sophrosyne.core.utils.ControlPanelTestUtils;
import sophrosyne_api.core.controlpaneldashboardservice.model.ControlPanelDashboard;

@SpringBootTest
@TestPropertySource(locations = "/application-test.properties")
public class ControlPanelDashboardServiceIT extends PostgresIntegrationTestBase {

  @Autowired ControlPanelDashboardService sut_controlPanelDashboard;

  @Autowired ControlPanelTestUtils controlPanelTestUtils;

  @BeforeEach
  @AfterEach
  public void setUp() {
    sut_controlPanelDashboard.deleteAllControlPanelDashboards();
    controlPanelTestUtils.setUp();
  }

  @Test
  public void test_createControlPanelDashboard() {
    ControlPanelDashboard controlPanelDashboard =
        controlPanelTestUtils.createControlPanelDashboard(0, 0, "Test");
    ControlPanelDashboardDTO controlPanelDashboardDTO =
        sut_controlPanelDashboard.createControlPanelDashboard(controlPanelDashboard);

    assertThat(controlPanelDashboardDTO.getName()).isEqualTo(controlPanelDashboard.getName());
    assertThat(controlPanelDashboardDTO.getDescription())
        .isEqualTo(controlPanelDashboard.getDescription());
    assertThat(controlPanelDashboardDTO.getAssociatedControlPanelGroups())
        .hasSize(1)
        .doesNotContainNull();
  }

  @Test
  public void test_updateControlPanelDashboard() {
    ControlPanelDashboard controlPanelDashboard =
        controlPanelTestUtils.createControlPanelDashboard(0, 0, "Test");

    ControlPanelDashboardDTO controlPanelDashboardDTO =
        sut_controlPanelDashboard.createControlPanelDashboard(controlPanelDashboard);

    controlPanelDashboard.setId(controlPanelDashboardDTO.getId());
    controlPanelDashboard.setName("NEW_NAME");
    controlPanelDashboard.setDescription("NEW_DESCRIPTION");

    ControlPanelDashboardDTO updated_controlPanelDashboardDTO =
        sut_controlPanelDashboard.updateControlPanelDashboard(controlPanelDashboard);

    assertThat(updated_controlPanelDashboardDTO.getId()).isEqualTo(controlPanelDashboard.getId());
    assertThat(updated_controlPanelDashboardDTO.getName())
        .isEqualTo(controlPanelDashboard.getName());
    assertThat(updated_controlPanelDashboardDTO.getDescription())
        .isEqualTo(controlPanelDashboard.getDescription());
    assertThat(updated_controlPanelDashboardDTO.getAssociatedControlPanelGroups())
        .hasSize(1)
        .doesNotContainNull();
  }

  @Test
  public void test_deleteControlPanelDashboard() {
    ControlPanelDashboard controlPanelDashboard =
        controlPanelTestUtils.createControlPanelDashboard(0, 0, "Test");
    ControlPanelDashboardDTO controlPanelDashboardDTO =
        sut_controlPanelDashboard.createControlPanelDashboard(controlPanelDashboard);

    sut_controlPanelDashboard.deleteControlPanelDashboard(controlPanelDashboardDTO);

    assertThat(
            sut_controlPanelDashboard
                .getControlPanelDashboardDTOById(controlPanelDashboardDTO.getId())
                .isPresent())
        .isFalse();
  }

  @Test
  public void test_getControlPanelDashboardById() {
    ControlPanelDashboard controlPanelDashboard =
        controlPanelTestUtils.createControlPanelDashboard(0, 0, "Test");
    ControlPanelDashboardDTO controlPanelDashboardDTO =
        sut_controlPanelDashboard.createControlPanelDashboard(controlPanelDashboard);

    Optional<ControlPanelDashboardDTO> sut_controlPanelDashboardDTO =
        sut_controlPanelDashboard.getControlPanelDashboardDTOById(controlPanelDashboardDTO.getId());

    assertThat(sut_controlPanelDashboardDTO.isPresent()).isTrue();
    assertThat(sut_controlPanelDashboardDTO.get().getId())
        .isEqualTo(controlPanelDashboardDTO.getId());
    assertThat(sut_controlPanelDashboardDTO.get().getName())
        .isEqualTo(controlPanelDashboardDTO.getName());
    assertThat(sut_controlPanelDashboardDTO.get().getDescription())
        .isEqualTo(controlPanelDashboardDTO.getDescription());
    assertThat(sut_controlPanelDashboardDTO.get().getAssociatedControlPanelGroups())
        .hasSize(1)
        .doesNotContainNull();
  }

  @Test
  public void test_getControlPanelById_WhenNotExists() {

    Optional<ControlPanelDashboardDTO> sut_controlPanelDTO =
        sut_controlPanelDashboard.getControlPanelDashboardDTOById("FAKE_ID");

    assertThat(sut_controlPanelDTO.isPresent()).isFalse();
  }
}
