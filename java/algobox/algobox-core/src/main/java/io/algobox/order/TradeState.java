package io.algobox.order;

public enum TradeState {
  OPEN("OPEN"),
  CLOSED("CLOSED");

  private String value;

  TradeState(String value) {
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
