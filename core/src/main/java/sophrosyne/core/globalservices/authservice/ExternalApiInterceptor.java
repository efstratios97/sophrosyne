package sophrosyne.core.globalservices.authservice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.NoSuchElementException;
import javax.security.sasl.AuthenticationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import sophrosyne.core.apikeyservice.service.ApikeyService;

@Configuration
public class ExternalApiInterceptor implements HandlerInterceptor {
  private final Logger logger = LogManager.getLogger(getClass());

  @Autowired private ApikeyService apikeyService;

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    try {
      String authorizationHeader = request.getHeader("Authorization");
      String accessTokenFromQuery = request.getParameter("apikey");
      String apikey = "";
      if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
        apikey = authorizationHeader.substring(7);
      } else if (accessTokenFromQuery != null && !accessTokenFromQuery.isEmpty()) {
        apikey = accessTokenFromQuery;
      } else {
        logger.error("No Authorization Header provided");
        response.setStatus(HttpStatus.NON_AUTHORITATIVE_INFORMATION.value());
        return false;
      }

      if (apikeyService.getApiDTOByApikey(apikey).get().getApikeyactive() != 1) {
        logger.error("Inactive Authorization Information");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        return false;
      }
      if (apikeyService.validateAPIKey(apikey)) {
        logger.info("Successful API Authorization");
        response.setStatus(HttpStatus.OK.value());
        return true;
      } else {
        logger.error("Invalid Authorization Information");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        return false;
      }
    } catch (AuthenticationException | NoSuchElementException ae) {
      logger.error("Authentication failed: {}", ae.getMessage());
      response.setStatus(HttpStatus.FORBIDDEN.value());
      return false;
    } catch (Exception e) {
      logger.error("Error occurred while authenticating request: {}", e.getMessage());
      response.setStatus(HttpStatus.FORBIDDEN.value());
      return false;
    }
  }
}
