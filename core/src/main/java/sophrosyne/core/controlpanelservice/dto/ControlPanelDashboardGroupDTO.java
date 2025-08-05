package sophrosyne.core.controlpanelservice.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import java.util.Set;
import lombok.*;
import sophrosyne.core.actionservice.dto.ActionDTO;
import sophrosyne.core.dynamicactionservice.dto.DynamicActionDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@JsonSerialize
@Entity(name = "sophrosyne_control_panel_dashboard_group")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ControlPanelDashboardGroupDTO {
  @Id private String id;
  private String name;
  private String description;
  private int position;

  @OneToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "control_panel_dashboard_group_actions",
      joinColumns = @JoinColumn(name = "control_panel_dashboard_group_id"),
      inverseJoinColumns = @JoinColumn(name = "action_id"))
  private Set<ActionDTO> associatedActions;

  @OneToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "control_panel_dashboard_group_dynamic_actions",
      joinColumns = @JoinColumn(name = "control_panel_dashboard_group_id"),
      inverseJoinColumns = @JoinColumn(name = "dynamic_action_id"))
  private Set<DynamicActionDTO> associatedDynamicActions;
}
