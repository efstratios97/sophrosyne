package sophrosyne.core.configurationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import sophrosyne.core.dropdownoption.dto.DropdownOptionDTO;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class DropdownOptionConfigurationDTO extends DropdownOptionDTO {

  public DropdownOptionConfigurationDTO(DropdownOptionDTO dropdownOptionDTO) {
    this.setId(dropdownOptionDTO.getId());
    this.setName(dropdownOptionDTO.getName());
    this.setDescription(dropdownOptionDTO.getDescription());
    this.setGetterDropdownOptionCallAddress(dropdownOptionDTO.getGetterDropdownOptionCallAddress());
    this.setDelimiter(dropdownOptionDTO.getDelimiter());
    this.setDynamicParameterToMatch(dropdownOptionDTO.getDynamicParameterToMatch());
    this.setMultiSelect(dropdownOptionDTO.isMultiSelect());
    this.setDropdownOptions(dropdownOptionDTO.getDropdownOptions());
    this.setType(dropdownOptionDTO.getType());
  }

  public DropdownOptionDTO toDropdownOptionDTO() {
    DropdownOptionDTO dropdownOptionDTO = new DropdownOptionDTO();
    dropdownOptionDTO.setId(this.getId());
    dropdownOptionDTO.setName(this.getName());
    dropdownOptionDTO.setDescription(this.getDescription());
    dropdownOptionDTO.setGetterDropdownOptionCallAddress(this.getGetterDropdownOptionCallAddress());
    dropdownOptionDTO.setDelimiter(this.getDelimiter());
    dropdownOptionDTO.setDynamicParameterToMatch(this.getDynamicParameterToMatch());
    dropdownOptionDTO.setMultiSelect(this.isMultiSelect());
    dropdownOptionDTO.setDropdownOptions(this.getDropdownOptions());
    dropdownOptionDTO.setType(this.getType());
    return dropdownOptionDTO;
  }
}
