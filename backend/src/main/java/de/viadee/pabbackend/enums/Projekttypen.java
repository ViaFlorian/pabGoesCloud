package de.viadee.pabbackend.enums;

public enum Projekttypen {
  ALLE("Alle"),
  DIENSTLEISTUNG("Dienstleistung"),
  FESTPREIS("Festpreis"),
  INTERN("intern"),
  WARTUNG("Wartung"),
  PRODUKT("Produkt");

  private final String text;

  Projekttypen(final String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return text;
  }
}
