package sophrosyne.core.configurationservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import sophrosyne.core.PostgresIntegrationTestBase;
import sophrosyne.core.actionrecommendationservice.dto.ActionRecommendationDTO;
import sophrosyne.core.apikeyservice.dto.ApikeyDTO;
import sophrosyne.core.apikeyservice.service.ApikeyService;
import sophrosyne.core.configurationservice.dto.ActionRecommendationConfigurationDTO;

@SpringBootTest
@TestPropertySource(locations = "/application-test.properties")
public class ActionRecommendationConfigurationDTOTest extends PostgresIntegrationTestBase {

  @Autowired ApikeyService apikeyService;

  private static ApikeyDTO api(String name) {
    ApikeyDTO a = new ApikeyDTO();
    a.setApikeyname(name);
    return a;
  }

  @BeforeEach
  public void generateApikey() {
    apikeyService.deleteAllApikeys();
  }

  @AfterEach
  public void cleanUp() {
    apikeyService.deleteAllApikeys();
  }

  @Test
  void constructorCopiesBaseFields() {
    // given
    ActionRecommendationDTO src = new ActionRecommendationDTO();
    src.setId("rec-1");
    src.setName("Recommend X");
    src.setDescription("Do X because Y");
    src.setResponsibleEntity("Ops");
    src.setContactInformation("ops@example.com");
    byte[] doc = "PDF".getBytes(StandardCharsets.UTF_8);
    src.setAdditionalDocumentation(doc);
    // allowed API keys don't need to exist in DB for constructor test
    src.setAllowedApikeys(new HashSet<>(Set.of(api("K1"), api("K2"))));

    // when
    ActionRecommendationConfigurationDTO cfg = new ActionRecommendationConfigurationDTO(src);

    // then (verify base fields copied)
    assertThat(cfg.getId()).isEqualTo("rec-1");
    assertThat(cfg.getName()).isEqualTo("Recommend X");
    assertThat(cfg.getDescription()).isEqualTo("Do X because Y");
    assertThat(cfg.getResponsibleEntity()).isEqualTo("Ops");
    assertThat(cfg.getContactInformation()).isEqualTo("ops@example.com");
    assertThat(cfg.getAdditionalDocumentation()).containsExactly(doc);
  }

  @Test
  void toActionRecommendationDTO_resolvesAllowedApikeysAndFiltersMissing_withRealService() {
    // prepare DB: make sure K_OK1 and K_OK2 exist; K_MISSING does not
    apikeyService.generateAPIKey("K_OK1", "test", 1);
    apikeyService.generateAPIKey("K_OK2", "test", 1);

    // given source with 3 names (one intentionally missing)
    ActionRecommendationDTO src = new ActionRecommendationDTO();
    src.setId("rec-2");
    src.setName("Name");
    src.setDescription("Desc");
    src.setResponsibleEntity("Team");
    src.setContactInformation("team@example.com");
    byte[] doc = "DOC".getBytes(StandardCharsets.UTF_8);
    src.setAdditionalDocumentation(doc);
    src.setAllowedApikeys(new HashSet<>(Set.of(api("K_OK1"), api("K_MISSING"), api("K_OK2"))));

    ActionRecommendationConfigurationDTO cfg = new ActionRecommendationConfigurationDTO(src);
    // inject real service (DTO is not a Spring bean)
    cfg.setApikeyService(apikeyService);

    // when
    ActionRecommendationDTO out = cfg.toActionRecommendationDTO();

    // then: base fields
    assertThat(out.getId()).isEqualTo("rec-2");
    assertThat(out.getName()).isEqualTo("Name");
    assertThat(out.getDescription()).isEqualTo("Desc");
    assertThat(out.getResponsibleEntity()).isEqualTo("Team");
    assertThat(out.getContactInformation()).isEqualTo("team@example.com");
    assertThat(out.getAdditionalDocumentation()).containsExactly(doc);

    // allowed apikeys resolved via real service; missing filtered
    assertThat(out.getAllowedApikeys()).isNotNull();
    Set<String> names =
        out.getAllowedApikeys().stream()
            .map(ApikeyDTO::getApikeyname)
            .collect(java.util.stream.Collectors.toSet());
    assertThat(names).containsExactlyInAnyOrder("K_OK1", "K_OK2");
  }

  @Test
  void toActionRecommendationDTO_whenNoAllowedApikeys_returnsEmptySet_withRealService() {
    // given
    ActionRecommendationDTO src = new ActionRecommendationDTO();
    src.setId("rec-3");
    src.setName("Empty Keys");
    src.setAllowedApikeys(Collections.emptySet());

    ActionRecommendationConfigurationDTO cfg = new ActionRecommendationConfigurationDTO(src);
    cfg.setApikeyService(apikeyService);

    // when
    ActionRecommendationDTO out = cfg.toActionRecommendationDTO();

    // then
    assertThat(out.getAllowedApikeys()).isNotNull().isEmpty();
  }
}
