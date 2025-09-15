package sophrosyne.core.actionrecommendationservice.dto;

import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@MappedSuperclass
public class AbstractActionRecommendationDTO {
  @Id private String id;
  private String name;
  private String description;
  private String responsibleEntity;
  private String contactInformation;

  @Lob private byte[] additionalDocumentation;
}
