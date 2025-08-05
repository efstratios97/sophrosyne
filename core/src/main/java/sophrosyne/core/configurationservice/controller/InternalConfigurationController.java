package sophrosyne.core.configurationservice.controller;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sophrosyne.core.configurationservice.service.ConfigurationService;
import sophrosyne_api.core.interalconfigurationservice.api.IntApi;

@RestController
@NoArgsConstructor
@AllArgsConstructor
public class InternalConfigurationController implements IntApi {

  @Autowired private ConfigurationService configurationService;

  @Override
  public ResponseEntity<Object> getConfigurationData() {
    return ResponseEntity.ok(configurationService.getConfigurationData());
  }

  @Override
  public ResponseEntity<String> importSophrosyneConfigurationFromFile() {
    return configurationService.getResponse(
        configurationService.importSophrosyneConfigurationFromFile());
  }

  @Override
  public ResponseEntity<String> importSophrosyneConfigurationFromFileForce() {
    return configurationService.getResponse(
        configurationService.importSophrosyneConfigurationFromFileForce());
  }
}
