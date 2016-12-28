package io.algobox.api.module.strategy.domain.mdb;

import io.algobox.strategy.StrategyEvent;
import io.algobox.strategy.StrategyEventType;
import io.algobox.price.PriceTick;

public final class StrategyEventMdb implements StrategyEvent {
  public static final String COLLECTION_STRATEGY_EVENTS = "strategyEvents";
  public static final String FIELD_STRATEGY_INSTANCE_ID = "strategyInstanceId";
  public static final String FIELD_TIMESTAMP = "timestamp";

  private String strategyInstanceId;

  private long timestamp;

  private StrategyEventType strategyEventType;

  private PriceTick priceTick;

  private String message;

  private String data;

  public StrategyEventMdb(String strategyInstanceId, StrategyEvent strategyEvent) {
    this.strategyInstanceId = strategyInstanceId;
    this.timestamp = strategyEvent.getTimestamp();
    this.strategyEventType = strategyEvent.getStrategyEventType();
    this.priceTick = strategyEvent.getPriceTick();
    this.message = strategyEvent.getMessage();
    this.data = strategyEvent.getData();
  }

  @Override
  public StrategyEventType getStrategyEventType() {
    return strategyEventType;
  }

  public void setStrategyEventType(StrategyEventType strategyEventType) {
    this.strategyEventType = strategyEventType;
  }

  @Override
  public PriceTick getPriceTick() {
    return priceTick;
  }

  public void setPriceTick(PriceTick priceTick) {
    this.priceTick = priceTick;
  }

  @Override
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public String getStrategyInstanceId() {
    return strategyInstanceId;
  }

  public void setStrategyInstanceId(String strategyInstanceId) {
    this.strategyInstanceId = strategyInstanceId;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }
}
