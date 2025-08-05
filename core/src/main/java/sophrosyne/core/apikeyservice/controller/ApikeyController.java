package sophrosyne.core.apikeyservice.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sophrosyne.core.apikeyservice.dto.ApikeyDTO;
import sophrosyne.core.apikeyservice.service.ApikeyService;
import sophrosyne_api.core.apikeyservice.api.IntApi;
import sophrosyne_api.core.apikeyservice.model.Apikey;
import sophrosyne_api.core.apikeyservice.model.ApikeyView;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class ApikeyController implements IntApi {

    private final Logger logger = LogManager.getLogger(getClass());

    @Autowired
    private ApikeyService apikeyService;

    @Override
    public ResponseEntity<Void> generateNewAPIToken(Apikey apikey) {
        HttpStatus generationStatus = HttpStatus.OK;
        try {
            apikeyService.generateAPIKey(
                    apikey.getApikeyname(), apikey.getApikeydescription(), apikey.getApikeyactive());
        } catch (Exception e) {
            logger.error(e.getMessage());
            generationStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(generationStatus);
    }

    @Override
    public ResponseEntity<List<ApikeyView>> getApikeys() {
        return ResponseEntity.ok(
                apikeyService.getApiDTOs().stream()
                        .map(
                                apikeyDTO -> {
                                    return apikeyService.convertApikeyDTOToApikeyView(apikeyDTO);
                                })
                        .collect(Collectors.toList()));
    }

    @Override
    public ResponseEntity<ApikeyView> activateApikeyByApikeyName(String apikeyname) {

        Optional<ApikeyDTO> apikeyDTO =
                apikeyService.getApiDTOByApikeyname(apikeyname);
        if (apikeyDTO.isPresent()) {
            apikeyService.activateApikey(apikeyDTO.get());
            return ResponseEntity.ok(
                    apikeyService.convertApikeyDTOToApikeyView(
                            apikeyService.getApiDTOByApikey(apikeyDTO.get().getApikey()).get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Override
    public ResponseEntity<ApikeyView> deactivateApikeyByApikeyName(String apikeyname) {

        Optional<ApikeyDTO> apikeyDTO =
                apikeyService.getApiDTOByApikeyname(apikeyname);
        if (apikeyDTO.isPresent()) {
            apikeyService.deactivateApikey(apikeyDTO.get());
            return ResponseEntity.ok(
                    apikeyService.convertApikeyDTOToApikeyView(
                            apikeyService.getApiDTOByApikey(apikeyDTO.get().getApikey()).get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Override
    public ResponseEntity<List<ApikeyView>> deactivateAllApikeys() {
        apikeyService.deactivateAllApikeys();
        return ResponseEntity.ok(
                apikeyService.getApiDTOs().stream()
                        .map(
                                apikeyDTO -> {
                                    return apikeyService.convertApikeyDTOToApikeyView(apikeyDTO);
                                })
                        .collect(Collectors.toList()));
    }

    @Override
    public ResponseEntity<Void> deleteApikey(String apikeyname) {

        Optional<ApikeyDTO> apikeyDTO =
                apikeyService.getApiDTOByApikeyname(apikeyname);
        if (apikeyDTO.isPresent()) {
            apikeyService.deleteApikey(apikeyDTO.get());
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Override
    public ResponseEntity<ApikeyView> getApikeyByApikeyName(String apikeyname) {

        Optional<ApikeyDTO> apikeyDTO =
                apikeyService.getApiDTOByApikeyname(apikeyname);
        return apikeyDTO
                .map(dto -> ResponseEntity.ok(apikeyService.convertApikeyDTOToApikeyView(dto)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Override
    public ResponseEntity<Void> updateApikey(String apikey) {
        Optional<ApikeyDTO> apikeyDTO =
                apikeyService.getApiDTOByApikey(apikey);
        if (apikeyDTO.isPresent()) {
            apikeyService.updateApikey(apikeyDTO.get());
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
