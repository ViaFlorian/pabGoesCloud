package de.viadee.pabbackend.entities;

public class DmsVerarbeitung {

  private boolean fehlerhaft;
  private String message;

  public boolean isFehlerhaft() {
    return fehlerhaft;
  }

  public void setFehlerhaft(boolean fehlerhaft) {
    this.fehlerhaft = fehlerhaft;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}