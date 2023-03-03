package de.viadee.pabbackend.enums;

import java.util.Arrays;
import java.util.List;

public enum Kostenarten {
  PROJEKTZEITEN("Projektzeiten"), REISEZEITEN("Reisezeiten"), REISEKOSTEN(
      "Reisekosten"), SONDERARBEITSZEITEN("Sonderarbeitszeiten"), RUFBEREITSCHAFTEN(
      "Rufbereitschaften"), SONSTIGE("Sonstige"), SKONTO("Skonto"), RABATTE(
      "Rabatte"), FAKTUFAEHIGE_LEISTUNG("FaktfaehigeLeistung");

  private final String text;

  Kostenarten(final String text) {
    this.text = text;
  }

  public static List<String> getValuesAsString() {
    return Arrays.stream(values()).map(Kostenarten::toString).
        toList();
  }

  public static List<Kostenarten> getValues() {
    return Arrays.asList(values());
  }

  @Override
  public String toString() {
    return text;
  }
}
