package io.algobox.strategy;

import com.google.common.base.MoreObjects;
import io.algobox.price.PriceTick;

public final class StrategyEventDto implements StrategyEvent {
  private long timestamp;

  private StrategyEventType strategyEventType;

  private PriceTick priceTick;

  private String message;

  private String data;

  public StrategyEventDto() {
    // Intentionally empty.
  }

  public StrategyEventDto(long timestamp, StrategyEventType strategyEventType, PriceTick priceTick,
      String message, String data) {
    this.timestamp = timestamp;
    this.strategyEventType = strategyEventType;
    this.priceTick = priceTick;
    this.message = message;
    this.data = data;
  }

  @Override
  public StrategyEventType getStrategyEventType() {
    return strategyEventType;
  }

  @Override
  public PriceTick getPriceTick() {
    return priceTick;
  }

  @Override
  public String getMessage() {
    return message;
  }

  @Override
  public String getData() {
    return data;
  }

  @Override
  public long getTimestamp() {
    return timestamp;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("timestamp", timestamp)
        .add("strategyEventType", strategyEventType)
        .add("priceTick", priceTick)
        .add("message", message)
        .add("data", data)
        .toString();
  }
}
