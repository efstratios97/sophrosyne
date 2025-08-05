package sophrosyne.core.controlpanelservice.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Set;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@JsonSerialize
@Entity(name = "sophrosyne_control_panel_dashboard")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ControlPanelDashboardDTO implements Serializable {
  @Id private String id;
  private String name;
  private String description;
  private int position;

  @OneToMany(
      fetch = FetchType.EAGER,
      cascade = {CascadeType.MERGE, CascadeType.PERSIST})
  @JoinTable(
      name = "control_panel_dashboard_control_panel_dashboard_group",
      joinColumns = @JoinColumn(name = "control_panel_dashboard_id"),
      inverseJoinColumns = @JoinColumn(name = "control_panel_dashboard_group_id"))
  private Set<ControlPanelDashboardGroupDTO> associatedControlPanelGroups;
}
