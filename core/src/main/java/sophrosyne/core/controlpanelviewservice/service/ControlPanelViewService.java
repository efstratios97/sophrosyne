package sophrosyne.core.controlpanelviewservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sophrosyne.core.actionservice.service.ActionService;
import sophrosyne.core.controlpanelservice.dto.ControlPanelDTO;
import sophrosyne.core.controlpanelservice.service.ControlPanelDashboardGroupService;
import sophrosyne.core.controlpanelservice.service.ControlPanelDashboardService;
import sophrosyne.core.controlpanelservice.service.ControlPanelService;
import sophrosyne.core.dynamicactionservice.service.DynamicActionService;
import sophrosyne.core.userservice.dto.UserDTO;
import sophrosyne_api.core.actionservice.model.Action;
import sophrosyne_api.core.controlpaneldashboardgroupservice.model.ControlPanelDashboardGroup;
import sophrosyne_api.core.controlpaneldashboardservice.model.ControlPanelDashboard;
import sophrosyne_api.core.controlpanelservice.model.ControlPanel;
import sophrosyne_api.core.dynamicactionservice.model.DynamicAction;

@Service
public class ControlPanelViewService {

    @Autowired
    ControlPanelDashboardService
            controlPanelDashboardService;

    @Autowired
    ControlPanelService controlPanelService;

    @Autowired
    ControlPanelDashboardGroupService controlPanelDashboardGroupService;

    @Autowired
    ActionService actionService;

    @Autowired
    DynamicActionService dynamicActionService;

    public Object createPanelViewByUser(UserDTO userDTO) {
        ControlPanelDTO controlPanelDTO =
                userDTO.getControlPanelDTO();

        ControlPanel controlPanel =
                controlPanelService.mapControlPanelDtoToControlPanel(controlPanelDTO);
        controlPanelDTO
                .getAssociatedControlPanelDashboards()
                .forEach(
                        controlPanelDashboardDTO -> {
                            ControlPanelDashboard controlPanelDashboard =
                                    controlPanelDashboardService.mapControlPanelDashboardDTOToControlPanelDashboard(
                                            controlPanelDashboardDTO);
                            controlPanelDashboard
                                    .getAssociatedControlPanelDashboardGroups()
                                    .forEach(
                                            controlPanelDashboardGroupId -> {
                                                if (controlPanelDashboardGroupService
                                                        .getControlPanelDashboardGroupDTOById(controlPanelDashboardGroupId)
                                                        .isPresent()) {
                                                    ControlPanelDashboardGroup controlPanelDashboardGroup =
                                                            controlPanelDashboardGroupService
                                                                    .mapControlPanelDashboardGroupDTOToControlPanelDashboardGroup(
                                                                            controlPanelDashboardGroupService
                                                                                    .getControlPanelDashboardGroupDTOById(
                                                                                            controlPanelDashboardGroupId)
                                                                                    .get());

                                                    controlPanelDashboardGroup
                                                            .getAssociatedActions()
                                                            .forEach(
                                                                    actionId -> {
                                                                        if (actionService.getActionDTOByID(actionId).isPresent()) {

                                                                            Action action =
                                                                                    actionService.mapActionDTOToAction(
                                                                                            actionService.getActionDTOByID(actionId).get());

                                                                            controlPanelDashboardGroup.addAssociatedActionObjectsItem(
                                                                                    action);
                                                                        }
                                                                    });
                                                    controlPanelDashboardGroup
                                                            .getAssociatedDynamicActions()
                                                            .forEach(
                                                                    dynamicActionId -> {
                                                                        if (dynamicActionService
                                                                                .getDynamicActionDTOByID(dynamicActionId)
                                                                                .isPresent()) {

                                                                            DynamicAction dynamicAction =
                                                                                    dynamicActionService
                                                                                            .mapDynamicActionDTOToDynamicActionService(
                                                                                                    dynamicActionService
                                                                                                            .getDynamicActionDTOByID(dynamicActionId)
                                                                                                            .get());
                                                                            controlPanelDashboardGroup
                                                                                    .addAssociatedDynamicActionObjectsItem(dynamicAction);
                                                                        }
                                                                    });
                                                    controlPanelDashboard.addAssociatedControlPanelDashboardGroupObjectsItem(
                                                            controlPanelDashboardGroup);
                                                    controlPanelDashboard
                                                            .getAssociatedControlPanelDashboardGroupObjects()
                                                            .sort(
                                                                    (o1, o2) -> {
                                                                        ControlPanelDashboardGroup controlPanelDashboardGroup1 =
                                                                                (ControlPanelDashboardGroup) o1;
                                                                        ControlPanelDashboardGroup controlPanelDashboardGroup2 =
                                                                                (ControlPanelDashboardGroup) o2;
                                                                        return Integer.compare(
                                                                                controlPanelDashboardGroup1.getPosition(),
                                                                                controlPanelDashboardGroup2.getPosition());
                                                                    });
                                                }
                                            });
                            controlPanel.addAssociatedControlPanelDashboardObjectsItem(controlPanelDashboard);
                        });
        controlPanel
                .getAssociatedControlPanelDashboardObjects()
                .sort(
                        (o1, o2) -> {
                            ControlPanelDashboard controlPanelDashboard1 = (ControlPanelDashboard) o1;
                            ControlPanelDashboard controlPanelDashboard2 = (ControlPanelDashboard) o2;
                            return Integer.compare(
                                    controlPanelDashboard1.getPosition(), controlPanelDashboard2.getPosition());
                        });
        return controlPanel;
    }
}
