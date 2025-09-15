package sophrosyne.core.userservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import sophrosyne.core.controlpanelservice.dto.ControlPanelDTO;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity(name = "sophrosyne_user")
@SecondaryTable(
    name = "sophrosyne_control_panel",
    pkJoinColumns = @PrimaryKeyJoinColumn(name = "id"))
public class UserDTO extends AbstractUserDTO {

  private String token;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "control_panel_id", referencedColumnName = "id")
  @JsonIgnore
  private ControlPanelDTO controlPanelDTO;
}
