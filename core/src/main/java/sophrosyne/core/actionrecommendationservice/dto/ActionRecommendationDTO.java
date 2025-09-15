package sophrosyne.core.actionrecommendationservice.dto;

import jakarta.persistence.*;
import java.util.Set;
import lombok.*;
import lombok.experimental.SuperBuilder;
import sophrosyne.core.apikeyservice.dto.ApikeyDTO;

@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "sophrosyne_action_recommendation")
public class ActionRecommendationDTO extends AbstractActionRecommendationDTO {

  @OneToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "action_recommendation_apikey",
      joinColumns = @JoinColumn(name = "id"),
      inverseJoinColumns = @JoinColumn(name = "apikey"))
  private Set<ApikeyDTO> allowedApikeys;
}
