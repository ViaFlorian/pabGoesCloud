package de.viadee.pabbackend.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public enum ProjektabrechnungBearbeitungsstatus {

  ALLE("Alle"), ERFASST("erfasst"), ABGERECHNET("abgerechnet"), ABGESCHLOSSEN("abgeschlossen");

  private final String text;

  ProjektabrechnungBearbeitungsstatus(final String text) {
    this.text = text;
  }

  public static List<String> getValuesAsString() {
    return Arrays.asList(values()).stream().map(value -> value.toString()).
        toList();
  }

  public static List<ProjektabrechnungBearbeitungsstatus> getValues() {
    return Arrays.asList(values());
  }

  public static Stream<ProjektabrechnungBearbeitungsstatus> stream() {
    return Stream.of(ProjektabrechnungBearbeitungsstatus.values());
  }

  public static ProjektabrechnungBearbeitungsstatus fromStatusId(int statusID) {
    return switch (statusID) {
      case 10 -> ProjektabrechnungBearbeitungsstatus.ERFASST;
      case 40 -> ProjektabrechnungBearbeitungsstatus.ABGERECHNET;
      case 50 -> ProjektabrechnungBearbeitungsstatus.ABGESCHLOSSEN;
      default -> null;
    };
  }

  @Override
  public String toString() {
    return text;
  }

  public int toStatusId() {
    int statusId = 0;

    switch (toString()) {
      case "erfasst":
        statusId = 10;
        break;
      case "abgerechnet":
        statusId = 40;
        break;
      case "abgeschlossen":
        statusId = 50;
        break;
      default:
        statusId = 0;
    }

    return statusId;
  }

}
