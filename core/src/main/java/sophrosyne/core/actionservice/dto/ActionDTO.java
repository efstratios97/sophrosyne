package sophrosyne.core.actionservice.dto;

import jakarta.persistence.*;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import sophrosyne.core.apikeyservice.dto.ApikeyDTO;

@EqualsAndHashCode(callSuper = true)
@Entity(name = "sophrosyne_action")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ActionDTO extends AbstractActionDTO {

  @OneToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "action_apikey",
      joinColumns = @JoinColumn(name = "id"),
      inverseJoinColumns = @JoinColumn(name = "apikey"))
  private Set<ApikeyDTO> allowedApikeys;
}
