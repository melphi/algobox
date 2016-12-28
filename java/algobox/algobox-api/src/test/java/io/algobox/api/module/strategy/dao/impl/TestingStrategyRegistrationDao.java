package io.algobox.api.module.strategy.dao.impl;

import io.algobox.api.module.strategy.domain.StrategyRegistration;

import java.util.stream.Collectors;

import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;

public class TestingStrategyRegistrationDao extends AbstractTestingDao<StrategyRegistration>
    implements StrategyRegistrationDao {
  @Override
  public void deleteById(String instanceId) {
    internalDelete(instanceId);
  }

  @Override
  public void save(StrategyRegistration strategyRegistration) {
    internalSaveValue(strategyRegistration.getInstanceId(), strategyRegistration);
  }

  @Override
  public boolean exists(String instanceId) {
    return internalExists(instanceId);
  }

  @Override
  public Iterable<? extends StrategyRegistration> findAll() {
    return internalGetAllValues();
  }

  @Override
  public StrategyRegistration findByInstanceId(String instanceId) {
    return internalGetValue(instanceId);
  }

  @Override
  public Iterable<? extends StrategyRegistration> findByStrategyId(String strategyId) {
    checkNotNullOrEmpty(strategyId);
    return internalGetAllValues().stream()
        .filter(item -> strategyId.equals(item.getStrategyId()))
        .collect(Collectors.toList());
  }
}
