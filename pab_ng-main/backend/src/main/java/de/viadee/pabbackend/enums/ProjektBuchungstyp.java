package de.viadee.pabbackend.enums;

import java.util.Arrays;
import java.util.List;

public enum ProjektBuchungstyp {
  ALLE("Alle"), BUCHUNG("Buchung"), KORREKTUR("Korrektur");

  private final String text;

  ProjektBuchungstyp(final String text) {
    this.text = text;
  }

  public static List<String> getValuesAsString() {
    return Arrays.asList(values()).stream().map(value -> value.toString()).
        toList();
  }

  public static List<ProjektBuchungstyp> getValues() {
    return Arrays.asList(values());
  }

  @Override
  public String toString() {
    return text;
  }

}
