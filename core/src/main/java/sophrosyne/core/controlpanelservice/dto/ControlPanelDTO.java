package sophrosyne.core.controlpanelservice.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import java.util.Set;
import lombok.*;
import lombok.experimental.SuperBuilder;
import sophrosyne.core.userservice.dto.UserDTO;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonSerialize
@Entity(name = "sophrosyne_control_panel")
public class ControlPanelDTO extends AbstractControlPanelDTO {

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "controlPanelDTO")
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private Set<UserDTO> associatedUsers;

  @OneToMany(
      fetch = FetchType.EAGER,
      cascade = {CascadeType.MERGE, CascadeType.PERSIST})
  @JoinTable(
      name = "control_panel_control_panel_dashboard",
      joinColumns = @JoinColumn(name = "control_panel_id"),
      inverseJoinColumns = @JoinColumn(name = "control_panel_dashboard_id"))
  private Set<ControlPanelDashboardDTO> associatedControlPanelDashboards;
}
