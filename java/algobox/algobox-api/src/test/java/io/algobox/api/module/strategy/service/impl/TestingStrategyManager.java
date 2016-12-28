package io.algobox.api.module.strategy.service.impl;

import io.algobox.api.module.strategy.service.StrategyManager;
import io.algobox.strategy.Strategy;
import io.algobox.strategy.dummy.DummyStrategy;

import static avro.shaded.com.google.common.base.Preconditions.checkArgument;

public class TestingStrategyManager implements StrategyManager {
  @Override
  public Class<? extends Strategy> getStrategyById(String strategyId) {
    checkArgument(DummyStrategy.STRATEGY_ID.equals(strategyId));
    return DummyStrategy.class;
  }
}
