package sophrosyne.core.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sophrosyne.core.actionservice.service.ActionService;
import sophrosyne.core.apikeyservice.service.ApikeyService;
import sophrosyne.core.controlpanelservice.dto.ControlPanelDashboardDTO;
import sophrosyne.core.controlpanelservice.dto.ControlPanelDashboardGroupDTO;
import sophrosyne.core.controlpanelservice.service.ControlPanelDashboardGroupService;
import sophrosyne.core.controlpanelservice.service.ControlPanelDashboardService;
import sophrosyne.core.controlpanelservice.service.ControlPanelService;
import sophrosyne.core.dynamicactionservice.service.DynamicActionService;
import sophrosyne.core.userservice.service.UserService;
import sophrosyne_api.core.actionservice.model.Action;
import sophrosyne_api.core.controlpaneldashboardgroupservice.model.ControlPanelDashboardGroup;
import sophrosyne_api.core.controlpaneldashboardservice.model.ControlPanelDashboard;
import sophrosyne_api.core.controlpanelservice.model.ControlPanel;
import sophrosyne_api.core.dynamicactionservice.model.DynamicAction;
import sophrosyne_api.core.userservice.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ControlPanelTestUtils {
    private final Logger logger =
            LogManager.getLogger(getClass());

    @Autowired
    ControlPanelDashboardService
            controlPanelDashboardService;

    @Autowired
    ControlPanelService controlPanelService;

    @Autowired
    UserService userService;

    @Autowired
    ControlPanelDashboardGroupService
            controlPanelDashboardGroupService;

    @Autowired
    ActionService actionService;

    @Autowired
    DynamicActionService dynamicActionService;

    @Autowired
    ApikeyService apikeyService;

    public User createUserAPIModel() {
        return new User()
                .email("test@test.de")
                .firstName("Lutz")
                .lastName("Schmutzfinger")
                .role(User.RoleEnum.ADMIN)
                .password("12345")
                .username("Lschmutzi18");
    }

    public ControlPanel createControlPanel() {
        ControlPanelDashboardDTO controlPanelDashboardDTO =
                controlPanelDashboardService.createControlPanelDashboard(
                        createControlPanelDashboard(6, 6, "Second"));
        controlPanelDashboardDTO.setName("Fourth");
        ControlPanelDashboardDTO controlPanelDashboardDTO2 =
                controlPanelDashboardService.createControlPanelDashboard(
                        createControlPanelDashboard(1, 7, "Second2"));
        controlPanelDashboardDTO2.setName("Second");
        ControlPanelDashboardDTO controlPanelDashboardDTO3 =
                controlPanelDashboardService.createControlPanelDashboard(
                        createControlPanelDashboard(4, 0, "First"));
        controlPanelDashboardDTO3.setName("Third");
        ControlPanelDashboardDTO controlPanelDashboardDTO4 =
                controlPanelDashboardService.createControlPanelDashboard(
                        createControlPanelDashboard(0, 4, "First2"));
        controlPanelDashboardDTO4.setName("First");
        return new ControlPanel()
                .name("Test")
                .description("Description")
                .associatedUsers(
                        new ArrayList<>() {
                            {
                                add(createUserAPIModel().getUsername());
                                add("admin");
                            }
                        })
                .addAssociatedControlPanelDashboardsItem(controlPanelDashboardDTO.getId())
                .addAssociatedControlPanelDashboardsItem(controlPanelDashboardDTO2.getId())
                .addAssociatedControlPanelDashboardsItem(controlPanelDashboardDTO3.getId())
                .addAssociatedControlPanelDashboardsItem(controlPanelDashboardDTO4.getId());
    }

    public ControlPanelDashboardGroup createControlPanelDashboardGroup(
            String nameControlPanelDashboardGroup, int position) {
        return new ControlPanelDashboardGroup()
                .name(nameControlPanelDashboardGroup)
                .description("Description")
                .addAssociatedDynamicActionsItem(
                        dynamicActionService.createDynamicAction(createDynamicAction()).getId())
                .addAssociatedActionsItem(actionService.createAction(createAPIAction()).getId())
                .position(position);
    }

    public ControlPanelDashboard createControlPanelDashboard(
            int positionControlPanelDashboard,
            int positionControlPanelDashboardGroup,
            String nameControlPanelDashboardGroup) {
        ControlPanelDashboardGroupDTO
                standardControlPanelDashboardGroupDTO =
                controlPanelDashboardGroupService.createControlPanelDashboardGroup(
                        createControlPanelDashboardGroup(UUID.randomUUID().toString(), 5));
        ControlPanelDashboardGroupDTO
                controlPanelDashboardGroupDTO =
                controlPanelDashboardGroupService.createControlPanelDashboardGroup(
                        createControlPanelDashboardGroup(
                                nameControlPanelDashboardGroup, positionControlPanelDashboardGroup));
        return new ControlPanelDashboard()
                .name(UUID.randomUUID().toString())
                .description("DESC")
                .addAssociatedControlPanelDashboardGroupsItem(controlPanelDashboardGroupDTO.getId())
                .addAssociatedControlPanelDashboardGroupObjectsItem(standardControlPanelDashboardGroupDTO)
                .position(positionControlPanelDashboard);
    }

    public DynamicAction createDynamicAction() {
        return new DynamicAction()
                .name(UUID.randomUUID().toString())
                .description("Test_Description")
                .command("ansible-playbook")
                .dynamicParameters("-i {{inventory}} {{playbook}}")
                .allowedApikeys(List.of("apikey_fictional"))
                .postExecutionLogFilePath("/etc/file/")
                .version("v.1.2.3")
                .requiresConfirmation(1);
    }

    public Action createAPIAction() {
        return new Action()
                .name(UUID.randomUUID().toString())
                .description("Test_Description")
                .command("ansible-playbook")
                .allowedApikeys(List.of("apikey_fictional"))
                .postExecutionLogFilePath("/etc/file/")
                .requiresConfirmation(0)
                .version("v.1.2.3");
    }

    public void setUp() {
        try {
            apikeyService.deleteAllApikeys();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        apikeyService.generateAPIKey("apikey_fictional", "test", 1);
        try {
            userService.deleteUserByUsername(createUserAPIModel().getUsername());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        try {
            userService.createUser(createUserAPIModel());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        try {
            dynamicActionService.deleteAllDynamicActions();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        try {
            actionService.deleteAllActions();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        try {
            controlPanelService.deleteAllControlPanels();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        try {
            controlPanelDashboardService.deleteAllControlPanelDashboards();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        try {
            controlPanelDashboardGroupService.deleteAllControlPanelDashboardGroups();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
