package io.algobox.connector.oanada.domain;

public enum OandaTradeState {
  OPEN("OPEN"),
  CLOSED("CLOSED"),
  CLOSE_WHEN_TRADEABLE("CLOSE_WHEN_TRADEABLE");

  private final String value;

  OandaTradeState(String value) {
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
