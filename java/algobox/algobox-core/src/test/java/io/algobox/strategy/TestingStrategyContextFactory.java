package io.algobox.strategy;

import com.google.common.collect.ImmutableMap;
import io.algobox.indicator.IndicatorService;
import io.algobox.instrument.InstrumentService;
import io.algobox.order.OrderService;
import io.algobox.TestingIndicatorService;
import io.algobox.TestingInstrumentService;

import java.util.Collection;

import static org.mockito.Mockito.mock;

public final class TestingStrategyContextFactory {
  private static final String DEFAULT_INSTANCE_ID = "testing-instance";
  private static final String DEFAULT_TITLE = "testing";

  public static StrategyContext createStrategyContext(
      OrderService orderService, Collection<InstrumentMapping> instrumentMappings) {
    InstrumentService instrumentService = new TestingInstrumentService();
    IndicatorService indicatorService = new TestingIndicatorService();
    StrategyEventService strategyEventService = mock(StrategyEventService.class);
    return new StrategyContext(orderService, indicatorService, instrumentService,
        strategyEventService, ImmutableMap.of(), instrumentMappings, DEFAULT_INSTANCE_ID,
        DEFAULT_TITLE);
  }
}
