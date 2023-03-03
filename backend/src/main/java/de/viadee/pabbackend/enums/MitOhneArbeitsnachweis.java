package de.viadee.pabbackend.enums;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public enum MitOhneArbeitsnachweis {
  NUR_VORHANDENE("vorhandene Arbeitsnachweise"), NUR_OFFENE("offene Arbeitsnachweise");

  private final String text;

  MitOhneArbeitsnachweis(final String text) {
    this.text = text;
  }

  public static List<String> getValuesAsString() {
    return Arrays.asList(values()).stream().map(value -> value.toString()).
        toList();
  }

  public static Collection<MitOhneArbeitsnachweis> getValues() {
    return Arrays.asList(values());
  }

  @Override
  public String toString() {
    return text;
  }

}
