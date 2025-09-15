package sophrosyne.core.configurationservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.internal.util.collections.CollectionHelper.setOf;

import org.junit.jupiter.api.Test;
import sophrosyne.core.actionservice.dto.ActionDTO;
import sophrosyne.core.apikeyservice.dto.ApikeyDTO;
import sophrosyne.core.configurationservice.dto.ActionConfigurationDTO;

class ActionConfigurationDTOTest {

  private static ApikeyDTO api(String name) {
    ApikeyDTO a = new ApikeyDTO();
    a.setApikeyname(name);
    return a;
  }

  @Test
  void constructorCopiesBaseFields() {
    // given
    ActionDTO src = new ActionDTO();
    src.setId("a-1");
    src.setName("Action Name");
    src.setCommand("run -x");
    src.setDescription("desc");
    src.setVersion("1.2.3");
    src.setPostExecutionLogFilePath("/tmp/log.txt");
    src.setRequiresConfirmation(1);
    src.setAllowedApikeys(setOf(api("K1"), api("K2")));

    // when
    ActionConfigurationDTO cfg = new ActionConfigurationDTO(src);

    // then
    assertThat(cfg.getId()).isEqualTo("a-1");
    assertThat(cfg.getName()).isEqualTo("Action Name");
    assertThat(cfg.getCommand()).isEqualTo("run -x");
    assertThat(cfg.getDescription()).isEqualTo("desc");
    assertThat(cfg.getVersion()).isEqualTo("1.2.3");
    assertThat(cfg.getPostExecutionLogFilePath()).isEqualTo("/tmp/log.txt");
    assertThat(cfg.getRequiresConfirmation()).isEqualTo(1);
  }

  @Test
  void toActionDTO_resolvesAllowedApikeys_andFiltersMissing() {
    // given a source action with 3 keys (one missing)
    ActionDTO src = new ActionDTO();
    src.setId("a-2");
    src.setName("Name");
    src.setCommand("cmd");
    src.setDescription("desc");
    src.setVersion("v");
    src.setPostExecutionLogFilePath("/p");
    src.setRequiresConfirmation(0);
    src.setAllowedApikeys(setOf(api("OK1"), api("MISSING"), api("OK2")));
  }
}
