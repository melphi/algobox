package io.algobox.api.module.strategy.dao.impl;

import io.algobox.api.module.strategy.domain.StrategyHistory;
import io.algobox.api.module.strategy.domain.StrategyRegistration;

import java.util.Optional;

public interface StrategyHistoryDao {
  void save(StrategyRegistration strategyRegistration, long timestampUtc,
      Optional<Throwable> exception, long receivedTicks);

  Iterable<? extends StrategyHistory> findAll(int pageNumber, int pageSize);
}
