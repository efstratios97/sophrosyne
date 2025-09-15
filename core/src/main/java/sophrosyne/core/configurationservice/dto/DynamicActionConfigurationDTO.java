package sophrosyne.core.configurationservice.dto;

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
import sophrosyne.core.actionservice.dto.ActionDTO;
import sophrosyne.core.apikeyservice.dto.ApikeyDTO;
import sophrosyne.core.apikeyservice.service.ApikeyService;
import sophrosyne.core.dynamicactionservice.dto.DynamicActionDTO;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DynamicActionConfigurationDTO extends ActionConfigurationDTO {

  @Getter(AccessLevel.NONE)
  @Autowired
  private ApikeyService apikeyService;

  private String dynamicParameters;
  private int keepLatestConfirmationRequest;

  public DynamicActionConfigurationDTO(DynamicActionDTO dynamicActionDTO) {
    super(dynamicActionDTO);
    this.setDynamicParameters(dynamicActionDTO.getDynamicParameters());
    this.setKeepLatestConfirmationRequest(dynamicActionDTO.getKeepLatestConfirmationRequest());
    this.setAllowedApikeysByName(
        dynamicActionDTO.getAllowedApikeysForDynamicActions().stream()
            .map(ApikeyDTO::getApikeyname)
            .toList());
  }

  public DynamicActionDTO toDynamicActionDTO() {
    super.setApikeyService(apikeyService);
    ActionDTO base = super.toActionDTO();

    DynamicActionDTO dynamicActionDTO = new DynamicActionDTO();
    dynamicActionDTO.setId(base.getId());
    dynamicActionDTO.setName(base.getName());
    dynamicActionDTO.setCommand(base.getCommand());
    dynamicActionDTO.setDescription(base.getDescription());
    dynamicActionDTO.setVersion(base.getVersion());
    dynamicActionDTO.setPostExecutionLogFilePath(base.getPostExecutionLogFilePath());
    dynamicActionDTO.setRequiresConfirmation(base.getRequiresConfirmation());
    dynamicActionDTO.setMuted(base.getMuted());
    dynamicActionDTO.setAllowedApikeys(null);
    dynamicActionDTO.setDynamicParameters(this.getDynamicParameters());
    dynamicActionDTO.setKeepLatestConfirmationRequest(this.getKeepLatestConfirmationRequest());
    dynamicActionDTO.setAllowedApikeysForDynamicActions(
        this.getAllowedApikeysByName().stream()
            .map(
                name -> {
                  Optional<ApikeyDTO> maybe = this.apikeyService.getApiDTOByApikeyname(name);
                  return maybe.orElse(null);
                })
            .filter(Objects::nonNull)
            .collect(Collectors.toSet()));
    return dynamicActionDTO;
  }
}
