package io.algobox.connector.oanada.domain;

public enum OandaOrderState {
  PENDING("PENDING"),
  FILLED("FILLED"),
  TRIGGERED("TRIGGERED"),
  CANCELLED("CANCELLED");

  private final String value;

  OandaOrderState(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return value;
  }
}
