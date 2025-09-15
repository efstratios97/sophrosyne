package sophrosyne.core.controlpanelservice.dto;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@MappedSuperclass
public class AbstractControlPanelDashboardDTO {
  @Id private String id;
  private String name;
  private String description;
  private int position;
}
