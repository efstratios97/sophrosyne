package sophrosyne.core.actionrecommendationservice.dto;

import jakarta.persistence.*;
import lombok.*;
import sophrosyne.core.apikeyservice.dto.ApikeyDTO;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Entity(name = "sophrosyne_action_recommendation")
public class ActionRecommendationDTO {

  @Id private String id;
  private String name;
  private String description;
  private String responsibleEntity;
  private String contactInformation;

  @Lob private byte[] additionalDocumentation;

  @OneToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "action_recommendation_apikey",
      joinColumns = @JoinColumn(name = "id"),
      inverseJoinColumns = @JoinColumn(name = "apikey"))
  private Set<ApikeyDTO> allowedApikeys;
}
