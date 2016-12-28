package io.algobox.api.module.strategy.domain;

import io.algobox.strategy.StrategyStatus;

import java.io.Serializable;

public interface StrategyInfo extends Serializable {
  StrategyStatus getStatus();

  long getReceivedTicks();
}
