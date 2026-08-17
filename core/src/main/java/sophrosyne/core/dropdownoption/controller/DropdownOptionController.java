package sophrosyne.core.dropdownoption.controller;

import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sophrosyne.core.dropdownoption.service.DropdownOptionService;
import sophrosyne_api.core.internaldropdownoptionservice.api.IntApi;
import sophrosyne_api.core.internaldropdownoptionservice.model.DropdownOption;

@RestController
public class DropdownOptionController implements IntApi {

  private final Logger logger = LogManager.getLogger(getClass());
  @Autowired DropdownOptionService dropdownOptionService;

  @Override
  public ResponseEntity<Void> createDropdownOption(DropdownOption dropdownOption) {
    try {
      dropdownOptionService.createDropdownOptionDTO(dropdownOption);
    } catch (DataIntegrityViolationException e) {
      logger.error(e.getMessage());
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    } catch (Exception e) {
      logger.error(e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  public ResponseEntity<Void> updateDropdownOption(String id, DropdownOption dropdownOption) {
    try {
      dropdownOptionService.updateDropdownOption(dropdownOption);
    } catch (Exception e) {
      logger.error(e.getMessage());
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  public ResponseEntity<List<DropdownOption>> getDropdownOptions() {
    return ResponseEntity.ok(dropdownOptionService.getDropdownOptions());
  }

  public ResponseEntity<DropdownOption> getDropdownOptionById(String id) {
    Optional<DropdownOption> optionalDropdownOption = dropdownOptionService.getDropdownOption(id);
    return optionalDropdownOption
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  public ResponseEntity<Void> deleteDropdownOption(String id) {
    dropdownOptionService.deleteDropdownOption(id);
    return ResponseEntity.ok().build();
  }
}
