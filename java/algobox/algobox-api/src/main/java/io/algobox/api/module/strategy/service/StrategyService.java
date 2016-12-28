package io.algobox.api.module.strategy.service;

import io.algobox.api.module.strategy.domain.StrategyHistory;
import io.algobox.api.module.strategy.domain.StrategyInfo;
import io.algobox.api.module.strategy.domain.StrategyRegistration;
import io.algobox.api.module.strategy.domain.dto.StrategyRegistrationRequestDto;
import io.algobox.price.SourcedPriceTickListener;

import java.util.Collection;

public interface StrategyService extends SourcedPriceTickListener {
  Collection<StrategyRegistration> getActiveInstances();

  Collection<StrategyHistory> getInstancesHistory(int pageNumber, int pageSize);

  /**
   * Returns the instance id of the registered dummy.
   */
  String createInstance(StrategyRegistrationRequestDto strategyCreationRequest);

  void removeInstance(String instanceId);

  StrategyInfo getInstanceStatus(String instanceId);
}
