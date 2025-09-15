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
import sophrosyne.core.controlpanelservice.dto.AbstractControlPanelDashboardDTO;
import sophrosyne.core.controlpanelservice.dto.ControlPanelDashboardDTO;
import sophrosyne.core.controlpanelservice.dto.ControlPanelDashboardGroupDTO;
import sophrosyne.core.controlpanelservice.service.ControlPanelDashboardGroupService;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ControlPanelDashboardConfigurationDTO extends AbstractControlPanelDashboardDTO {

  @Getter(AccessLevel.NONE)
  @Autowired
  private ControlPanelDashboardGroupService controlPanelDashboardGroupService;

  private List<String> associatedControlPanelGroupIds;

  public ControlPanelDashboardConfigurationDTO(ControlPanelDashboardDTO dashboardDTO) {
    this.setId(dashboardDTO.getId());
    this.setName(dashboardDTO.getName());
    this.setDescription(dashboardDTO.getDescription());
    this.setPosition(dashboardDTO.getPosition());
    this.setAssociatedControlPanelGroupIds(
        dashboardDTO.getAssociatedControlPanelGroups() == null
            ? List.of()
            : dashboardDTO.getAssociatedControlPanelGroups().stream()
                .map(ControlPanelDashboardGroupDTO::getId)
                .toList());
  }

  public ControlPanelDashboardDTO toControlPanelDashboardDTO() {
    ControlPanelDashboardDTO dto = new ControlPanelDashboardDTO();
    dto.setId(this.getId());
    dto.setName(this.getName());
    dto.setDescription(this.getDescription());
    dto.setPosition(this.getPosition());

    dto.setAssociatedControlPanelGroups(
        this.associatedControlPanelGroupIds.stream()
            .map(
                groupId -> {
                  Optional<ControlPanelDashboardGroupDTO> maybe =
                      controlPanelDashboardGroupService.getControlPanelDashboardGroupDTOById(
                          groupId);
                  return maybe.orElse(null);
                })
            .filter(Objects::nonNull)
            .collect(Collectors.toSet()));

    return dto;
  }
}
