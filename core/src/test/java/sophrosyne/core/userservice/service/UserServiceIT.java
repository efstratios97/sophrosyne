package sophrosyne.core.userservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;
import sophrosyne.core.PostgresIntegrationTestBase;
import sophrosyne.core.userservice.dto.UserDTO;
import sophrosyne_api.core.userservice.model.User;

@SpringBootTest
@TestPropertySource(locations = "/application-test.properties")
public class UserServiceIT extends PostgresIntegrationTestBase {

  private static final User USER_API_MODEL = createAPIUserModel();
  private final Logger logger = LogManager.getLogger(getClass());
  @Autowired private UserService sut_userService;

  private static User createAPIUserModel() {
    return new User()
        .email("test@test.de")
        .firstName("Lutz")
        .lastName("Schmutzfinger")
        .role(User.RoleEnum.ADMIN)
        .password("12345")
        .username("Lschmutzi18");
  }

  @BeforeEach
  public void cleanUpEach() {
    try {
      sut_userService.deleteUserByUsername(USER_API_MODEL.getUsername());
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  @Test
  void test_userCreation_roleADMIN() {
    UserDTO userDTO = sut_userService.createUser(USER_API_MODEL);
    assertThat(userDTO).isNotNull();
    assertThat(userDTO.getEmail()).isEqualTo(USER_API_MODEL.getEmail());
  }

  @Test
  void test_userCreation_roleUSER() {
    USER_API_MODEL.setRole(User.RoleEnum.USER);
    UserDTO userDTO = sut_userService.createUser(USER_API_MODEL);
    assertThat(userDTO).isNotNull();
    assertThat(userDTO.getEmail()).isEqualTo(USER_API_MODEL.getEmail());
  }

  @Test
  void test_userCreation_roleClient() {
    USER_API_MODEL.setRole(User.RoleEnum.CLIENT);
    UserDTO userDTO = sut_userService.createUser(USER_API_MODEL);
    assertThat(userDTO).isNotNull();
    assertThat(userDTO.getEmail()).isEqualTo(USER_API_MODEL.getEmail());
  }

  @Test
  void test_userUpdate() {
    String testToken = "myToken";
    UserDTO userDTO = sut_userService.createUser(USER_API_MODEL);
    assertThat(userDTO.getToken()).isNull();
    userDTO.setToken(testToken);
    sut_userService.updateUser(userDTO);
    userDTO = sut_userService.getUserByUserName(USER_API_MODEL.getUsername()).get();
    assertThat(userDTO).isNotNull();
    assertThat(userDTO.getToken()).isEqualTo(testToken);
  }

  @Test
  void test_userCreation_whenUserAlreadyExists() {
    sut_userService.createUser(USER_API_MODEL);
    assertThrows(
        DataIntegrityViolationException.class, () -> sut_userService.createUser(USER_API_MODEL));
  }

  @Test
  void test_getUserByUsername() {
    sut_userService.createUser(USER_API_MODEL);
    Optional<UserDTO> userDTO = sut_userService.getUserByUserName(USER_API_MODEL.getUsername());
    assertThat(userDTO).isNotNull();
    assertThat(userDTO.get().getEmail()).isEqualTo(USER_API_MODEL.getEmail());
  }

  @Test
  void test_deleteUserByUsername() {
    UserDTO userDTO = sut_userService.createUser(USER_API_MODEL);
    sut_userService.deleteUserByUsername(userDTO.getUsername());
    Optional<UserDTO> userNotFound =
        sut_userService.getUserByUserName(USER_API_MODEL.getUsername());
    assertThat(userNotFound).isEmpty();
  }
}
