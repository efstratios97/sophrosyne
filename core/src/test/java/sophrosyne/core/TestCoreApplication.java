package sophrosyne.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.TestPropertySource;

@TestConfiguration(proxyBeanMethods = false)
@SpringBootTest
@TestPropertySource(locations = "/application-test.properties")
public class TestCoreApplication {

  public static void main(String[] args) {
    SpringApplication.from(CoreApplication::main).with(TestCoreApplication.class).run(args);
  }
}
