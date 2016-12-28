package io.algobox.strategy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.algobox.indicator.IndicatorService;
import io.algobox.instrument.InstrumentService;
import io.algobox.order.OrderService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class StrategyContextTest {
  private static final InstrumentMapping INSTRUMENT_MAPPING = new InstrumentMapping(
      "instrument1", "connection1", "instrument2", "connection2");

  private StrategyContext strategyContext;

  @Before
  public void init() {
    this.strategyContext = createStrategyContext();
  }

  @Test
  public void testIncrementTicksCount() {
    assertEquals(0, strategyContext.getReceivedTicks());
    strategyContext.incrementReceivedTicks();
    assertEquals(1, strategyContext.getReceivedTicks());
  }

  @Test
  public void testGetMapping() {
    assertEquals(
        INSTRUMENT_MAPPING.getPriceConnectionId(), strategyContext.getOnlyPriceConnectorId());
    assertEquals(
        INSTRUMENT_MAPPING.getPriceInstrumentId(), strategyContext.getOnlyPriceInstrumentId());
    assertEquals(
        INSTRUMENT_MAPPING.getOrderConnectionId(), strategyContext.getOnlyOrderConnectorId());
    assertEquals(
        INSTRUMENT_MAPPING.getOrderInstrumentId(), strategyContext.getOnlyOrderInstrumentId());
  }

  private StrategyContext createStrategyContext() {
    return new StrategyContext(mock(OrderService.class), mock(IndicatorService.class),
        mock(InstrumentService.class), mock(StrategyEventService.class), ImmutableMap.of(),
        ImmutableList.of(INSTRUMENT_MAPPING), "instance1", "title1");
  }
}
