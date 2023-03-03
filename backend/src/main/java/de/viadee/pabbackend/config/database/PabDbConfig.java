package de.viadee.pabbackend.config.database;

import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jdbc.core.convert.BasicJdbcConverter;
import org.springframework.data.jdbc.core.convert.DefaultJdbcTypeFactory;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;
import org.springframework.data.jdbc.core.convert.RelationResolver;
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.dialect.SqlServerDialect;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJdbcRepositories(
    basePackages = "de.viadee.pabbackend.repositories.pabdb",
    transactionManagerRef = "pabDbTransactionManager",
    jdbcOperationsRef = "pabDbJdbcOperations"
)
@EnableAutoConfiguration(exclude = {
    DataSourceAutoConfiguration.class,
    JdbcRepositoriesAutoConfiguration.class
})
@EnableCaching
public class PabDbConfig {

  @Bean
  @ConfigurationProperties(prefix = "spring.pabdb-datasource")
  DataSource pabDbDataSource() {
    return DataSourceBuilder.create().build();
  }

  @Bean(name = "pabDbTransactionManager")
  PlatformTransactionManager pabDbTransactionManager(
      @Qualifier("pabDbDataSource") DataSource pabDbDataSource) {
    return new JdbcTransactionManager(pabDbDataSource);
  }

  @Bean
  NamedParameterJdbcOperations pabDbJdbcOperations(
      @Qualifier("pabDbDataSource") DataSource dataSource) {
    return new NamedParameterJdbcTemplate(dataSource);
  }

  @Bean
  Dialect jdbcDialect() {
    return SqlServerDialect.INSTANCE;
  }

  @Bean
  JdbcCustomConversions customConversions() {
    return new JdbcCustomConversions();
  }

  @Bean
  JdbcMappingContext jdbcMappingContext(Optional<NamingStrategy> namingStrategy,
      JdbcCustomConversions customConversions) {
    JdbcMappingContext mappingContext = new JdbcMappingContext(
        (NamingStrategy) namingStrategy.orElse(NamingStrategy.INSTANCE));
    mappingContext.setSimpleTypeHolder(customConversions.getSimpleTypeHolder());
    return mappingContext;
  }

  @Bean
  JdbcConverter jdbcConverter(JdbcMappingContext mappingContext,
      @Qualifier("pabDbJdbcOperations") NamedParameterJdbcOperations jdbcOperationsDataBase1,
      @Lazy RelationResolver relationResolver,
      JdbcCustomConversions conversions,
      Dialect dialect) {

    DefaultJdbcTypeFactory jdbcTypeFactory = new DefaultJdbcTypeFactory(
        jdbcOperationsDataBase1.getJdbcOperations());
    return new BasicJdbcConverter(mappingContext, relationResolver, conversions, jdbcTypeFactory,
        dialect.getIdentifierProcessing());
  }

}

