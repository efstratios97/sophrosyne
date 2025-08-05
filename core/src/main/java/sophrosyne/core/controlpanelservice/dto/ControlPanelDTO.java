package sophrosyne.core.controlpanelservice.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import java.util.Set;
import lombok.*;
import sophrosyne.core.userservice.dto.UserDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@JsonSerialize
@Entity(name = "sophrosyne_control_panel")
public class ControlPanelDTO {
  @Id private String id;
  private String name;
  private String description;

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
