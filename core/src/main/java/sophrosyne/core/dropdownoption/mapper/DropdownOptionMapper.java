package sophrosyne.core.dropdownoption.mapper;

import org.mapstruct.Mapper;
import sophrosyne.core.dropdownoption.dto.DropdownOptionDTO;
import sophrosyne_api.core.internaldropdownoptionservice.model.DropdownOption;

@Mapper(componentModel = "spring")
public interface DropdownOptionMapper {

  DropdownOption dropdownOptionDtoToDropdownOption(DropdownOptionDTO dropdownOptionDTO);

  DropdownOptionDTO dropdownOptionToDropdownOptionDto(DropdownOption dropdownOption);
}
