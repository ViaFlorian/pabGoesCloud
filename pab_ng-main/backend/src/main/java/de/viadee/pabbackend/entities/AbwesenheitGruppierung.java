package de.viadee.pabbackend.entities;

public class AbwesenheitGruppierung {

  private String einsatzort;

  private String kundeID;

  private Integer kalenderwoche;

  private Integer jahr;

  public AbwesenheitGruppierung(final String einsatzort, final String kundeID,
      final Integer kalenderwoche,
      final Integer jahr) {
    this.einsatzort = einsatzort;
    this.kundeID = kundeID;
    this.kalenderwoche = kalenderwoche;
    this.jahr = jahr;
  }

  public String getEinsatzort() {
    return einsatzort;
  }

  public void setEinsatzort(final String einsatzort) {
    this.einsatzort = einsatzort;
  }

  public String getKundeID() {
    return kundeID;
  }

  public void setKundeID(final String kundeID) {
    this.kundeID = kundeID;
  }

  public Integer getKalenderwoche() {
    return kalenderwoche;
  }

  public void setKalenderwoche(final Integer kalenderwoche) {
    this.kalenderwoche = kalenderwoche;
  }

  public Integer getJahr() {
    return jahr;
  }

  public void setJahr(final Integer jahr) {
    this.jahr = jahr;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((einsatzort == null) ? 0 : einsatzort.hashCode());
    result = prime * result + ((jahr == null) ? 0 : jahr.hashCode());
    result = prime * result + ((kalenderwoche == null) ? 0 : kalenderwoche.hashCode());
    result = prime * result + ((kundeID == null) ? 0 : kundeID.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final AbwesenheitGruppierung other = (AbwesenheitGruppierung) obj;
    if (einsatzort == null) {
      if (other.einsatzort != null) {
        return false;
      }
    } else if (!einsatzort.equals(other.einsatzort)) {
      return false;
    }
    if (jahr == null) {
      if (other.jahr != null) {
        return false;
      }
    } else if (!jahr.equals(other.jahr)) {
      return false;
    }
    if (kalenderwoche == null) {
      if (other.kalenderwoche != null) {
        return false;
      }
    } else if (!kalenderwoche.equals(other.kalenderwoche)) {
      return false;
    }
    if (kundeID == null) {
      return other.kundeID == null;
    } else {
      return kundeID.equals(other.kundeID);
    }
  }

}
