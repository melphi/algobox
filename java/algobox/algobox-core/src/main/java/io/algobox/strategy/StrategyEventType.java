package io.algobox.strategy;

public enum StrategyEventType {
  PATTERN("PATTERN"),
  ORDER_SENT("ORDER_SENT"),
  ERROR("ERROR");

  private final String value;

  StrategyEventType(String value) {
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
