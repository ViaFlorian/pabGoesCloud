package de.viadee.pabbackend.enums;

public enum Projektnummern {
  URLAUB("9004");

  private final String projektnummer;

  Projektnummern(final String projektnummer) {
    this.projektnummer = projektnummer;
  }

  @Override
  public String toString() {
    return projektnummer;
  }
}
