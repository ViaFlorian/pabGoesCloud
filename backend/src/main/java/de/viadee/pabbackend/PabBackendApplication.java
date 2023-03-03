package de.viadee.pabbackend;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PabBackendApplication {

  @Value("${timezone.set.utc}")
  private boolean setTimezone;

  @PostConstruct
  private void init() {
    if (setTimezone) {
      TimeZone.setDefault(TimeZone.getTimeZone("UTC"));   // It will set UTC timezone
    }
  }

  public static void main(String[] args) {
    SpringApplication.run(PabBackendApplication.class, args);
  }

}
