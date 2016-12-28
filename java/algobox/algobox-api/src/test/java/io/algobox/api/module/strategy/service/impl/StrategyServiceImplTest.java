package io.algobox.api.module.strategy.service.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import io.algobox.api.module.strategy.dao.impl.TestingStrategyHistoryDao;
import io.algobox.api.module.strategy.dao.impl.TestingStrategyRegistrationDao;
import io.algobox.api.module.strategy.domain.StrategyHistory;
import io.algobox.api.module.strategy.domain.StrategyInfo;
import io.algobox.api.module.strategy.domain.StrategyRegistration;
import io.algobox.api.module.strategy.domain.dto.StrategyRegistrationRequestDto;
import io.algobox.api.module.strategy.service.StrategyService;
import io.algobox.indicator.IndicatorService;
import io.algobox.order.OrderService;
import io.algobox.price.PriceTick;
import io.algobox.strategy.StrategyEventService;
import io.algobox.strategy.StrategyEventType;
import io.algobox.strategy.StrategyStatus;
import io.algobox.strategy.dummy.DummyStrategy;
import io.algobox.testing.TestingConstants;
import io.algobox.testing.TestingInstrumentService;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class StrategyServiceImplTest {
  private static final String DEFAULT_SOURCE =
      TestingConstants.DEFAULT_INSTRUMENT_MAPPING.getPriceConnectionId();
  private static final StrategyRegistrationRequestDto DEFAULT_STRATEGY_REGISTRATION =
      new StrategyRegistrationRequestDto(DummyStrategy.STRATEGY_ID, "title1", ImmutableMap.of(),
          ImmutableList.of(TestingConstants.DEFAULT_INSTRUMENT_MAPPING));

  private OrderService orderService;
  private StrategyService service;
  private StrategyEventService strategyEventService;

  @Before
  public void init() {
    strategyEventService = mock(StrategyEventService.class);
    orderService = mock(OrderService.class);
    service = new StrategyServiceImpl(strategyEventService,
        new TestingStrategyRegistrationDao(), new TestingStrategyHistoryDao(),
        orderService, mock(IndicatorService.class), new TestingInstrumentService(),
        new TestingStrategyManager());
  }

  @Test
  public void testRegisterStrategy() {
    String instanceId = service.createInstance(DEFAULT_STRATEGY_REGISTRATION);
    StrategyRegistration strategyRegistration =
        Iterables.getOnlyElement(service.getActiveInstances());
    assertEquals(instanceId, strategyRegistration.getInstanceId());
    assertNotNull(service.getInstanceStatus(instanceId));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAvoidSimilarStrategies() {
    service.createInstance(DEFAULT_STRATEGY_REGISTRATION);
    service.createInstance(DEFAULT_STRATEGY_REGISTRATION);
  }

  @Test
  public void testReceiveOrders() throws InterruptedException {
    service.createInstance(DEFAULT_STRATEGY_REGISTRATION);
    for (int i = 0; i < DummyStrategy.DEFAULT_TICKS_COUNT_ORDER_TRIGGER; i++) {
      service.onPriceTick(DEFAULT_SOURCE, TestingConstants.DEFAULT_PRICE_TICK_3);
    }
    verify(orderService, timeout(100).times(1)).sendOrderAsync(any());
  }

  @Test
  public void testRemoveStrategy() {
    String instanceId = service.createInstance(DEFAULT_STRATEGY_REGISTRATION);
    service.removeInstance(instanceId);
    service.onPriceTick(DEFAULT_SOURCE, TestingConstants.DEFAULT_PRICE_TICK_1);
    assertTrue(service.getActiveInstances().isEmpty());
    StrategyHistory strategyHistory = Iterables.getOnlyElement(service.getInstancesHistory(0, 1));
    assertEquals(instanceId, strategyHistory.getInstanceId());
    assertEquals(0, strategyHistory.getReceivedTicks());
  }

  @Test
  public void testSendTicksToRegisteredStrategies() throws Exception {
    service.createInstance(DEFAULT_STRATEGY_REGISTRATION);
    service.onPriceTick(DEFAULT_SOURCE, TestingConstants.DEFAULT_PRICE_TICK_1);
    service.onPriceTick(DEFAULT_SOURCE, TestingConstants.DEFAULT_PRICE_TICK_2);
    Thread.sleep(50);
    StrategyInfo info = service.getInstanceStatus(Iterables.getOnlyElement(
        service.getActiveInstances()).getInstanceId());
    assertEquals(1, info.getReceivedTicks());
    assertEquals(StrategyStatus.PROCESSING, info.getStatus());
  }

  @Test
  public void testRemoveStrategyIfRaisesException() throws Exception {
    PriceTick badPriceTick = new PriceTick(TestingConstants.DEFAULT_INSTRUMENT_DAX, 123, 0, 0);
    String instanceId = service.createInstance(DEFAULT_STRATEGY_REGISTRATION);
    service.onPriceTick(DEFAULT_SOURCE, badPriceTick);
    Thread.sleep(250);
    verify(strategyEventService, times(1)).logEventAsync(eq(instanceId),
        eq(StrategyEventType.ERROR), eq(badPriceTick), any(), anyString());
    assertTrue(service.getActiveInstances().isEmpty());
  }
}
