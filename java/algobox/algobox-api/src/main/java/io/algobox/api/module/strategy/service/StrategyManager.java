package io.algobox.api.module.strategy.service;

import io.algobox.strategy.Strategy;

public interface StrategyManager {
  Class<? extends Strategy> getStrategyById(String strategyId);
}
