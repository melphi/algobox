package io.algobox.api.module.strategy.dao.impl;

import io.algobox.api.module.strategy.domain.StrategyHistory;
import io.algobox.api.module.strategy.domain.StrategyRegistration;
import io.algobox.api.module.strategy.domain.mdb.StrategyHistoryMdb;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class TestingStrategyHistoryDao extends AbstractTestingDao<StrategyHistory>
    implements StrategyHistoryDao {
  @Override
  public void save(StrategyRegistration strategyRegistration, long timestampUtc,
      Optional<Throwable> exception, long receivedTicks) {
    StrategyHistory strategyHistory = new StrategyHistoryMdb(
        strategyRegistration, timestampUtc, exception.orElse(null), receivedTicks);
    internalSaveValue(UUID.randomUUID().toString(), strategyHistory);
  }

  @Override
  public Iterable<? extends StrategyHistory> findAll(int pageNumber, int pageSize) {
    return internalGetAllValues().stream()
        .skip(pageNumber * pageSize)
        .limit(pageSize)
        .collect(Collectors.toList());
  }
}
