package sophrosyne.core.actionarchiveservice.dto;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Entity(name = "sophrosyne_action_archive")
public class ActionArchiveDTO {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String actionId;
  private String actionName;
  private String actionCommand;
  private String version;
  private String type;
  private LocalDateTime executionStartPoint;
  private LocalDateTime executionEndPoint;
  private int exitCode;
  @Lob private byte[] executionLogFileData;
  @Lob private byte[] postExecutionLogFileData;

  public enum TYPES {
    INTERNAL,
    EXTERNAL
  }
}
