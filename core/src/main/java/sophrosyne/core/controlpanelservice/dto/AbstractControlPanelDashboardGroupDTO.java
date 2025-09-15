package sophrosyne.core.controlpanelservice.dto;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public abstract class AbstractControlPanelDashboardGroupDTO {
  @Id private String id;
  private String name;
  private String description;
  private int position;
}
