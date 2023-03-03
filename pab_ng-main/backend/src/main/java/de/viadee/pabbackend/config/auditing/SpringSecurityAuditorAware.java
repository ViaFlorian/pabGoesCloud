package de.viadee.pabbackend.config.auditing;

import java.util.Optional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

@Configuration
@EnableJdbcAuditing
@Profile("!test")
class SpringSecurityAuditorAware implements AuditorAware<String> {

  @Override
  public Optional<String> getCurrentAuditor() {
    String username = "unknown";
    Authentication authentication
        = SecurityContextHolder.getContext().getAuthentication();
    if (authentication.isAuthenticated()) {
      Jwt token = (Jwt) authentication.getPrincipal();
      username = token.getClaimAsString("unique_name");
    }
    return Optional.ofNullable(username);
  }
}
