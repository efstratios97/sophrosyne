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
import sophrosyne.core.controlpanelservice.dto.ControlPanelDashboardGroupDTO;
import sophrosyne.core.controlpanelservice.service.ControlPanelDashboardGroupService;
import sophrosyne.core.utils.ControlPanelTestUtils;
import sophrosyne_api.core.controlpaneldashboardgroupservice.model.ControlPanelDashboardGroup;

@SpringBootTest
@TestPropertySource(locations = "/application-test.properties")
public class ControlPanelServiceDashboardGroupIT extends PostgresIntegrationTestBase {

  @Autowired ControlPanelDashboardGroupService sut_controlPanelDashboardGroupService;

  @Autowired ControlPanelTestUtils controlPanelTestUtils;

  @BeforeEach
  @AfterEach
  public void setUp() {
    sut_controlPanelDashboardGroupService.deleteAllControlPanelDashboardGroups();
    controlPanelTestUtils.setUp();
  }

  @Test
  public void test_createControlPanelDashboardGroup() {
    ControlPanelDashboardGroupDTO controlPanelDashboardGroupDTO =
        sut_controlPanelDashboardGroupService.createControlPanelDashboardGroup(
            controlPanelTestUtils.createControlPanelDashboardGroup("Test2", 0));

    assertThat(controlPanelDashboardGroupDTO.getName())
        .isEqualTo(controlPanelTestUtils.createControlPanelDashboardGroup("Test2", 3).getName());
    assertThat(controlPanelDashboardGroupDTO.getDescription())
        .isEqualTo(
            controlPanelTestUtils.createControlPanelDashboardGroup("Test3", 1).getDescription());
    org.assertj.core.api.Assertions.assertThat(controlPanelDashboardGroupDTO.getAssociatedActions())
        .hasSize(1)
        .doesNotContainNull();
    org.assertj.core.api.Assertions.assertThat(
            controlPanelDashboardGroupDTO.getAssociatedDynamicActions())
        .hasSize(1)
        .doesNotContainNull();
  }

  @Test
  public void test_updateControlPanelDashboardGroup() {
    ControlPanelDashboardGroup controlPanelDashboardGroup =
        controlPanelTestUtils.createControlPanelDashboardGroup("Test2", 1);

    ControlPanelDashboardGroupDTO controlPanelDashboardGroupDTO =
        sut_controlPanelDashboardGroupService.createControlPanelDashboardGroup(
            controlPanelDashboardGroup);

    controlPanelDashboardGroup.setName("NEW_NAME");
    controlPanelDashboardGroup.setDescription("NEW_DESCRIPTION");
    controlPanelDashboardGroup.setId(controlPanelDashboardGroupDTO.getId());

    ControlPanelDashboardGroupDTO updated_controlPanelDashboardGroupDTO =
        sut_controlPanelDashboardGroupService.updateControlPanelDashboardGroup(
            controlPanelDashboardGroup);

    assertThat(updated_controlPanelDashboardGroupDTO.getId())
        .isEqualTo(controlPanelDashboardGroup.getId());
    assertThat(updated_controlPanelDashboardGroupDTO.getName())
        .isEqualTo(controlPanelDashboardGroup.getName());
    assertThat(updated_controlPanelDashboardGroupDTO.getDescription())
        .isEqualTo(controlPanelDashboardGroup.getDescription());
    org.assertj.core.api.Assertions.assertThat(
            updated_controlPanelDashboardGroupDTO.getAssociatedActions())
        .hasSize(1)
        .isNotNull()
        .doesNotContainNull();
    org.assertj.core.api.Assertions.assertThat(
            updated_controlPanelDashboardGroupDTO.getAssociatedDynamicActions())
        .hasSize(1)
        .isNotNull()
        .doesNotContainNull();
  }

  @Test
  public void test_getControlPanelDashboardGroupById() {
    ControlPanelDashboardGroupDTO controlPanelDashboardGroupDTO =
        sut_controlPanelDashboardGroupService.createControlPanelDashboardGroup(
            controlPanelTestUtils.createControlPanelDashboardGroup("Test", 2));

    Optional<ControlPanelDashboardGroupDTO> sut_controlPanelDashboardGroupDTO =
        sut_controlPanelDashboardGroupService.getControlPanelDashboardGroupDTOById(
            controlPanelDashboardGroupDTO.getId());

    assertThat(sut_controlPanelDashboardGroupDTO.isPresent()).isTrue();
    assertThat(controlPanelDashboardGroupDTO.getName())
        .isEqualTo(sut_controlPanelDashboardGroupDTO.get().getName());
    assertThat(controlPanelDashboardGroupDTO.getDescription())
        .isEqualTo(sut_controlPanelDashboardGroupDTO.get().getDescription());
    org.assertj.core.api.Assertions.assertThat(
            sut_controlPanelDashboardGroupDTO.get().getAssociatedDynamicActions())
        .hasSize(1)
        .isNotNull();
    org.assertj.core.api.Assertions.assertThat(
            sut_controlPanelDashboardGroupDTO.get().getAssociatedActions())
        .hasSize(1)
        .isNotNull();
  }

  @Test
  public void test_deleteControlPanel() {
    sophrosyne.core.controlpanelservice.dto.ControlPanelDashboardGroupDTO
        controlPanelDashboardGroupDTO =
            sut_controlPanelDashboardGroupService.createControlPanelDashboardGroup(
                controlPanelTestUtils.createControlPanelDashboardGroup("Test", 3));

    sut_controlPanelDashboardGroupService.deleteControlPanelDashboardGroup(
        controlPanelDashboardGroupDTO);

    assertThat(
            sut_controlPanelDashboardGroupService
                .getControlPanelDashboardGroupDTOById(controlPanelDashboardGroupDTO.getId())
                .isPresent())
        .isFalse();
  }

  @Test
  public void test_getControlPanelById_WhenNotExists() {

    Optional<sophrosyne.core.controlpanelservice.dto.ControlPanelDashboardGroupDTO>
        sut_controlPanelDashboardGroupDTO =
            sut_controlPanelDashboardGroupService.getControlPanelDashboardGroupDTOById("FAKE_ID");

    assertThat(sut_controlPanelDashboardGroupDTO.isPresent()).isFalse();
  }
}
