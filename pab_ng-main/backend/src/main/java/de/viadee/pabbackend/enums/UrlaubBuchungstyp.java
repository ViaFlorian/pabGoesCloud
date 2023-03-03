package de.viadee.pabbackend.enums;

import java.util.Arrays;
import java.util.List;

public enum UrlaubBuchungstyp {

  WERTKONTO("Wertkonto"), GENOMMEN("Genommen"), ANSPRUCH("Anspruch"), ÜBERTRAG("Übertrag");

  private final String text;

  UrlaubBuchungstyp(final String text) {
    this.text = text;
  }

  public static List<String> getValuesAsString() {
    return Arrays.stream(values()).map(UrlaubBuchungstyp::toString).
        toList();
  }

  public static List<UrlaubBuchungstyp> getValues() {
    return Arrays.asList(values());
  }

  @Override
  public String toString() {
    return text;
  }

}
