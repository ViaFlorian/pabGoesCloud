package de.viadee.pabbackend.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public enum SmartphoneBesitzKenner {

  EIGEN(true), FIRMA(false), KEIN(null);

  private final Boolean eigen;

  SmartphoneBesitzKenner(final Boolean eigen) {
    this.eigen = eigen;
  }

  public static List<String> getValuesAsString() {
    return Arrays.asList(values()).stream().map(value -> value.toString()).
        toList();
  }

  @Override
  public String toString() {
    if (Objects.nonNull(eigen) && eigen.equals(Boolean.TRUE)) {
      return "Eigen";
    } else if (Objects.nonNull(eigen) && eigen.equals(Boolean.FALSE)) {
      return "Firma";
    }
    return "kein";
  }

  public Boolean value() {
    return eigen;
  }

}
