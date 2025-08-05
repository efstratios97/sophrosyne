package sophrosyne.core.controlpanelviewservice;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import sophrosyne.core.PostgresIntegrationTestBase;
import sophrosyne.core.controlpanelservice.service.ControlPanelService;
import sophrosyne.core.controlpanelviewservice.service.ControlPanelViewService;
import sophrosyne.core.userservice.service.UserService;
import sophrosyne.core.utils.ControlPanelTestUtils;
import sophrosyne_api.core.controlpaneldashboardgroupservice.model.ControlPanelDashboardGroup;
import sophrosyne_api.core.controlpaneldashboardservice.model.ControlPanelDashboard;
import sophrosyne_api.core.controlpanelservice.model.ControlPanel;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations = "/application-test.properties")
public class ControlPanelViewServiceIT extends PostgresIntegrationTestBase {

    @Autowired
    ControlPanelViewService
            sut_controlPanelViewService;

    @Autowired
    ControlPanelTestUtils controlPanelTestUtils;

    @Autowired
    ControlPanelService controlPanelService;

    @Autowired
    UserService userService;

    @BeforeEach
    @AfterEach
    public void setUp() {
        controlPanelTestUtils.setUp();
    }

    @Test
    public void test_createControlPanelView() {
        controlPanelService.createControlPanel(controlPanelTestUtils.createControlPanel());
        ControlPanel controlPanel =
                (ControlPanel)
                        sut_controlPanelViewService.createPanelViewByUser(
                                userService
                                        .getUserByUserName(controlPanelTestUtils.createUserAPIModel().getUsername())
                                        .get());
        List<ControlPanelDashboard> controlPanelDashboards = new ArrayList<>();
        controlPanel
                .getAssociatedControlPanelDashboardObjects()
                .forEach(
                        (o1) -> {
                            controlPanelDashboards.add((ControlPanelDashboard) o1);
                        });
        assertThat(controlPanelDashboards)
                .isSortedAccordingTo(
                        (controlPanelDashboard1, controlPanelDashboard2) -> {
                            return Integer.compare(
                                    controlPanelDashboard1.getPosition(), controlPanelDashboard2.getPosition());
                        });

        controlPanelDashboards.forEach(
                controlPanelDashboard -> {
                    assertThat(controlPanelDashboard.getAssociatedControlPanelDashboardGroupObjects())
                            .isSortedAccordingTo(
                                    (o1, o2) -> {
                                        ControlPanelDashboardGroup controlPanelDashboardGroup1 =
                                                (ControlPanelDashboardGroup) o1;
                                        ControlPanelDashboardGroup controlPanelDashboardGroup2 =
                                                (ControlPanelDashboardGroup) o2;
                                        return Integer.compare(
                                                controlPanelDashboardGroup1.getPosition(),
                                                controlPanelDashboardGroup2.getPosition());
                                    });
                });
    }
}
