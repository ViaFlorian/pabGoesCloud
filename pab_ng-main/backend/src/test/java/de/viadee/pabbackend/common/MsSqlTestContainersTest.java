package de.viadee.pabbackend.common;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@ActiveProfiles("test")
public class MsSqlTestContainersTest {

  static DockerImageName sqlServerImage =
      DockerImageName.parse("mcr.microsoft.com/azure-sql-edge")
          .withTag("latest")
          .asCompatibleSubstituteFor("mcr.microsoft.com/mssql/server");

  @Container static MSSQLServerContainer mssqlserver = new MSSQLServerContainer(sqlServerImage);

  @DynamicPropertySource
  static void mssqlServerProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.pabdb-datasource.jdbc-url", mssqlserver::getJdbcUrl);
    registry.add("spring.pabdb-datasource.username", mssqlserver::getUsername);
    registry.add("spring.pabdb-datasource.password", mssqlserver::getPassword);
    registry.add("spring.flyway.url", mssqlserver::getJdbcUrl);
    registry.add("spring.flyway.password", mssqlserver::getPassword);
    registry.add("spring.flyway.user", mssqlserver::getUsername);
  }
}
