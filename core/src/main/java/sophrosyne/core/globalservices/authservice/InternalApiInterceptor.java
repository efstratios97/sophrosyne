package sophrosyne.core.globalservices.authservice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.PublicKey;
import java.util.NoSuchElementException;
import javax.security.sasl.AuthenticationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

@Configuration
public class InternalApiInterceptor implements HandlerInterceptor {
  private static final String AUTH_HEADER_PARAMETER_AUTHORIZATION = "Authorization";
  private static final String AUTH_HEADER_PARAMETER_BEARER = "Bearer ";
  private final Logger logger = LogManager.getLogger(getClass());

  @Value("${request.header.username}")
  private String AUTH_HEADER_USERNAME;

  @Autowired private AuthService authService;
  @Autowired private KeyGenService keyGenService;

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    String jwtAuthToken = null;
    PublicKey tokenPublicKey = null;

    try {
      String username = request.getHeader(AUTH_HEADER_USERNAME);
      // Get JWT token from header value
      jwtAuthToken =
          request
              .getHeader(AUTH_HEADER_PARAMETER_AUTHORIZATION)
              .replace(AUTH_HEADER_PARAMETER_BEARER, "");
      // Fetching AUTH token public key from resource folder
      tokenPublicKey = keyGenService.getPublicKey();
      // Validate JWT token using public key
      return authService.validateJwtToken(jwtAuthToken, tokenPublicKey, username);
    } catch (AuthenticationException | NoSuchElementException ae) {
      logger.error("Authentication failed: {}", ae.getMessage());
    } catch (Exception e) {
      logger.error(e.getMessage());
      logger.error("Error occurred while authenticating request: {}", e.getMessage());
    }
    response.setStatus(HttpStatus.FORBIDDEN.value());
    return false;
  }
}
