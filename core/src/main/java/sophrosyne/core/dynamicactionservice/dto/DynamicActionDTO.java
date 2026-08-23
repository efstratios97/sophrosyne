package sophrosyne.core.dynamicactionservice.dto;

import jakarta.persistence.*;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sophrosyne.core.actionservice.dto.ActionDTO;
import sophrosyne.core.apikeyservice.dto.ApikeyDTO;
import sophrosyne.core.dropdownoption.dto.DropdownOptionDTO;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "sophrosyne_dynamic_action")
@Table(name = "sophrosyne_dynamic_action")
public class DynamicActionDTO extends ActionDTO {
  private String dynamicParameters;
  @Transient private String runningActionId;
  private int keepLatestConfirmationRequest;
  private int onlySingleExecution;

  @OneToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "dynamic_action_dropdown_option",
      joinColumns = @JoinColumn(name = "dynamic_action_id"),
      inverseJoinColumns = @JoinColumn(name = "dropdown_option_id"))
  private Set<DropdownOptionDTO> associatedDropdownOptions;

  @OneToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "dynamic_action_apikey",
      joinColumns = @JoinColumn(name = "id"),
      inverseJoinColumns = @JoinColumn(name = "apikey"))
  private Set<ApikeyDTO> allowedApikeysForDynamicActions;
}
