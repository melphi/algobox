package io.algobox.strategy;

import io.algobox.price.PriceTick;

/**
 * Strategy interface. By convention a Strategy class:
 * - should be named with the suffix Strategy
 * - should contain a public static final String STRATEGY_ID property.
 */
public interface Strategy {
  String getStrategyId();

  StrategyContext getStrategyContext();

  void onPriceTick(PriceTick priceTick);
}
