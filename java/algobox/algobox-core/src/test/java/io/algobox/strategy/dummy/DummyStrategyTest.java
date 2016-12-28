package io.algobox.strategy.dummy;

import com.google.common.collect.ImmutableList;
import io.algobox.TestingConstants;
import io.algobox.order.OrderService;
import io.algobox.strategy.InstrumentMapping;
import io.algobox.strategy.Strategy;
import io.algobox.strategy.StrategyContext;
import io.algobox.strategy.StrategyStatus;
import io.algobox.strategy.TestingStrategyContextFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class DummyStrategyTest {
  private static final String DEFAULT_CONNECTION_ID = "test";
  private static final String DEFAULT_INSTRUMENT_ID =
      TestingConstants.DEFAULT_PRICE_TICK.getInstrument();

  private OrderService orderService;
  private Strategy strategy;

  @Before
  public void init() {
    orderService = mock(OrderService.class);
    Collection<InstrumentMapping> instrumentMapping = ImmutableList.of(new InstrumentMapping(
        DEFAULT_CONNECTION_ID, DEFAULT_INSTRUMENT_ID,
        DEFAULT_CONNECTION_ID, DEFAULT_INSTRUMENT_ID));
    StrategyContext strategyContext = TestingStrategyContextFactory.createStrategyContext(
        orderService, instrumentMapping);
    strategy = new DummyStrategy(strategyContext);
  }

  @Test
  public void shouldProcessTicks() throws Exception {
    strategy.onPriceTick(TestingConstants.DEFAULT_PRICE_TICK);
    assertEquals(StrategyStatus.PROCESSING, strategy.getStrategyContext().getStatus());
    verify(orderService, never()).sendOrderAsync(any());
  }

  @Test
  public void shouldSendOrders() throws Exception {
    for (int i = 0; i < DummyStrategy.DEFAULT_TICKS_COUNT_ORDER_TRIGGER; i++) {
      strategy.onPriceTick(TestingConstants.DEFAULT_PRICE_TICK);
    }
    assertEquals(StrategyStatus.PROCESSING, strategy.getStrategyContext().getStatus());
    verify(orderService, times(1)).sendOrderAsync(any());
  }
}
