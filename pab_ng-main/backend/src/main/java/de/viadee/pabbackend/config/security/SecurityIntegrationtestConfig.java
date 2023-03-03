package de.viadee.pabbackend.config.security;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration()
@EnableWebSecurity
@EnableMethodSecurity
@Profile("integrationtest")
public class SecurityIntegrationtestConfig {

  @Value("${spring.profiles.active:Unknown}")
  private String activeProfile;

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf().disable();

    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER);
    http.cors()
        .and()
        .authorizeHttpRequests()
        .anyRequest().permitAll();

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    final CorsConfiguration config = new CorsConfiguration();

    config.setAllowedOrigins(List.of("http://localhost:4200"));
    config.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS", "DELETE", "PUT", "PATCH"));
    config.setAllowCredentials(true);
    config.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));

    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);

    return source;
  }
}
