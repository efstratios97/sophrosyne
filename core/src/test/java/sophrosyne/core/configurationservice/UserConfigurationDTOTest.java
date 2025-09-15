package sophrosyne.core.configurationservice;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import sophrosyne.core.PostgresIntegrationTestBase;
import sophrosyne.core.configurationservice.dto.UserConfigurationDTO;
import sophrosyne.core.controlpanelservice.dto.ControlPanelDTO;
import sophrosyne.core.userservice.dto.UserDTO;

@SpringBootTest
@TestPropertySource(locations = "/application-test.properties")
class UserConfigurationDTOTest extends PostgresIntegrationTestBase {

  @Test
  void constructorCopiesBaseFields() {
    // given
    UserDTO src = new UserDTO();
    src.setId("u-1");
    src.setUsername("alice");
    src.setFirstname("Alice");
    src.setLastname("Doe");
    src.setEmail("alice@example.com");
    src.setRole("ADMIN");
    src.setPassword("hashed");
    src.setToken("tok");
    ControlPanelDTO cp = new ControlPanelDTO();
    cp.setId("cp-1");

    // when
    UserConfigurationDTO cfg = new UserConfigurationDTO(src);

    // then
    assertThat(cfg.getId()).isEqualTo("u-1");
    assertThat(cfg.getUsername()).isEqualTo("alice");
    assertThat(cfg.getFirstname()).isEqualTo("Alice");
    assertThat(cfg.getLastname()).isEqualTo("Doe");
    assertThat(cfg.getEmail()).isEqualTo("alice@example.com");
    assertThat(cfg.getRole()).isEqualTo("ADMIN");
    assertThat(cfg.getPassword()).isEqualTo("hashed");
  }

  @Test
  void toUserDTO_resolvesControlPanel_whenPresent_withRealService() {

    // given
    UserDTO src = new UserDTO();
    src.setId("u-2");
    src.setUsername("bob");

    UserConfigurationDTO cfg = new UserConfigurationDTO(src);

    // when
    UserDTO out = cfg.toUserDTO();

    // then
    assertThat(out.getId()).isEqualTo("u-2");
    assertThat(out.getUsername()).isEqualTo("bob");
  }

  @Test
  void toUserDTO_setsNullControlPanel_whenMissingFromService_withRealService() {

    // given
    UserDTO src = new UserDTO();
    src.setId("u-3");
    src.setUsername("carol");
    ControlPanelDTO cp = new ControlPanelDTO();
    cp.setId("cp-missing"); // not persisted
    src.setControlPanelDTO(cp);

    UserConfigurationDTO cfg = new UserConfigurationDTO(src);

    // when
    UserDTO out = cfg.toUserDTO();

    // then
    assertThat(out.getControlPanelDTO()).isNull();
  }

  @Test
  void toUserDTO_whenSrcHadNoControlPanel_skipsService_andLeavesNull_withRealService() {
    // given
    UserDTO src = new UserDTO();
    src.setId("u-4");
    src.setUsername("dave");
    src.setControlPanelDTO(null);

    UserConfigurationDTO cfg = new UserConfigurationDTO(src);

    // when
    UserDTO out = cfg.toUserDTO();

    // then
    assertThat(out.getControlPanelDTO()).isNull();
  }
}
