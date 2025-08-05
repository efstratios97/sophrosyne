package sophrosyne.core.controlpanelservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import sophrosyne.core.actionservice.dto.ActionDTO;
import sophrosyne.core.actionservice.service.ActionService;
import sophrosyne.core.configurationservice.service.ConfigurationService;
import sophrosyne.core.controlpanelservice.dto.ControlPanelDashboardGroupDTO;
import sophrosyne.core.controlpanelservice.repository.ControlPanelDashboardGroupRepository;
import sophrosyne.core.dynamicactionservice.dto.DynamicActionDTO;
import sophrosyne.core.dynamicactionservice.service.DynamicActionService;
import sophrosyne_api.core.controlpaneldashboardgroupservice.model.ControlPanelDashboardGroup;

import java.util.*;


@Service
public class ControlPanelDashboardGroupService {
    @Autowired
    ControlPanelDashboardGroupRepository
            controlPanelDashboardGroupRepository;

    @Autowired
    ActionService actionService;

    @Autowired
    DynamicActionService dynamicActionService;

    @Autowired
    @Lazy
    private ConfigurationService configurationService;

    public ControlPanelDashboardGroupDTO
    createControlPanelDashboardGroup(ControlPanelDashboardGroup controlPanelDashboardGroup) {
        ControlPanelDashboardGroupDTO
                controlPanelDashboardGroupDTO =
                controlPanelDashboardGroupRepository.save(
                        createControlPanelGroupDTOFromControlPanelDashboardGroup(
                                controlPanelDashboardGroup));

        return controlPanelDashboardGroupDTO;
    }

    public ControlPanelDashboardGroupDTO
    updateControlPanelDashboardGroup(ControlPanelDashboardGroup controlPanelDashboardGroup) {
        Optional<ControlPanelDashboardGroupDTO>
                controlPanelDashboardGroupDTO =
                getControlPanelDashboardGroupDTOById(controlPanelDashboardGroup.getId());
        if (controlPanelDashboardGroupDTO.isPresent()) {
            unlinkActions(controlPanelDashboardGroupDTO.get());
            ControlPanelDashboardGroupDTO
                    controlPanelDashboardGroupDTOToUpdate =
                    createControlPanelGroupDTOFromControlPanelDashboardGroup(controlPanelDashboardGroup);
            controlPanelDashboardGroupDTOToUpdate.setId(controlPanelDashboardGroupDTO.get().getId());
            ControlPanelDashboardGroupDTO
                    updatedControlPanelDashboardGroupDTO =
                    controlPanelDashboardGroupRepository.save(controlPanelDashboardGroupDTOToUpdate);

            return updatedControlPanelDashboardGroupDTO;
        } else {
            throw new NoSuchElementException(
                    "No ControlPanelDashboardGroup with id: " + controlPanelDashboardGroup.getId());
        }
    }

    private void unlinkActions(
            ControlPanelDashboardGroupDTO
                    controlPanelDashboardGroupDTO) {
        controlPanelDashboardGroupDTO.setAssociatedDynamicActions(null);
        controlPanelDashboardGroupDTO.setAssociatedActions(null);
        controlPanelDashboardGroupRepository.save(controlPanelDashboardGroupDTO);
    }

    public Optional<ControlPanelDashboardGroupDTO>
    getControlPanelDashboardGroupDTOById(String controlPanelDashboardGroupId) {
        return controlPanelDashboardGroupRepository.findById(controlPanelDashboardGroupId);
    }

    public List<ControlPanelDashboardGroupDTO>
    getControlPanelDashboardGroups() {
        return controlPanelDashboardGroupRepository.findAll();
    }

    public void deleteControlPanelDashboardGroup(
            ControlPanelDashboardGroupDTO
                    controlPanelDashboardGroupDTO) {
        unlinkActions(controlPanelDashboardGroupDTO);
        controlPanelDashboardGroupRepository.delete(controlPanelDashboardGroupDTO);

    }

    public void deleteAllControlPanelDashboardGroups() {
        controlPanelDashboardGroupRepository.deleteAll();
    }

    public ControlPanelDashboardGroup mapControlPanelDashboardGroupDTOToControlPanelDashboardGroup(
            ControlPanelDashboardGroupDTO
                    controlPanelDashboardGroupDTO) {
        return new ControlPanelDashboardGroup()
                .id(controlPanelDashboardGroupDTO.getId())
                .name(controlPanelDashboardGroupDTO.getName())
                .description(controlPanelDashboardGroupDTO.getDescription())
                .associatedActions(
                        controlPanelDashboardGroupDTO.getAssociatedActions().stream()
                                .map(ActionDTO::getId)
                                .toList())
                .associatedDynamicActions(
                        controlPanelDashboardGroupDTO.getAssociatedDynamicActions().stream()
                                .map(DynamicActionDTO::getId)
                                .toList())
                .position(controlPanelDashboardGroupDTO.getPosition());
    }

    public ControlPanelDashboardGroupDTO
    createControlPanelGroupDTOFromControlPanelDashboardGroup(
            ControlPanelDashboardGroup controlPanelDashboardGroup) {
        ControlPanelDashboardGroupDTO
                controlPanelDashboardGroupDTO =
                new ControlPanelDashboardGroupDTO();
        controlPanelDashboardGroupDTO.setId(UUID.randomUUID().toString());
        controlPanelDashboardGroupDTO.setName(controlPanelDashboardGroup.getName());
        controlPanelDashboardGroupDTO.setDescription(controlPanelDashboardGroup.getDescription());
        controlPanelDashboardGroupDTO.setAssociatedActions(
                getAssociatedActions(controlPanelDashboardGroup));
        controlPanelDashboardGroupDTO.setAssociatedDynamicActions(
                getAssociatedDynamicActions(controlPanelDashboardGroup));
        controlPanelDashboardGroupDTO.setPosition(controlPanelDashboardGroup.getPosition());
        return controlPanelDashboardGroupDTO;
    }

    private HashSet<ActionDTO> getAssociatedActions(
            ControlPanelDashboardGroup controlPanelDashboardGroup) {
        return new HashSet<ActionDTO>(
                controlPanelDashboardGroup.getAssociatedActions().stream()
                        .map(
                                actionId -> {
                                    return actionService.getActionDTOByID(actionId).isPresent()
                                            ? actionService.getActionDTOByID(actionId).get()
                                            : null;
                                })
                        .toList());
    }

    private HashSet<DynamicActionDTO>
    getAssociatedDynamicActions(ControlPanelDashboardGroup controlPanelDashboardGroup) {
        return new HashSet<DynamicActionDTO>(
                controlPanelDashboardGroup.getAssociatedDynamicActions().stream()
                        .map(
                                dynamicActionId -> {
                                    return dynamicActionService.getDynamicActionDTOByID(dynamicActionId).isPresent()
                                            ? dynamicActionService.getDynamicActionDTOByID(dynamicActionId).get()
                                            : null;
                                })
                        .toList());
    }
}
