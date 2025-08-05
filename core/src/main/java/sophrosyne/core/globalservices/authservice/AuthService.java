package sophrosyne.core.globalservices.authservice;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import javax.security.sasl.AuthenticationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final Logger logger = LogManager.getLogger(getClass());

  @Autowired private KeyGenService keyGenService;

  @Value("${token.lifespan}")
  private int TOKENLIFESPAN;

  @Value("${token.issuer}")
  private String ISSUER;

  public String generateJWSToken(String username) {

    // Retrieve private key
    PrivateKey privateKey = keyGenService.getPrivateKey();

    // Generate token id.
    String tokenId = "sophrosyne_internal_auth_token_" + UUID.randomUUID();

    // Generate token issued date.
    Calendar cal = Calendar.getInstance();
    Date tokenIssuedAt = cal.getTime();

    // Generate token expiration time.
    cal.add(Calendar.SECOND, TOKENLIFESPAN);
    Date tokenExpirationAt = cal.getTime();

    return Jwts.builder()
        .id(tokenId)
        .subject(username)
        .issuer(ISSUER)
        .issuedAt(tokenIssuedAt)
        .notBefore(tokenIssuedAt)
        .expiration(tokenExpirationAt)
        .signWith(privateKey)
        .compact();
  }

  public Boolean validateJwtToken(String jwtAuthToken, PublicKey tokenPublicKey, String username)
      throws AuthenticationException {
    try {
      Jws<Claims> jws;
      jws = Jwts.parser().verifyWith(tokenPublicKey).build().parseSignedClaims(jwtAuthToken);
      if ((jws.getPayload().getIssuer().equals(ISSUER))
          && (jws.getPayload().getExpiration().compareTo(new Date()) > 0)
          && (jws.getPayload().getNotBefore().compareTo(new Date()) < 0)
          && jws.getPayload().getSubject().equals(username)) {
        return true;
      }
      throw new AuthenticationException("Invalid JWT token");
    } catch (JwtException e) {
      logger.error(e.getMessage());
      throw new AuthenticationException("Invalid JWT token signature");
    }
  }

  public Boolean validateAuthenticUserWithJwtToken(
      String username, String jwtAuthToken, PublicKey tokenPublicKey)
      throws AuthenticationException {
    try {
      Jws<Claims> jws = null;
      jws = Jwts.parser().verifyWith(tokenPublicKey).build().parseSignedClaims(jwtAuthToken);
      if (jws.getPayload().getSubject().equals(username)) {
        return true;
      }
      throw new AuthenticationException("Token does not belong to claimed user");
    } catch (JwtException e) {
      logger.error(e.getMessage());
      throw new AuthenticationException("Invalid JWT token signature");
    }
  }

  public String generateAPIKey(PrivateKey privateKey) {
    String tokenId = "sophrosyne_api_key" + UUID.randomUUID();
    return Jwts.builder()
        .id(tokenId)
        .subject("sophrosyne_core")
        .issuer(ISSUER)
        .issuedAt(new Date())
        .notBefore(new Date())
        .expiration(new Date(Long.MAX_VALUE))
        .signWith(privateKey)
        .compact();
  }

  public Boolean validateAPIKey(String apiKey, PublicKey apiPublicKey)
      throws AuthenticationException {
    try {
      Jws<Claims> jws;
      jws = Jwts.parser().verifyWith(apiPublicKey).build().parseSignedClaims(apiKey);
      if ((jws.getPayload().getIssuer().equals(ISSUER))
          && (jws.getPayload().getExpiration().compareTo(new Date()) > 0)
          && (jws.getPayload().getNotBefore().compareTo(new Date()) < 0)) {

        return true;
      }
      throw new AuthenticationException("Invalid API Key");
    } catch (JwtException e) {
      logger.error(e.getMessage());
      throw new AuthenticationException("Invalid API signature");
    }
  }
}
