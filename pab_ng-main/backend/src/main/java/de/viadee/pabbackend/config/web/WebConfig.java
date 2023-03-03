package de.viadee.pabbackend.config.web;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import de.viadee.pabbackend.config.constants.BackendPfade;
import de.viadee.pabbackend.config.constants.ErrorAttributes;
import de.viadee.pabbackend.config.constants.HeaderKonstanten;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Value("${security.cors.header.active:false}")
  private boolean corsHeaderActive;

  @Override
  public void addCorsMappings(final CorsRegistry registry) {
    if (corsHeaderActive) {
      registry.addMapping(BackendPfade.BASE_URL + "/**")
          .allowedHeaders(HeaderKonstanten.AUTHORIZATION, HeaderKonstanten.CONTENT_TYPE)
          .exposedHeaders(HeaderKonstanten.RETRY_AFTER, HeaderKonstanten.AUTHORIZATION)
          .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
  }

  @Override
  public void addViewControllers(final ViewControllerRegistry registry) {
    registry.addViewController("{path:(?:(?!api|.).)*}/**").setViewName("forward:/");
  }

  @Bean
  public ErrorAttributes errorAttributes() {
    return new ErrorAttributes();
  }

  @Bean
  public CommonsRequestLoggingFilter logFilter() {
    final CommonsRequestLoggingFilter filter
        = new CommonsRequestLoggingFilter();
    filter.setIncludeQueryString(true);
    filter.setIncludePayload(true);
    filter.setMaxPayloadLength(10000);
    filter.setIncludeHeaders(false);
    filter.setAfterMessagePrefix("REQUEST DATA : ");
    return filter;
  }

  @Bean
  public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
    final DateTimeFormatter timeFormatter = new DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .append(ISO_LOCAL_DATE_TIME)
        .appendLiteral('Z')
        .parseStrict()
        .toFormatter();

    final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .append(ISO_LOCAL_DATE)
        .parseStrict()
        .toFormatter();

    return builder -> {
      builder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(timeFormatter));
      builder.serializerByType(LocalDate.class, new LocalDateSerializer(formatter));
      builder.deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(timeFormatter));
      builder.deserializerByType(LocalDate.class, new LocalDateDeserializer(formatter));
      builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    };
  }

}
