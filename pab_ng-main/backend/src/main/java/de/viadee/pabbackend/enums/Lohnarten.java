package de.viadee.pabbackend.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Lohnarten {
  ZUSCHLAG_UEBERSTUNDEN_50_PROZENT_FREI("008"),
  ZUSCHLAG_UEBERSTUNDEN_50_PROZENT_PFL("009"),
  UEBERSTUNDEN_GRUNDVERGUETUNG("012"),
  ZUSCHLAG_UEBERSTUNDEN_25_PROZENT_FREI("014"),
  ZUSCHLAG_UEBERSTUNDEN_25_PROZENT_PFL("016"),
  RUFBEREITSCHAFT("032"),
  SACHBEZUG_FRUEHSTUECK("053"),
  JOBTICKET("9057"),
  VERPFLEGUNG_MEHRAUFWAND_FREI("9979"),
  VERPFLEGUNG_MEHRAUFWAND_25_PROZENT("079"),
  VERPFLEGUNG_MEHRAUFWAND_PFL("080"),
  DIENSTF_UND_NEBENKOSTEN_FREI("9978"),
  HANDY_ERSTATTUNG_FREI("091"),
  HANDY_ERSTATTUNG_PFL("092"),
  ZUSCHLAG_UEBERSTUNDEN_100_PROZENT_FREI("107");

  private static final Map<String, Lohnarten> kontoLohnarten = new HashMap<>();

  static {
    for (final Lohnarten lohnart : Lohnarten.values()) {
      kontoLohnarten.put(lohnart.getKonto(), lohnart);
    }
  }

  private final String konto;

  Lohnarten(final String konto) {
    this.konto = konto;
  }

  public static List<String> getValuesAsString() {
    return Arrays.asList(values()).stream().map(value -> value.toString()).
        toList();
  }

  public static List<Lohnarten> getValues() {
    return Arrays.asList(values());
  }

  public static Lohnarten get(final String konto) {
    return kontoLohnarten.get(konto);
  }

  public String getKonto() {
    return konto;
  }

  public String getLegacyKontonummer() {
    switch (this) {
      case VERPFLEGUNG_MEHRAUFWAND_FREI:
        return "078";
      case DIENSTF_UND_NEBENKOSTEN_FREI:
        return "081";
      default:
        return null;
    }
  }

}
