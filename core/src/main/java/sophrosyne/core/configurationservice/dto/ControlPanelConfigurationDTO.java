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
import sophrosyne.core.controlpanelservice.dto.AbstractControlPanelDTO;
import sophrosyne.core.controlpanelservice.dto.ControlPanelDTO;
import sophrosyne.core.controlpanelservice.dto.ControlPanelDashboardDTO;
import sophrosyne.core.controlpanelservice.service.ControlPanelDashboardService;
import sophrosyne.core.userservice.dto.UserDTO;
import sophrosyne.core.userservice.service.UserService;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ControlPanelConfigurationDTO extends AbstractControlPanelDTO {

  @Getter(AccessLevel.NONE)
  @Autowired
  private UserService userService;

  @Getter(AccessLevel.NONE)
  @Autowired
  private ControlPanelDashboardService controlPanelDashboardService;

  private List<String> associatedUserIds;
  private List<String> associatedControlPanelDashboardIds;

  public ControlPanelConfigurationDTO(ControlPanelDTO controlPanelDTO) {
    this.setId(controlPanelDTO.getId());
    this.setName(controlPanelDTO.getName());
    this.setDescription(controlPanelDTO.getDescription());
    this.setAssociatedUserIds(
        controlPanelDTO.getAssociatedUsers() == null
            ? List.of()
            : controlPanelDTO.getAssociatedUsers().stream().map(UserDTO::getId).toList());
    this.setAssociatedControlPanelDashboardIds(
        controlPanelDTO.getAssociatedControlPanelDashboards() == null
            ? List.of()
            : controlPanelDTO.getAssociatedControlPanelDashboards().stream()
                .map(ControlPanelDashboardDTO::getId)
                .toList());
  }

  public ControlPanelDTO toControlPanelDTO() {
    ControlPanelDTO dto = new ControlPanelDTO();
    dto.setId(this.getId());
    dto.setName(this.getName());
    dto.setDescription(this.getDescription());

    dto.setAssociatedUsers(
        this.getAssociatedUserIds().stream()
            .map(
                userId -> {
                  Optional<UserDTO> maybe = userService.getUserById(userId);
                  return maybe.orElse(null);
                })
            .filter(Objects::nonNull)
            .collect(Collectors.toSet()));

    dto.setAssociatedControlPanelDashboards(
        this.getAssociatedControlPanelDashboardIds().stream()
            .map(
                dashId -> {
                  Optional<ControlPanelDashboardDTO> maybe =
                      controlPanelDashboardService.getControlPanelDashboardDTOById(dashId);
                  return maybe.orElse(null);
                })
            .filter(Objects::nonNull)
            .collect(Collectors.toSet()));
    return dto;
  }
}
