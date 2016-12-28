package io.algobox.api.module.strategy.dao.impl;

import io.algobox.api.module.strategy.domain.StrategyRegistration;

public interface StrategyRegistrationDao {
  void deleteById(String instanceId);

  void save(StrategyRegistration strategyRegistration);

  boolean exists(String instanceId);

  Iterable<? extends StrategyRegistration> findAll();

  StrategyRegistration findByInstanceId(String instanceId);

  Iterable<? extends StrategyRegistration> findByStrategyId(String strategyId);
}
