package sophrosyne.core.globalservices.authservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.security.sasl.AuthenticationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestPropertySource(locations = "/application-test.properties")
public class AuthServiceTest {

  @Autowired private AuthService sut_authService;

  @Autowired private KeyGenService keyGenService;

  @Test
  public void test_tokenCreation() {
    String token = sut_authService.generateJWSToken("Lschmuti18");
    assertThat(token).isInstanceOf(String.class);
  }

  @Test
  public void test_tokenValidation_whenWrongPublicKey() {
    String token = sut_authService.generateJWSToken("Lschmuti18");
    keyGenService.createKeys();
    assertThrows(
        AuthenticationException.class,
        () -> sut_authService.validateJwtToken(token, keyGenService.getPublicKey(), "Lschmuti18"));
  }
}
