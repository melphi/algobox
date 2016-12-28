package io.algobox.order;

public enum OrderType {
  MARKET("MARKET"),
  LIMIT("LIMIT"),
  STOP("STOP"),
  TAKE_PROFIT("TAKE_PROFIT"),
  STOP_LOSS("STOP_LOSS");

  private String value;

  OrderType(String value) {
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
