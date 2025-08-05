package sophrosyne.core.actionservice.dto;

import jakarta.persistence.*;
import java.util.Set;
import lombok.*;
import sophrosyne.core.apikeyservice.dto.ApikeyDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Entity(name = "sophrosyne_action")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ActionDTO {
  @Id private String id;
  private String name;
  private String description;
  private String command;
  private String postExecutionLogFilePath;
  private String version;
  private int requiresConfirmation;
  private int muted;

  @OneToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "action_apikey",
      joinColumns = @JoinColumn(name = "id"),
      inverseJoinColumns = @JoinColumn(name = "apikey"))
  private Set<ApikeyDTO> allowedApikeys;
}
