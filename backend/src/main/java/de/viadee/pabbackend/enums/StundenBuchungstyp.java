package de.viadee.pabbackend.enums;

import java.util.Arrays;
import java.util.List;

public enum StundenBuchungstyp {

  UEBERTRAG("Übertrag"), SOLLSTUNDEN("Sollstunden"), IST_STUNDEN("Ist-Stunden"), AUSZAHLUNG(
      "Auszahlung"), AUSZAHLUNG_SONDERARBEITSZEIT("Auszahlung Sonderarbeitszeit");

  private final String text;

  StundenBuchungstyp(final String text) {
    this.text = text;
  }

  public static List<String> getValuesAsString() {
    return Arrays.stream(values()).map(StundenBuchungstyp::toString).
        toList();
  }

  public static List<StundenBuchungstyp> getValues() {
    return Arrays.asList(values());
  }

  @Override
  public String toString() {
    return text;
  }

}
