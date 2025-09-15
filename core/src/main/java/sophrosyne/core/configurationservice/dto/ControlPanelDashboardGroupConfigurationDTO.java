package sophrosyne.core.configurationservice.dto;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import sophrosyne.core.actionservice.dto.ActionDTO;
import sophrosyne.core.actionservice.service.ActionService;
import sophrosyne.core.controlpanelservice.dto.AbstractControlPanelDashboardGroupDTO;
import sophrosyne.core.controlpanelservice.dto.ControlPanelDashboardGroupDTO;
import sophrosyne.core.dynamicactionservice.dto.DynamicActionDTO;
import sophrosyne.core.dynamicactionservice.service.DynamicActionService;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ControlPanelDashboardGroupConfigurationDTO
    extends AbstractControlPanelDashboardGroupDTO {

  @Getter(AccessLevel.NONE)
  @Autowired
  private ActionService actionService;

  @Getter(AccessLevel.NONE)
  @Autowired
  private DynamicActionService dynamicActionService;

  private List<String> associatedActionIds;
  private List<String> associatedDynamicActionIds;

  public ControlPanelDashboardGroupConfigurationDTO(ControlPanelDashboardGroupDTO groupDTO) {
    this.setId(groupDTO.getId());
    this.setName(groupDTO.getName());
    this.setDescription(groupDTO.getDescription());
    this.setPosition(groupDTO.getPosition());
    this.setAssociatedActionIds(
        groupDTO.getAssociatedActions() == null
            ? List.of()
            : groupDTO.getAssociatedActions().stream().map(ActionDTO::getId).toList());
    this.setAssociatedDynamicActionIds(
        groupDTO.getAssociatedDynamicActions() == null
            ? List.of()
            : groupDTO.getAssociatedDynamicActions().stream()
                .map(DynamicActionDTO::getId)
                .toList());
  }

  public ControlPanelDashboardGroupDTO toControlPanelDashboardGroupDTO() {
    ControlPanelDashboardGroupDTO dto = new ControlPanelDashboardGroupDTO();
    dto.setId(this.getId());
    dto.setName(this.getName());
    dto.setDescription(this.getDescription());
    dto.setPosition(this.getPosition());

    dto.setAssociatedActions(
        this.getAssociatedActionIds().stream()
            .map(
                actionId -> {
                  Optional<ActionDTO> action = actionService.getActionDTOByID(actionId);
                  return action.orElse(null);
                })
            .filter(Objects::nonNull)
            .collect(Collectors.toSet()));

    dto.setAssociatedDynamicActions(
        this.getAssociatedDynamicActionIds().stream()
            .map(
                dynId -> {
                  Optional<DynamicActionDTO> dyn =
                      dynamicActionService.getDynamicActionDTOByID(dynId);
                  return dyn.orElse(null);
                })
            .filter(Objects::nonNull)
            .collect(Collectors.toSet()));
    return dto;
  }
}
