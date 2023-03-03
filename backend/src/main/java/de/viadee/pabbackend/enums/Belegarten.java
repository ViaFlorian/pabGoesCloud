package de.viadee.pabbackend.enums;

import java.util.Arrays;
import java.util.List;

public enum Belegarten {
  PKW("PKW"), BAHN("Bahn"), FLUG("Flug"), OPNV("ÖPNV"), TAXI("Taxi"), HOTEL("Hotel"), PARKEN(
      "Parken"), HANDY(
      "Handy"), SONSTIGES("Sonstiges"), JOBTICKET("Jobticket"), VERBINDUNGSENTGELT("Verbindungsentgelt");

  private final String text;

  Belegarten(final String text) {
    this.text = text;
  }

  public static List<String> getValuesAsString() {
    return Arrays.asList(values()).stream().map(value -> value.toString()).
        toList();
  }

  public static List<Belegarten> getValues() {
    return Arrays.asList(values());
  }

  @Override
  public String toString() {
    return text;
  }

}
