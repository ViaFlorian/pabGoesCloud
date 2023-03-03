package de.viadee.pabbackend.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ViadeeAuslagenKostenarten {
  REISE("Reise"), SONSTIGES("Sonstiges");

  private final String text;

  ViadeeAuslagenKostenarten(final String text) {
    this.text = text;
  }

  public static List<String> getValuesAsString() {
    return Arrays.asList(values()).stream().map(value -> value.toString())
        .collect(Collectors.toList());
  }

  public static List<ViadeeAuslagenKostenarten> getValues() {
    return List.of(values());
  }

  public static Stream<ViadeeAuslagenKostenarten> stream() {
    return Stream.of(ViadeeAuslagenKostenarten.values());
  }

  @Override
  public String toString() {
    return text;
  }

  public int toId() {
    int id;

    switch (toString()) {
      case "Reise":
        id = 1;
        break;
      case "Sonstiges":
        id = 2;
        break;

      default:
        id = 1;
    }

    return id;
  }
}
