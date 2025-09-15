package sophrosyne.core.configurationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import sophrosyne.core.userservice.dto.AbstractUserDTO;
import sophrosyne.core.userservice.dto.UserDTO;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class UserConfigurationDTO extends AbstractUserDTO {

  public UserConfigurationDTO(UserDTO userDTO) {
    // base fields
    this.setId(userDTO.getId());
    this.setUsername(userDTO.getUsername());
    this.setFirstname(userDTO.getFirstname());
    this.setLastname(userDTO.getLastname());
    this.setEmail(userDTO.getEmail());
    this.setRole(userDTO.getRole());
    this.setPassword(userDTO.getPassword());
  }

  public UserDTO toUserDTO() {
    UserDTO dto = new UserDTO();
    dto.setId(this.getId());
    dto.setUsername(this.getUsername());
    dto.setFirstname(this.getFirstname());
    dto.setLastname(this.getLastname());
    dto.setEmail(this.getEmail());
    dto.setRole(this.getRole());
    dto.setPassword(this.getPassword());
    return dto;
  }
}
