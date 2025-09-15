package sophrosyne.core.actionservice.dto;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder(toBuilder = true)
@MappedSuperclass
public abstract class AbstractActionDTO {
  @Id private String id;
  private String name;
  private String description;
  private String command;
  private String postExecutionLogFilePath;
  private String version;
  private int requiresConfirmation;
  private int muted;
}
