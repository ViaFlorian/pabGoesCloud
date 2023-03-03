package de.viadee.pabbackend.config.constants;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.web.context.request.WebRequest;

public class ErrorAttributes extends DefaultErrorAttributes {

  private static final String[] KEYS = {"timestamp", "status", "error", "path"};

  public ErrorAttributes() {
    super();
  }

  @Override
  public Map<String, Object> getErrorAttributes(final WebRequest webRequest,
      final ErrorAttributeOptions options) {
    final Map<String, Object> originalErrorAttributes = super.getErrorAttributes(webRequest,
        options);

    final Map<String, Object> errorAttributes = new LinkedHashMap<>();
    for (final String key : KEYS) {
      final Object value = originalErrorAttributes.get(key);
      errorAttributes.put(key, value);
    }

    // Attribute Message is needed by the Spring Whitelabel Error Page
    errorAttributes.put("message", "");

    return errorAttributes;
  }

}
