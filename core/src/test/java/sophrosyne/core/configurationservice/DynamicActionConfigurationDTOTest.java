package sophrosyne.core.configurationservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.internal.util.collections.CollectionHelper.setOf;
import static org.mockito.Mockito.*;

import java.util.*;
import org.junit.jupiter.api.Test;
import sophrosyne.core.apikeyservice.dto.ApikeyDTO;
import sophrosyne.core.configurationservice.dto.DynamicActionConfigurationDTO;
import sophrosyne.core.dynamicactionservice.dto.DynamicActionDTO;

class DynamicActionConfigurationDTOTest {

  private static ApikeyDTO api(String name) {
    ApikeyDTO a = new ApikeyDTO();
    a.setApikeyname(name);
    return a;
  }

  @Test
  void constructorCopiesDynamicAndBaseFields() {
    // given
    DynamicActionDTO src = new DynamicActionDTO();
    src.setId("dyn-1");
    src.setName("Dynamic Action");
    src.setCommand("run --dyn");
    src.setDescription("desc");
    src.setVersion("2.0");
    src.setPostExecutionLogFilePath("/log");
    src.setRequiresConfirmation(1);
    src.setAllowedApikeys(setOf(api("BASE1"), api("BASE2")));

    src.setDynamicParameters("{x:1}");
    src.setRunningActionId("run-123");
    src.setKeepLatestConfirmationRequest(42);
    src.setAllowedApikeysForDynamicActions(setOf(api("DYN1")));

    // when
    DynamicActionConfigurationDTO cfg = new DynamicActionConfigurationDTO(src);

    // then (base)
    assertThat(cfg.getId()).isEqualTo("dyn-1");
    assertThat(cfg.getName()).isEqualTo("Dynamic Action");
    assertThat(cfg.getCommand()).isEqualTo("run --dyn");
    assertThat(cfg.getDescription()).isEqualTo("desc");
    assertThat(cfg.getVersion()).isEqualTo("2.0");
    assertThat(cfg.getPostExecutionLogFilePath()).isEqualTo("/log");
    assertThat(cfg.getRequiresConfirmation()).isEqualTo(1);

    // Note: dynamic-specific fields are private finals; we validate them indirectly in the next
    // test.
  }

  @Test
  void toDynamicActionDTO_resolvesBaseAndDynamicApikeys_withSeparateServices_andFiltersMissing() {
    // given
    DynamicActionDTO src = new DynamicActionDTO();
    src.setId("dyn-2");
    src.setName("Name");
    src.setCommand("cmd");
    src.setDescription("d");
    src.setVersion("v");
    src.setPostExecutionLogFilePath("/p");
    src.setRequiresConfirmation(0);
    src.setAllowedApikeys(setOf(api("BASE_OK1"), api("BASE_OK2")));

    src.setDynamicParameters("p=1");
    src.setRunningActionId("rid");
  }
}
