package sophrosyne.core.globalservices.authservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.security.PrivateKey;
import java.security.PublicKey;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations = "/application-test.properties")
public class KeyGenServiceTest {

  @Autowired private KeyGenService sut_keyGenService;

  @Value("${keygen.algorithm}")
  private String algorithm;

  @Test
  public void test_keyGenProperties() {
    sut_keyGenService.createKeys();
    assertThat(sut_keyGenService.getPublicKey().getAlgorithm()).isEqualTo(algorithm);
    assertThat(sut_keyGenService.getPrivateKey().getAlgorithm()).isEqualTo(algorithm);
  }

  @Test
  public void test_keyPairGeneration() {
    sut_keyGenService.createKeys();
    assertThat(sut_keyGenService.getPublicKey()).isNotNull().isInstanceOf(PublicKey.class);
    assertThat(sut_keyGenService.getPrivateKey()).isNotNull().isInstanceOf(PrivateKey.class);
  }

  @Test
  public void test_keyPairGeneration_whenNoKeyPairExists() {
    PublicKey publicKey = sut_keyGenService.getPublicKey();
    assertThat(publicKey).isNotNull().isInstanceOf(PublicKey.class);
    assertThat(sut_keyGenService.getPrivateKey()).isNotNull().isInstanceOf(PrivateKey.class);
  }
}
