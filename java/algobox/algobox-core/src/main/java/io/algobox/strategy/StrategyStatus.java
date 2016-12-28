package io.algobox.strategy;

public enum StrategyStatus {
  UNDEFINED(0),
  WAITING_FOR_MARKET(1),
  PROCESSING(2),
  PATTERN_FOUND(3);

  private final int value;

  StrategyStatus(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}
