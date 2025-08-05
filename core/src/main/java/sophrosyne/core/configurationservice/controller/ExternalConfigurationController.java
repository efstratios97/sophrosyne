package sophrosyne.core.configurationservice.controller;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sophrosyne.core.configurationservice.service.ConfigurationService;
import sophrosyne_api.core.externalconfigurationservice.api.ApiApi;

@RestController
@NoArgsConstructor
@AllArgsConstructor
public class ExternalConfigurationController implements ApiApi {

  @Autowired private ConfigurationService configurationService;

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

  @Override
  public ResponseEntity<String> importSophrosyneConfiguration(Object configuration) {
    return configurationService.getResponse(
        configurationService.importSophrosyneConfiguration(configuration));
  }

  @Override
  public ResponseEntity<String> importSophrosyneConfigurationForce(Object configuration) {
    return configurationService.getResponse(
        configurationService.importSophrosyneConfigurationForce(configuration));
  }
}
