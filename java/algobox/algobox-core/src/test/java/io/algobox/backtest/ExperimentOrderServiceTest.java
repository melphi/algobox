package io.algobox.backtest;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import io.algobox.connector.ConnectorException;
import io.algobox.order.CloseStrategy;
import io.algobox.order.OpenStrategy;
import io.algobox.order.OrderDirection;
import io.algobox.order.OrderRequest;
import io.algobox.order.OrderType;
import io.algobox.order.Trade;
import io.algobox.order.TradeState;
import io.algobox.price.PriceTick;
import io.algobox.TestingConstants;
import io.algobox.TestingInstrumentService;
import org.junit.Test;

import java.util.Collection;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ExperimentOrderServiceTest {
  private static final String DEFAULT_ORDER_REQUEST_ID = "orderRequestId";
  private static final PriceTick DEFAULT_PRICE_TICK_FIRST =
      new PriceTick(TestingConstants.DEFAULT_INSTRUMENT_DAX, 125, 1235, 1234);
  private static final PriceTick DEFAULT_PRICE_TICK_LAST =
      new PriceTick(TestingConstants.DEFAULT_INSTRUMENT_DAX, 128, 1233, 1232);
  private static final Collection<PriceTick> DEFAULT_PRICE_TICKS = ImmutableList.of(
      DEFAULT_PRICE_TICK_FIRST,
      new PriceTick(TestingConstants.DEFAULT_INSTRUMENT_DAX, 126, 1236, 1235),
      new PriceTick(TestingConstants.DEFAULT_INSTRUMENT_DAX, 127,1237, 1236),
      DEFAULT_PRICE_TICK_LAST);
  private static final OpenStrategy DEFAULT_OPEN_STRATEGY_LONG =
      new OpenStrategy(OrderDirection.LONG, OrderType.MARKET, 1237.0);
  private static final OpenStrategy DEFAULT_OPEN_STRATEGY_SHORT =
      new OpenStrategy(OrderDirection.SHORT, OrderType.MARKET, 1234.0);

  @Test
  public void testShouldFillMarketOrder() throws ConnectorException {
    OrderRequest orderRequest = createOrderRequest(
        DEFAULT_OPEN_STRATEGY_LONG, new CloseStrategy(1247.0, 1227.0));
    ExperimentOrderService orderService = process(DEFAULT_PRICE_TICKS, orderRequest);
    assertEquals(1, orderService.getActiveTradesCount());
    assertEquals(0, orderService.getClosedTradesCount());
    assertEquals(0, orderService.getActiveOrdersCount());
    Trade trade = Iterables.getOnlyElement(orderService.findTrades(
        TestingConstants.DEFAULT_CONNECTION_ID, TradeState.OPEN));
    checkTrade(trade, orderRequest);
  }

  @Test
  public void testShouldManageMultipleMarketOrders() throws ConnectorException {
    Collection<OrderRequest> orderRequests = ImmutableList.of(
        createOrderRequest(DEFAULT_OPEN_STRATEGY_LONG, new CloseStrategy(1247.0, 1227.0)),
        createOrderRequest(DEFAULT_OPEN_STRATEGY_LONG, new CloseStrategy(1235.0, 1227.0)));
    ExperimentOrderService orderService = process(DEFAULT_PRICE_TICKS, orderRequests);
    assertEquals(1, orderService.getActiveTradesCount());
    assertEquals(1, orderService.getClosedTradesCount());
    assertEquals(0, orderService.getActiveOrdersCount());
  }

  @Test
  public void testShouldApplyLatency_orderNotFilled() {
    OrderRequest orderRequest = createOrderRequest(
        DEFAULT_OPEN_STRATEGY_LONG, new CloseStrategy(1247.0, 1227.0));
    long latency = DEFAULT_PRICE_TICK_LAST.getTime() - DEFAULT_PRICE_TICK_FIRST.getTime() + 1;
    ExperimentOrderService orderService = process(
        DEFAULT_PRICE_TICKS, ImmutableList.of(orderRequest), latency);
    assertEquals(0, orderService.getActiveTradesCount());
    assertEquals(0, orderService.getClosedTradesCount());
    assertEquals(1, orderService.getActiveOrdersCount());
  }

  @Test
  public void testShouldApplyLatency_orderFilled() {
    OrderRequest orderRequest = createOrderRequest(
        DEFAULT_OPEN_STRATEGY_LONG, new CloseStrategy(1247.0, 1227.0));
    long latency = DEFAULT_PRICE_TICK_LAST.getTime() - DEFAULT_PRICE_TICK_FIRST.getTime();
    ExperimentOrderService orderService = process(
        DEFAULT_PRICE_TICKS, ImmutableList.of(orderRequest), latency);
    assertEquals(1, orderService.getActiveTradesCount());
    assertEquals(0, orderService.getClosedTradesCount());
    assertEquals(0, orderService.getActiveOrdersCount());
  }

  @Test
  public void testShouldCloseTradeOnTakeProfit_long() {
    OrderRequest orderRequest = createOrderRequest(
        DEFAULT_OPEN_STRATEGY_LONG, new CloseStrategy(1236.0, 1227.0));
    ExperimentOrderService orderService = process(DEFAULT_PRICE_TICKS, orderRequest);
    assertEquals(0, orderService.getActiveTradesCount());
    assertEquals(1, orderService.getClosedTradesCount());
    assertEquals(0, orderService.getActiveOrdersCount());
  }

  @Test
  public void testShouldCloseTradeOnStopLoss_long() {
    OrderRequest orderRequest = createOrderRequest(
        DEFAULT_OPEN_STRATEGY_LONG, new CloseStrategy(1246.0, 1236.0));
    ExperimentOrderService orderService = process(DEFAULT_PRICE_TICKS, orderRequest);
    assertEquals(0, orderService.getActiveTradesCount());
    assertEquals(1, orderService.getClosedTradesCount());
    assertEquals(0, orderService.getActiveOrdersCount());
  }

  @Test
  public void testShouldCloseTradeOnTakeProfit_short() {
    OrderRequest orderRequest = createOrderRequest(
        DEFAULT_OPEN_STRATEGY_SHORT, new CloseStrategy(1234.0, 1247.0));
    ExperimentOrderService orderService = process(DEFAULT_PRICE_TICKS, orderRequest);
    assertEquals(0, orderService.getActiveTradesCount());
    assertEquals(1, orderService.getClosedTradesCount());
    assertEquals(0, orderService.getActiveOrdersCount());
  }

  @Test
  public void testShouldCloseTradeOnStopLoss_short() {
    OrderRequest orderRequest = createOrderRequest(
        DEFAULT_OPEN_STRATEGY_SHORT, new CloseStrategy(1224.0, 1236.0));
    ExperimentOrderService orderService = process(DEFAULT_PRICE_TICKS, orderRequest);
    assertEquals(0, orderService.getActiveTradesCount());
    assertEquals(1, orderService.getClosedTradesCount());
    assertEquals(0, orderService.getActiveOrdersCount());
  }

  @Test
  public void testShouldCancelOrderOnOutOfBoundPrice_long() {
    OpenStrategy openStrategy = new OpenStrategy(OrderDirection.LONG, OrderType.MARKET, 1234.0);
    OrderRequest orderRequest = createOrderRequest(openStrategy, new CloseStrategy(1236.0, 1227.0));
    ExperimentOrderService orderService = process(DEFAULT_PRICE_TICKS, orderRequest);
    assertEquals(0, orderService.getActiveTradesCount());
    assertEquals(0, orderService.getClosedTradesCount());
    assertEquals(0, orderService.getActiveOrdersCount());
    assertEquals(1, orderService.getCancelledOrdersCount());
  }

  @Test
  public void testShouldCancelOrderOnOutOfBoundPrice_short() {
    OpenStrategy openStrategy = new OpenStrategy(OrderDirection.SHORT, OrderType.MARKET, 1235.0);
    OrderRequest orderRequest = createOrderRequest(openStrategy, new CloseStrategy(1234.0, 1247.0));
    ExperimentOrderService orderService = process(DEFAULT_PRICE_TICKS, orderRequest);
    assertEquals(0, orderService.getActiveTradesCount());
    assertEquals(0, orderService.getClosedTradesCount());
    assertEquals(0, orderService.getActiveOrdersCount());
    assertEquals(1, orderService.getCancelledOrdersCount());
  }

  @Test
  public void testShouldUpdatePl_longActive() throws ConnectorException {
    OrderRequest orderRequest = createOrderRequest(
        DEFAULT_OPEN_STRATEGY_LONG, new CloseStrategy(1247.0, 1227.0));
    ExperimentOrderService orderService = process(DEFAULT_PRICE_TICKS, orderRequest);
    Trade trade = Iterables.getOnlyElement(orderService.findTrades(
        TestingConstants.DEFAULT_CONNECTION_ID, TradeState.OPEN));
    double plPips = trade.getProfitLossPips();
    assertTrue(plPips != 0.0);
    assertEquals(plPips, orderService.getActiveTradesPlPips());
    assertEquals(0.0, orderService.getClosedTradesPlPips());
  }

  @Test
  public void testShouldUpdatePl_longClosed() throws ConnectorException {
    OrderRequest orderRequest = createOrderRequest(
        DEFAULT_OPEN_STRATEGY_LONG, new CloseStrategy(1247.0, 1233.0));
    ExperimentOrderService orderService = process(DEFAULT_PRICE_TICKS, orderRequest);
    Trade trade = Iterables.getOnlyElement(orderService.findTrades(
        TestingConstants.DEFAULT_CONNECTION_ID, TradeState.CLOSED));
    double plPips = trade.getProfitLossPips();
    assertTrue(plPips != 0.0);
    assertEquals(0.0, orderService.getActiveTradesPlPips());
    assertEquals(plPips, orderService.getClosedTradesPlPips());
  }

  @Test
  public void testShouldUpdatePl_shortActive() throws ConnectorException {
    OrderRequest orderRequest = createOrderRequest(
        DEFAULT_OPEN_STRATEGY_SHORT, new CloseStrategy(1227.0, 1247.0));
    ExperimentOrderService orderService = process(DEFAULT_PRICE_TICKS, orderRequest);
    Trade trade = Iterables.getOnlyElement(orderService.findTrades(
        TestingConstants.DEFAULT_CONNECTION_ID, TradeState.OPEN));
    double plPips = trade.getProfitLossPips();
    assertTrue(plPips != 0.0);
    assertEquals(plPips, orderService.getActiveTradesPlPips());
    assertEquals(0.0, orderService.getClosedTradesPlPips());
  }

  @Test
  public void testShouldUpdatePl_shortClosed() throws ConnectorException {
    OrderRequest orderRequest = createOrderRequest(
        DEFAULT_OPEN_STRATEGY_SHORT, new CloseStrategy(1234.0, 1247.0));
    ExperimentOrderService orderService = process(DEFAULT_PRICE_TICKS, orderRequest);
    Trade trade = Iterables.getOnlyElement(orderService.findTrades(
        TestingConstants.DEFAULT_CONNECTION_ID, TradeState.CLOSED));
    double plPips = trade.getProfitLossPips();
    assertTrue(plPips != 0.0);
    assertEquals(0.0, orderService.getActiveTradesPlPips());
    assertEquals(plPips, orderService.getClosedTradesPlPips());
  }

  private void checkTrade(Trade trade, OrderRequest orderRequest) {
    assertNotNull(trade);
    assertEquals(orderRequest.getCloseStrategy(), trade.getCloseStrategy());
    assertEquals(orderRequest.getOpenStrategy().getOrderDirection(), trade.getDirection());
    assertEquals(orderRequest.getInstrumentId(), trade.getInstrumentId());
    assertEquals(orderRequest.getAmount(), trade.getAmount());
    assertTrue(trade.getCreatedOn() >= orderRequest.getPriceTick().getTime());
  }

  private OrderRequest createOrderRequest(OpenStrategy openStrategy, CloseStrategy closeStrategy) {
    return new OrderRequest(DEFAULT_ORDER_REQUEST_ID,
        TestingConstants.DEFAULT_CONNECTION_ID,
        TestingConstants.DEFAULT_INSTRUMENT_DAX, 0.01, DEFAULT_PRICE_TICK_FIRST, openStrategy,
        closeStrategy);
  }

  private ExperimentOrderService process(
      Collection<PriceTick> priceTicks, OrderRequest orderRequest) {
    return process(priceTicks, ImmutableList.of(orderRequest), 0);
  }

  private ExperimentOrderService process(
      Collection<PriceTick> priceTicks, Collection<OrderRequest> orderRequests) {
    return process(priceTicks, orderRequests, 0);
  }

  private ExperimentOrderService process(Collection<PriceTick> priceTicks,
        Collection<OrderRequest> orderRequests, long latencyMilliseconds) {
    ExperimentOrderService orderService = new ExperimentOrderService(
        new TestingInstrumentService(), latencyMilliseconds);
    for (OrderRequest orderRequest: orderRequests) {
      orderService.sendOrderAsync(orderRequest);
    }
    for (PriceTick priceTick: priceTicks) {
      orderService.onPriceTick(ExperimentOrderService.DEFAULT_CONNECTION_ID, priceTick);
    }
    return orderService;
  }
}
