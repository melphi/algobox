package io.algobox.order;

public enum OrderStatus {
  CANCELLED("CANCELLED");

  private final String value;

  OrderStatus(String value) {
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
