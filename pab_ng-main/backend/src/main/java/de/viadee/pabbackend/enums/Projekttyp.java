package de.viadee.pabbackend.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public enum Projekttyp {

  ALLE("Alle"), DIENSTLEISTUNG("Dienstleistung"), FESTPREIS("Festpreis"), INTERN("intern"), WARTUNG(
      "Wartung"), PRODUKT("Produkt");

  private final String text;

  Projekttyp(final String text) {
    this.text = text;
  }

  public static List<String> getValuesAsString() {
    return Arrays.asList(values()).stream().map(value -> value.toString()).
        toList();
  }

  public static List<Projekttyp> getValues() {
    return Arrays.asList(values());
  }

  public static Stream<Projekttyp> stream() {
    return Stream.of(Projekttyp.values());
  }

  @Override
  public String toString() {
    return text;
  }

}
