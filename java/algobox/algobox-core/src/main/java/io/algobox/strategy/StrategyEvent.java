package io.algobox.strategy;

import io.algobox.price.PriceTick;

import java.io.Serializable;

public interface StrategyEvent extends Serializable {
  long getTimestamp();

  StrategyEventType getStrategyEventType();

  PriceTick getPriceTick();

  String getMessage();

  String getData();
}
