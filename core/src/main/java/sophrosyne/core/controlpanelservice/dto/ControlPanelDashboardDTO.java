package sophrosyne.core.controlpanelservice.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Set;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonSerialize
@Entity(name = "sophrosyne_control_panel_dashboard")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ControlPanelDashboardDTO extends AbstractControlPanelDashboardDTO
    implements Serializable {

  @OneToMany(
      fetch = FetchType.EAGER,
      cascade = {CascadeType.MERGE, CascadeType.PERSIST})
  @JoinTable(
      name = "control_panel_dashboard_control_panel_dashboard_group",
      joinColumns = @JoinColumn(name = "control_panel_dashboard_id"),
      inverseJoinColumns = @JoinColumn(name = "control_panel_dashboard_group_id"))
  private Set<ControlPanelDashboardGroupDTO> associatedControlPanelGroups;
}
