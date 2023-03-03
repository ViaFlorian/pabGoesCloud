package de.viadee.pabbackend.enums;

import java.util.HashMap;
import java.util.Map;

public enum ProjektstundeTyp {
  NORMAL("Normal"), SONDERARBEITSZEIT("Sonder"), RUFBEREITSCHAFT("Ruf"), TATSAECHLICHE_REISEZEIT(
      "tat_Reise"), ANGERECHNETE_REISEZEIT("ang_Reise");

  private static Map<Object, Object> map = new HashMap<>();

  static {
    for (final ProjektstundeTyp projektstundenTyp : ProjektstundeTyp.values()) {
      map.put(projektstundenTyp.typ, projektstundenTyp);
    }
  }

  private String typ;

  ProjektstundeTyp(final String typ) {
    this.typ = typ;
  }

  public static ProjektstundeTyp valueOf(final Long typ) {
    return (ProjektstundeTyp) map.get(typ);
  }

  public String getValue() {
    return typ;
  }

}
