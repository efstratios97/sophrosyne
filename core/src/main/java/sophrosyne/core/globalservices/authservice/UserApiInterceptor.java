package sophrosyne.core.globalservices.authservice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.NoSuchElementException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import sophrosyne.core.userservice.dto.UserDTO;
import sophrosyne.core.userservice.service.UserService;

@Service
public class UserApiInterceptor implements HandlerInterceptor {

  private final Logger logger = LogManager.getLogger(getClass());

  @Autowired private UserService userService;

  @Value("${request.header.username}")
  private String AUTH_HEADER_USERNAME;

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    try {
      String username = request.getHeader(AUTH_HEADER_USERNAME);
      UserDTO userDTO = userService.getUserByUserName(username).get();
      return userDTO.getRole().equals(UserDTO.ROLES.USER.name())
          || userDTO.getRole().equals(UserDTO.ROLES.ADMIN.name());
    } catch (NoSuchElementException e) {
      logger.error("Authorization failed: {}", e.getMessage());
    }
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    return false;
  }
}
