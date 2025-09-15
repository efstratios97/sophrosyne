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
import sophrosyne.core.actionrecommendationservice.dto.AbstractActionRecommendationDTO;
import sophrosyne.core.actionrecommendationservice.dto.ActionRecommendationDTO;
import sophrosyne.core.apikeyservice.dto.ApikeyDTO;
import sophrosyne.core.apikeyservice.service.ApikeyService;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActionRecommendationConfigurationDTO extends AbstractActionRecommendationDTO {

  @Getter(AccessLevel.NONE)
  @Autowired
  private ApikeyService apikeyService;

  private List<String> allowedApikeysByName;

  public ActionRecommendationConfigurationDTO(ActionRecommendationDTO recommendationDTO) {
    this.setId(recommendationDTO.getId());
    this.setName(recommendationDTO.getName());
    this.setDescription(recommendationDTO.getDescription());
    this.setResponsibleEntity(recommendationDTO.getResponsibleEntity());
    this.setContactInformation(recommendationDTO.getContactInformation());
    this.setAdditionalDocumentation(recommendationDTO.getAdditionalDocumentation());
    this.setAllowedApikeysByName(
        recommendationDTO.getAllowedApikeys().stream().map(ApikeyDTO::getApikeyname).toList());
  }

  public ActionRecommendationDTO toActionRecommendationDTO() {
    ActionRecommendationDTO dto = new ActionRecommendationDTO();
    dto.setId(this.getId());
    dto.setName(this.getName());
    dto.setDescription(this.getDescription());
    dto.setResponsibleEntity(this.getResponsibleEntity());
    dto.setContactInformation(this.getContactInformation());
    dto.setAdditionalDocumentation(this.getAdditionalDocumentation());
    dto.setAllowedApikeys(
        this.allowedApikeysByName.stream()
            .map(
                apikeyName -> {
                  Optional<ApikeyDTO> apikeyDTO = apikeyService.getApiDTOByApikeyname(apikeyName);
                  return apikeyDTO.orElse(null);
                })
            .filter(Objects::nonNull)
            .collect(Collectors.toSet()));
    return dto;
  }
}
