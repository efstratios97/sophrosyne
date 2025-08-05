package sophrosyne.core.apiservice.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import sophrosyne.core.PostgresIntegrationTestBase;
import sophrosyne.core.apikeyservice.dto.ApikeyDTO;
import sophrosyne.core.apikeyservice.service.ApikeyService;
import sophrosyne_api.core.apikeyservice.model.Apikey;

import javax.security.sasl.AuthenticationException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@TestPropertySource(locations = "/application-test.properties")
public class ApikeyServiceIT extends PostgresIntegrationTestBase {

    private static final Apikey DUMMY_APIKEY = creatAPIKEY("TEST");
    private static final Apikey DUMMY_APIKEY_2 = creatAPIKEY("TEST2");
    private final Logger logger = LogManager.getLogger(getClass());
    @Autowired
    private ApikeyService sut_apikeyService;

    private static Apikey creatAPIKEY(String name) {

        Apikey apikey = new Apikey();
        apikey.apikeyname(name);
        apikey.apikeydescription("TEST_DESCRIPTION");
        apikey.apikeyactive(1);
        return apikey;
    }

    @BeforeEach
    public void cleanUpEach() {
        try {
            sut_apikeyService.deleteAllApikeys();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Test
    public void test_apikeyCreation() {
        ApikeyDTO apikeyDTO =
                sut_apikeyService.generateAPIKey(
                        DUMMY_APIKEY.getApikeyname(),
                        DUMMY_APIKEY.getApikeydescription(),
                        DUMMY_APIKEY.getApikeyactive());
        assertThat(sut_apikeyService.getApiDTOByApikey(apikeyDTO.getApikey()).get())
                .isNotNull()
                .isInstanceOf(ApikeyDTO.class);
        assertThat(apikeyDTO)
                .isEqualTo(sut_apikeyService.getApiDTOByApikey(apikeyDTO.getApikey()).get());
    }

    @Test
    public void test_getApikey_AllOptions() {
        ApikeyDTO apikeyDTO1 =
                sut_apikeyService.generateAPIKey(
                        DUMMY_APIKEY.getApikeyname(),
                        DUMMY_APIKEY.getApikeydescription(),
                        DUMMY_APIKEY.getApikeyactive());
        ApikeyDTO apikeyDTO2 =
                sut_apikeyService.generateAPIKey(
                        DUMMY_APIKEY_2.getApikeyname(),
                        DUMMY_APIKEY_2.getApikeydescription(),
                        DUMMY_APIKEY_2.getApikeyactive());

        ApikeyDTO apikeyDTO1FromDBByKey =
                sut_apikeyService.getApiDTOByApikey(apikeyDTO1.getApikey()).get();
        ApikeyDTO apikeyDTO2FromDBByName =
                sut_apikeyService.getApiDTOByApikeyname(apikeyDTO2.getApikeyname()).get();

        assertThat(apikeyDTO1).isNotEqualTo(apikeyDTO2);
        assertThat(apikeyDTO1FromDBByKey).isEqualTo(apikeyDTO1);
        assertThat(apikeyDTO2FromDBByName).isEqualTo(apikeyDTO2);
    }

    @Test
    public void test_getApikeys() {
        ApikeyDTO apikeyDTO1 =
                sut_apikeyService.generateAPIKey(
                        DUMMY_APIKEY.getApikeyname(),
                        DUMMY_APIKEY.getApikeydescription(),
                        DUMMY_APIKEY.getApikeyactive());
        ApikeyDTO apikeyDTO2 =
                sut_apikeyService.generateAPIKey(
                        DUMMY_APIKEY_2.getApikeyname(),
                        DUMMY_APIKEY_2.getApikeydescription(),
                        DUMMY_APIKEY_2.getApikeyactive());

        List<ApikeyDTO> apikeyDTOsFromDB = sut_apikeyService.getApiDTOs();

        assertThat(apikeyDTO1).isNotEqualTo(apikeyDTO2);
        assertThat(apikeyDTOsFromDB).hasSize(2).contains(apikeyDTO1, apikeyDTO2);
    }

    @Test
    public void test_DeactivateApikey() {
        ApikeyDTO apikeyDTO1 =
                sut_apikeyService.generateAPIKey(
                        DUMMY_APIKEY.getApikeyname(),
                        DUMMY_APIKEY.getApikeydescription(),
                        DUMMY_APIKEY.getApikeyactive());

        sut_apikeyService.deactivateApikey(apikeyDTO1);

        ApikeyDTO apikeyDTOFromDB = sut_apikeyService.getApiDTOByApikey(apikeyDTO1.getApikey()).get();

        assertThat(apikeyDTOFromDB.getApikeyactive()).isZero();
    }

    @Test
    public void test_DeactivateAll() {
        sut_apikeyService.generateAPIKey(
                DUMMY_APIKEY.getApikeyname(),
                DUMMY_APIKEY.getApikeydescription(),
                DUMMY_APIKEY.getApikeyactive());
        sut_apikeyService.generateAPIKey(
                DUMMY_APIKEY_2.getApikeyname(),
                DUMMY_APIKEY_2.getApikeydescription(),
                DUMMY_APIKEY_2.getApikeyactive());
        sut_apikeyService.generateAPIKey(
                "Test3", DUMMY_APIKEY.getApikeydescription(), DUMMY_APIKEY.getApikeyactive());
        sut_apikeyService.generateAPIKey(
                "Test4", DUMMY_APIKEY_2.getApikeydescription(), DUMMY_APIKEY_2.getApikeyactive());

        sut_apikeyService.deactivateAllApikeys();

        List<ApikeyDTO> apikeyDTOS = sut_apikeyService.getApiDTOs();
        apikeyDTOS.forEach(
                apikey -> {
                    assertThat(apikey.getApikeyactive()).isZero();
                });
    }

    @Test
    public void test_DeleteApikey() {
        ApikeyDTO apikeyDTO1 =
                sut_apikeyService.generateAPIKey(
                        DUMMY_APIKEY.getApikeyname(),
                        DUMMY_APIKEY.getApikeydescription(),
                        DUMMY_APIKEY.getApikeyactive());

        sut_apikeyService.deactivateApikey(apikeyDTO1);

        ApikeyDTO apikeyDTOFromDB = sut_apikeyService.getApiDTOByApikey(apikeyDTO1.getApikey()).get();

        assertThat(apikeyDTOFromDB.getApikeyactive()).isZero();
    }

    @Test
    public void test_apikeyValidation()
            throws AuthenticationException, NoSuchAlgorithmException, InvalidKeySpecException {
        ApikeyDTO apikeyDTO =
                sut_apikeyService.generateAPIKey(
                        DUMMY_APIKEY.getApikeyname(),
                        DUMMY_APIKEY.getApikeydescription(),
                        DUMMY_APIKEY.getApikeyactive());

        assertThat(sut_apikeyService.validateAPIKey(apikeyDTO.getApikey())).isTrue();
    }

    @Test
    public void test_apikeyValidation_WhenApikeyIsInvalid() {
        ApikeyDTO apikeyDTOInvalid =
                sut_apikeyService.generateAPIKey(
                        DUMMY_APIKEY.getApikeyname(),
                        DUMMY_APIKEY.getApikeydescription(),
                        DUMMY_APIKEY.getApikeyactive());

        apikeyDTOInvalid.setPublickey(apikeyDTOInvalid.getPublickey().toLowerCase());
        sut_apikeyService.updateApikey(apikeyDTOInvalid);

        assertThrows(
                InvalidKeySpecException.class,
                () -> sut_apikeyService.validateAPIKey(apikeyDTOInvalid.getApikey()));
    }
}
