package sophrosyne.core.apikeyservice.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import sophrosyne.core.apikeyservice.dto.ApikeyDTO;
import sophrosyne.core.apikeyservice.repository.ApikeyRepository;
import sophrosyne.core.configurationservice.service.ConfigurationService;
import sophrosyne.core.globalservices.authservice.AuthService;
import sophrosyne.core.globalservices.authservice.KeyGenService;
import sophrosyne_api.core.apikeyservice.model.ApikeyView;

import javax.security.sasl.AuthenticationException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class ApikeyService {

    private final Logger logger = LogManager.getLogger(getClass());

    @Value("${keygen.algorithm}")
    private String algorithm;

    @Autowired
    private ApikeyRepository apikeyRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private KeyGenService keyGenService;

    @Autowired
    @Lazy
    private ConfigurationService configurationService;

    public ApikeyDTO generateAPIKey(String apikeyName, String apikeyDescription, int apikeyActive) {

        KeyPair apikeys = keyGenService.createKeysTemp();
        String apikey = authService.generateAPIKey(apikeys.getPrivate());
        String pubkey = Base64.getEncoder().encodeToString(apikeys.getPublic().getEncoded());
        String prvkey = Base64.getEncoder().encodeToString(apikeys.getPrivate().getEncoded());
        ApikeyDTO apiKeyDTO =
                createApiDTO(apikeyName, apikeyDescription, apikey, apikeyActive, prvkey, pubkey);
        apikeyRepository.save(apiKeyDTO);

        return apiKeyDTO;
    }

    public boolean validateAPIKey(String apikey)
            throws AuthenticationException, NoSuchAlgorithmException, InvalidKeySpecException {
        PublicKey publicKey;
        Optional<ApikeyDTO> apikeyDTO = getApiDTOByApikey(apikey);
        if (apikeyDTO.isEmpty()) {
            throw new RuntimeException("No matching apikey found");
        }
        byte[] publicBytes = Base64.getDecoder().decode(apikeyDTO.get().getPublickey());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        publicKey = keyFactory.generatePublic(keySpec);

        return authService.validateAPIKey(apikey, publicKey);
    }

    public List<ApikeyDTO> getApiDTOs() {
        return apikeyRepository.findAll();
    }

    public Optional<ApikeyDTO> getApiDTOByApikey(String apikey) {
        return apikeyRepository.findByApikey(apikey);
    }

    public Optional<ApikeyDTO> getApiDTOByApikeyname(String apikeyname) {

        return apikeyRepository.findByApikeyname(apikeyname);
    }

    public void deleteApikey(ApikeyDTO apikeyDTO) {
        apikeyRepository.delete(apikeyDTO);

    }

    public void deleteAllApikeys() {
        apikeyRepository.deleteAll();
    }

    public void activateApikey(ApikeyDTO apikeyDTO) {
        apikeyDTO.setApikeyactive(1);
        updateApikey(apikeyDTO);
    }

    public void deactivateApikey(ApikeyDTO apikeyDTO) {
        apikeyDTO.setApikeyactive(0);
        updateApikey(apikeyDTO);
    }

    public void deactivateAllApikeys() {
        List<CompletableFuture<Void>> deactivationJobs = new ArrayList<>();
        getApiDTOs()
                .forEach(
                        apikeyDTO -> {
                            CompletableFuture<Void> completableFuture =
                                    CompletableFuture.runAsync(() -> deactivateApikey(apikeyDTO));
                            deactivationJobs.add(completableFuture);
                        });
        deactivationJobs.forEach(
                deactivationJob -> {
                    try {
                        deactivationJob.get();
                    } catch (InterruptedException | ExecutionException e) {
                        logger.error(e.getMessage());
                        throw new RuntimeException(e);
                    }
                });
    }

    public void updateApikey(ApikeyDTO apikeyDTO) {
        apikeyRepository.save(apikeyDTO);

    }

    private ApikeyDTO createApiDTO(
            String apikeyName,
            String apikeyDescription,
            String apikey,
            int apikeyActive,
            String privateKey,
            String publicKey) {
        return ApikeyDTO.builder()
                .apikeyname(apikeyName)
                .apikeydescription(apikeyDescription)
                .apikeyactive(apikeyActive)
                .apikey(apikey)
                .privatekey(privateKey)
                .publickey(publicKey)
                .build();
    }

    public ApikeyView convertApikeyDTOToApikeyView(ApikeyDTO apikeyDTO) {
        return new ApikeyView()
                .apikey(apikeyDTO.getApikey())
                .apikeyname(apikeyDTO.getApikeyname())
                .apikeydescription(apikeyDTO.getApikeydescription())
                .apikeyactive(apikeyDTO.getApikeyactive());
    }
}
