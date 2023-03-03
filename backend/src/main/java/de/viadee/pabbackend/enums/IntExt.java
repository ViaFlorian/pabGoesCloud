package de.viadee.pabbackend.enums;

import java.util.Arrays;
import java.util.List;

public enum IntExt {

  NUR_INTERNE("nur interne Mitarbeiter/innen"), NUR_STUDENTEN_PRAKTIKANTEN(
      "nur Studenten/Praktikanten"), NUR_EXTERNE(
      "nur externe Mitarbeiter/innen"), INTERN(
      "Intern"), EXTERN("Extern");

  private final String text;

  IntExt(final String text) {
    this.text = text;
  }

  public static List<String> getValuesAsString() {
    return Arrays.asList(values()).stream().map(value -> value.toString()).
        toList();
  }

  public static List<IntExt> getValues() {
    return Arrays.asList(values());
  }

  @Override
  public String toString() {
    return text;
  }
}
