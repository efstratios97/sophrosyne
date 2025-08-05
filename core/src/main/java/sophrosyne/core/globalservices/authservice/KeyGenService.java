package sophrosyne.core.globalservices.authservice;

import java.security.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KeyGenService {

  private final Logger logger = LogManager.getLogger(getClass());

  @Value("${keygen.algorithm}")
  private String algorithm;

  @Value("${keygen.length}")
  private int length;

  private KeyPair pair;

  public void createKeys() {
    pair = createKeyPair();
  }

  public KeyPair createKeysTemp() {
    return createKeyPair();
  }

  private KeyPair createKeyPair() {
    KeyPairGenerator keyPairGenerator;
    try {
      keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
    } catch (NoSuchAlgorithmException e) {
      logger.error(e.getMessage());
      throw new RuntimeException(e);
    }
    keyPairGenerator.initialize(length);
    return keyPairGenerator.generateKeyPair();
  }

  public PublicKey getPublicKey() {
    try {
      if (pair == null) {
        throw new KeyException();
      }
    } catch (KeyException e) {
      logger.error(e.getMessage());
      createKeys();
    }
    return pair.getPublic();
  }

  public PrivateKey getPrivateKey() {
    try {
      if (pair == null) {
        throw new KeyException();
      }
    } catch (KeyException e) {
      logger.error(e.getMessage());
      createKeys();
    }
    return pair.getPrivate();
  }
}
