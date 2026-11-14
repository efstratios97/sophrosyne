package sophrosyne.core.dropdownoption.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import sophrosyne.core.dropdownoption.dto.DropdownOptionDTO;
import sophrosyne.core.dropdownoption.mapper.DropdownOptionMapper;
import sophrosyne.core.dropdownoption.repository.DropdownRepository;
import sophrosyne_api.core.internaldropdownoptionservice.model.DropdownOption;

@Service
public class DropdownOptionService {

  @Autowired DropdownOptionMapper dropdownOptionMapper;

  @Autowired DropdownRepository dropdownRepository;

  public DropdownOptionDTO createDropdownOptionDTO(DropdownOption dropdownOption)
      throws DataIntegrityViolationException {
    DropdownOptionDTO dropdownOptionDTO =
        dropdownOptionMapper.dropdownOptionToDropdownOptionDto(dropdownOption);
    dropdownOptionDTO.setId(UUID.randomUUID().toString());
    return dropdownRepository.save(dropdownOptionDTO);
  }

  public DropdownOptionDTO updateDropdownOption(DropdownOption updatedDropdownOption) {
    DropdownOptionDTO dropdownOptionDTO =
        dropdownOptionMapper.dropdownOptionToDropdownOptionDto(updatedDropdownOption);
    return updateDropdownOptionDTO(dropdownOptionDTO);
  }

  public DropdownOptionDTO updateDropdownOptionDTO(DropdownOptionDTO updatedDropdownOptionDTO) {
    return dropdownRepository.save(updatedDropdownOptionDTO);
  }

  public Optional<DropdownOptionDTO> getDropdownOptionDTO(String id) {
    return dropdownRepository.findById(id);
  }

  public Optional<DropdownOption> getDropdownOption(String id) {
    Optional<DropdownOptionDTO> optionalDropdownOptionDTO = getDropdownOptionDTO(id);
    return optionalDropdownOptionDTO.map(
        dropdownOptionDTO ->
            dropdownOptionMapper.dropdownOptionDtoToDropdownOption(dropdownOptionDTO));
  }

  public List<DropdownOptionDTO> getDropdownOptionDTOs() {
    return dropdownRepository.findAll();
  }

  public List<DropdownOption> getDropdownOptions() {
    return getDropdownOptionDTOs().stream()
        .map(
            dropdownOptionDTO ->
                dropdownOptionMapper.dropdownOptionDtoToDropdownOption(dropdownOptionDTO))
        .toList();
  }

  public void deleteDropdownOption(String id) {
    Optional<DropdownOptionDTO> dropdownOptionDTOOptional = this.getDropdownOptionDTO(id);
    dropdownOptionDTOOptional.ifPresent(
        (dropdownOptionDTO -> {
          dropdownRepository.delete(dropdownOptionDTO);
        }));
  }

  public void deleteAllDropdownOptions() {
    dropdownRepository.deleteAll();
  }
}
