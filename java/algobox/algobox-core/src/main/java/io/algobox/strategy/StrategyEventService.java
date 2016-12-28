package io.algobox.strategy;

import io.algobox.price.PriceTick;

import java.util.Collection;

public interface StrategyEventService {
  void logEventAsync(String strategyInstanceId, StrategyEventType strategyEventType,
      PriceTick priceTick, String message, String data);

  Collection<StrategyEvent> getInstanceEventsLog(String instanceId, int pageNumber, int pageSize);
}
