package sophrosyne.core;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest()
@TestPropertySource(locations = "/application-test.properties")
class CoreApplicationTests {

  @Test
  void contextLoads() {}
}
