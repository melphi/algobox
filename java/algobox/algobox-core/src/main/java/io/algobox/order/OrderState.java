package io.algobox.order;

public enum OrderState {
  PENDING("PENDING"),
  FILLED("FILLED"),
  CANCELLED("CANCELLED");

  private String value;

  OrderState(String value) {
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
