package sophrosyne.core.userservice.controller;

import java.security.SecureRandom;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import sophrosyne.core.globalservices.authservice.AuthService;
import sophrosyne.core.globalservices.authservice.KeyGenService;
import sophrosyne.core.userservice.dto.UserDTO;
import sophrosyne.core.userservice.service.UserService;
import sophrosyne_api.core.userservice.api.AuthApi;
import sophrosyne_api.core.userservice.api.IntApi;
import sophrosyne_api.core.userservice.model.User;
import sophrosyne_api.core.userservice.model.UserView;

@RestController
class UserServiceController implements IntApi, AuthApi {

  private final Logger logger = LogManager.getLogger(getClass());

  @Autowired UserService userService;

  @Autowired AuthService authService;

  @Autowired private KeyGenService keyGenService;

  @Override
  public Optional<NativeWebRequest> getRequest() {
    return IntApi.super.getRequest();
  }

  @Override
  public ResponseEntity<String> createUser(User user) {
    byte[] randomBytes = new byte[10];
    SecureRandom secureRandom = new SecureRandom();
    secureRandom.nextBytes(randomBytes);
    Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
    String intPassword = encoder.encodeToString(randomBytes);
    user.password(intPassword);
    userService.createUser(user);
    return ResponseEntity.ok(intPassword);
  }

  @Override
  public ResponseEntity<Void> deleteUser(String username) {

    List<UserDTO> users = userService.getUsers();
    long adminCountLeft =
        users.stream()
            .filter(userDTO -> Objects.equals(userDTO.getRole(), UserDTO.ROLES.ADMIN.name()))
            .count();
    if (adminCountLeft > 1
        || userService.getUserByUserName(username).get().getRole().equals(UserDTO.ROLES.USER.name())
        || userService
            .getUserByUserName(username)
            .get()
            .getRole()
            .equals(UserDTO.ROLES.CLIENT.name())) {
      userService.deleteUserByUsername(username);
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
    }
  }

  @Override
  public ResponseEntity<UserView> getUserByUserName(String username) {

    Optional<UserDTO> user;
    user = userService.getUserByUserName(username);

    return user.map(userDTO -> ResponseEntity.ok(userService.mapUserDtoToUserView(userDTO)))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @Override
  public ResponseEntity<List<UserView>> getUsers() {
    List<UserView> userViews =
        userService.getUsers().stream()
            .map(
                userDTO -> {
                  return userService.mapUserDtoToUserView(userDTO);
                })
            .toList();
    return ResponseEntity.ok(userViews);
  }

  @Override
  public ResponseEntity<Void> logoutUser(String username) {

    UserDTO userDTO = userService.getUserByUserName(username).get();
    userDTO.setToken("");
    userService.updateUser(userDTO);

    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> updateUser(String username, User user) {
    try {
      UserDTO userDTO = userService.getUserByUserName(username).get();
      userDTO = userService.mapUserToExistentUserDto(user, userDTO, userDTO.getPassword());
      userService.updateUser(userDTO);
    } catch (NoSuchElementException e) {
      logger.error(e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<UserView> updateLoggedUser(String username, User user) {
    UserDTO userDTO = null;
    try {
      boolean genuineUser = userService.checkGenuineUser(username);
      if (genuineUser) {
        userDTO = userService.getUserByUserName(username).get();
        user.setRole(User.RoleEnum.valueOf(userDTO.getRole()));
        userDTO =
            userService.mapUserToExistentUserDto(
                user, userDTO, UserDTO.encodePassword(user.getPassword(), userDTO.getId()));
        userService.updateUser(userDTO);
      }
    } catch (NoSuchElementException e) {
      logger.error(e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    return ResponseEntity.ok(userService.mapUserDtoToUserView(userDTO));
  }

  @Override
  public ResponseEntity<String> loginUser(String password, String username) {

    boolean authenticated = false;
    Optional<UserDTO> user;
    user = userService.getUserByUserName(username);

    if (user.isPresent()) {
      authenticated = user.get().authenticatePassword(password);
      if (authenticated) {
        String token = authService.generateJWSToken(username);
        user.get().setToken(token);
        userService.updateUser(user.get());

        return ResponseEntity.ok(token);
      }
    }
    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
  }

  @Override
  public ResponseEntity<UserView> getLoggedUserData(String username) {

    Optional<UserDTO> user;
    boolean genuineUser = userService.checkGenuineUser(username);
    user = userService.getUserByUserName(username);

    if (genuineUser) {
      try {
        return user.map(userDTO -> ResponseEntity.ok(userService.mapUserDtoToUserView(userDTO)))
            .get();
      } catch (Exception e) {
        logger.error(e.getMessage());
      }
    }
    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
  }
}
