package io.algobox.api.module.strategy.dao.impl;

import io.algobox.strategy.StrategyEvent;

import java.util.Collection;

public interface StrategyEventDao {
  void logEvent(String instanceId, StrategyEvent strategyEvent);

  Collection<StrategyEvent> findEventsLog(String instanceId, int pageNumber, int pageSize);
}
