package de.viadee.pabbackend.config.database;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJdbcRepositories(
    basePackages = "de.viadee.pabbackend.repositories.pabbi",
    transactionManagerRef = "pabBiTransactionManager",
    jdbcOperationsRef = "pabBiJdbcOperations"
)
@EnableAutoConfiguration(exclude = {
    DataSourceAutoConfiguration.class,
    JdbcRepositoriesAutoConfiguration.class
})
public class PabBiConfig {

  @Bean
  @ConfigurationProperties(prefix = "spring.pabbi-datasource")
  DataSource pabBiDataSource() {
    return DataSourceBuilder.create().build();
  }

  @Bean(name = "pabBiTransactionManager")
  PlatformTransactionManager pabBiTransactionManager(
      @Qualifier("pabBiDataSource") DataSource pabBiDataSource) {
    return new JdbcTransactionManager(pabBiDataSource);
  }

  @Bean
  NamedParameterJdbcOperations pabBiJdbcOperations(
      @Qualifier("pabBiDataSource") DataSource pabBiDataSource) {
    return new NamedParameterJdbcTemplate(pabBiDataSource);
  }

}