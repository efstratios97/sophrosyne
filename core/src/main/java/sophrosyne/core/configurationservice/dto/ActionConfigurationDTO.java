package sophrosyne.core.configurationservice.dto;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import sophrosyne.core.actionservice.dto.AbstractActionDTO;
import sophrosyne.core.actionservice.dto.ActionDTO;
import sophrosyne.core.apikeyservice.dto.ApikeyDTO;
import sophrosyne.core.apikeyservice.service.ApikeyService;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActionConfigurationDTO extends AbstractActionDTO {

  @Getter(AccessLevel.NONE)
  @Autowired
  private ApikeyService apikeyService;

  private List<String> allowedApikeysByName;

  public ActionConfigurationDTO(ActionDTO actionDTO) {
    this.setId(actionDTO.getId());
    this.setName(actionDTO.getName());
    this.setCommand(actionDTO.getCommand());
    this.setDescription(actionDTO.getDescription());
    this.setVersion(actionDTO.getVersion());
    this.setPostExecutionLogFilePath(actionDTO.getPostExecutionLogFilePath());
    this.setRequiresConfirmation(actionDTO.getRequiresConfirmation());
    this.setAllowedApikeysByName(
        actionDTO.getAllowedApikeys().stream().map(ApikeyDTO::getApikeyname).toList());
  }

  public ActionDTO toActionDTO() {
    ActionDTO actionDTO = new ActionDTO();
    actionDTO.setId(this.getId());
    actionDTO.setName(this.getName());
    actionDTO.setCommand(this.getCommand());
    actionDTO.setDescription(this.getDescription());
    actionDTO.setVersion(this.getVersion());
    actionDTO.setPostExecutionLogFilePath(this.getPostExecutionLogFilePath());
    actionDTO.setRequiresConfirmation(this.getRequiresConfirmation());
    actionDTO.setMuted(this.getMuted());
    actionDTO.setAllowedApikeys(
        this.allowedApikeysByName.stream()
            .map(
                apikeyName -> {
                  Optional<ApikeyDTO> apikeyDTO = apikeyService.getApiDTOByApikeyname(apikeyName);
                  return apikeyDTO.orElse(null);
                })
            .filter(Objects::nonNull)
            .collect(Collectors.toSet()));
    return actionDTO;
  }
}
