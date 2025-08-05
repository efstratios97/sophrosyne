package sophrosyne.core;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest()
@TestPropertySource(locations = "/application-test.properties")
public abstract class PostgresIntegrationTestBase {

  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>(DockerImageName.parse("postgres:16.1"))
          .withDatabaseName("sophrosyne_db")
          .withUsername("sophrosyne")
          .withPassword("sophrosyne")
          .withExposedPorts(5432);

  @DynamicPropertySource
  static void postgresProperties(DynamicPropertyRegistry registry) {
    postgres.start();
    registry.add("spring.liquibase.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
  }
}
