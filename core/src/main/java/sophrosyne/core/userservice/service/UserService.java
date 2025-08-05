package sophrosyne.core.userservice.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import sophrosyne.core.configurationservice.service.ConfigurationService;
import sophrosyne.core.globalservices.authservice.AuthService;
import sophrosyne.core.globalservices.authservice.KeyGenService;
import sophrosyne.core.userservice.dto.UserDTO;
import sophrosyne.core.userservice.repository.UserServiceRepository;
import sophrosyne_api.core.userservice.model.User;
import sophrosyne_api.core.userservice.model.UserView;

import javax.security.sasl.AuthenticationException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService {

  private final Logger logger = LogManager.getLogger(getClass());

  @Autowired private UserServiceRepository userServiceRepository;

  @Autowired private AuthService authService;

  @Autowired private KeyGenService keyGenService;

  @Autowired @Lazy private ConfigurationService configurationService;

  public UserDTO createUser(User user) {
    UserDTO userDTO = createNewUser(user);
    userServiceRepository.save(userDTO);

    return userDTO;
  }

  public void updateUser(UserDTO user) {
    userServiceRepository.save(user);
  }

  public List<UserDTO> getUsers() {
    return userServiceRepository.findAll();
  }

  public Optional<UserDTO> getUserByUserName(String username) {

    return userServiceRepository.findByUsername(username);
  }

  public Optional<UserDTO> getUserById(String id) {
    return userServiceRepository.findById(id);
  }

  public void deleteUserByUsername(String username) {

    userServiceRepository.deleteByUsername(username);
  }

  public void deleteAllUsers() {
    userServiceRepository.deleteAll();
  }

  public UserDTO createNewUser(User user) {
    String userId = UserDTO.generateUserId();
    String userPassword = UserDTO.encodePassword(user.getPassword(), userId);
    return UserDTO.builder()
        .id(userId)
        .email(user.getEmail())
        .username(user.getUsername())
        .firstname(user.getFirstName())
        .lastname(user.getLastName())
        .email(user.getEmail())
        .role((user.getRole().getValue()))
        .password(userPassword)
        .build();
  }

  public boolean checkGenuineUser(String username) {

    boolean genuineUser = false;
    Optional<UserDTO> user;
    try {
      user = getUserByUserName(username);
      genuineUser =
          authService.validateAuthenticUserWithJwtToken(
              username, user.get().getToken(), keyGenService.getPublicKey());
    } catch (AuthenticationException | NoSuchElementException e) {
      logger.error(e.getMessage());
      return genuineUser;
    }

    return genuineUser;
  }

  public UserView mapUserToUserView(User user) {
    return new UserView()
        .email(user.getEmail())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .username(user.getUsername())
        .role(UserView.RoleEnum.valueOf(user.getRole().toString()));
  }

  public UserView mapUserDtoToUserView(UserDTO user) {
    try {
      return new UserView()
          .id(user.getId())
          .email(user.getEmail())
          .firstName(user.getFirstname())
          .lastName(user.getLastname())
          .username(user.getUsername())
          .role(UserView.RoleEnum.valueOf(user.getRole()));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public UserDTO mapUserToExistentUserDto(User user, UserDTO userDTO, String encodedPassword) {
    userDTO.setEmail(user.getEmail());
    userDTO.setFirstname(user.getFirstName());
    userDTO.setLastname(user.getLastName());
    userDTO.setPassword(encodedPassword);
    userDTO.setUsername(user.getUsername());
    userDTO.setRole(user.getRole().getValue());
    return userDTO;
  }
}
