package io.algobox.backtest;

import com.google.common.collect.Lists;
import io.algobox.price.PriceTick;
import io.algobox.strategy.StrategyEvent;
import io.algobox.strategy.StrategyEventDto;
import io.algobox.strategy.StrategyEventService;
import io.algobox.strategy.StrategyEventType;

import java.util.Collection;
import java.util.List;

public final class ExperimentStrategyEventService implements StrategyEventService {
  private List<StrategyEvent> allStrategyEvents = Lists.newLinkedList();

  @Override
  public void logEventAsync(String strategyInstanceId, StrategyEventType strategyEventType,
      PriceTick priceTick, String message, String data) {
    StrategyEvent strategyEvent = new StrategyEventDto(priceTick.getTime(), strategyEventType,
        priceTick, message, data);
    allStrategyEvents.add(strategyEvent);
  }

  @Override
  public Collection<StrategyEvent> getInstanceEventsLog(
      String instanceId, int pageNumber, int pageSize) {
    throw new IllegalArgumentException("Not yet implemented");
  }

  public Collection<StrategyEvent> getAllStrategyEvents() {
    List<StrategyEvent> result = Lists.newArrayListWithExpectedSize(allStrategyEvents.size());
    result.addAll(allStrategyEvents);
    return result;
  }
}
