package sophrosyne.core.userservice.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import sophrosyne.core.userservice.dto.UserDTO;
import sophrosyne_api.core.userservice.model.User;
import sophrosyne_api.core.userservice.model.UserView;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@TestPropertySource(locations = "/application-test.properties")
public class UserServiceTest {

    private static final User USER_API_MODEL = createUserAPIModel();
    private static final UserDTO USER_DTO_MODEL = createUserDTOModel();
    @Autowired
    private UserService sut_userService;

    private static User createUserAPIModel() {
        return new User()
                .email("test@test.de")
                .firstName("Lutz")
                .lastName("Schmutzfinger")
                .role(User.RoleEnum.ADMIN)
                .password("12345")
                .username("Lschmutzi18");
    }

    private static UserDTO createUserDTOModel() {
        UserDTO testUser = new UserDTO();
        testUser.setEmail("test@test.de");
        testUser.setFirstname("Lutz");
        testUser.setLastname("Schmutzfinger");
        testUser.setRole(UserDTO.ROLES.ADMIN.name());
        testUser.setId("1");
        testUser.setPassword("12345");
        testUser.setUsername("Lschmutzi18");
        return testUser;
    }

    @Test
    void test_userDtoFromApiModelUser() {

        UserView userView = sut_userService.mapUserToUserView(USER_API_MODEL);

        assertThat(userView).isNotNull();
        assertThat(userView.getEmail()).isEqualTo(USER_API_MODEL.getEmail());
        assertThat(userView.getFirstName()).isEqualTo(USER_API_MODEL.getFirstName());
        assertThat(userView.getLastName()).isEqualTo(USER_API_MODEL.getLastName());
        assertThat(userView.getRole().getValue()).isEqualTo(USER_API_MODEL.getRole().getValue());

    }

    @Test
    void test_userApiModelMappingToUserDTO() {

        UserDTO userDTO = sut_userService.createNewUser(USER_API_MODEL);

        assertThat(userDTO).isNotNull();
        assertThat(userDTO.getEmail()).isEqualTo(USER_API_MODEL.getEmail());
        assertThat(userDTO.getFirstname()).isEqualTo(USER_API_MODEL.getFirstName());
        assertThat(userDTO.getLastname()).isEqualTo(USER_API_MODEL.getLastName());
        assertThat(userDTO.getRole()).isEqualTo(USER_API_MODEL.getRole().getValue());
        assertThat(userDTO.getPassword()).isNotEqualTo(USER_API_MODEL.getPassword());
        assertThat(userDTO.getId()).isNotNull().isInstanceOf(String.class);
    }

    @Test
    void test_userDTOModelMappingToUserView() {
        UserView userView = sut_userService.mapUserDtoToUserView(USER_DTO_MODEL);
        assertThat(userView.getEmail()).isEqualTo(USER_DTO_MODEL.getEmail());
        assertThat(userView.getFirstName()).isEqualTo(USER_DTO_MODEL.getFirstname());
        assertThat(userView.getLastName()).isEqualTo(USER_DTO_MODEL.getLastname());
        assertThat(userView.getUsername()).isEqualTo(USER_DTO_MODEL.getUsername());
        assertThat(userView.getRole().name()).isEqualTo(USER_DTO_MODEL.getRole());

    }

    @Test
    void test_passwordAuthentification() {

        UserDTO userDTO = sut_userService.createNewUser(USER_API_MODEL);

        // Correct password
        assertThat(userDTO.authenticatePassword(USER_API_MODEL.getPassword())).isTrue();
        // Wrong password
        assertThat(userDTO.authenticatePassword("WRONG_PASSWORD")).isFalse();

    }
}
