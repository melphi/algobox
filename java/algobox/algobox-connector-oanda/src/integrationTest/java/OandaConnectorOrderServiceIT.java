import com.google.common.base.Strings;
import io.algobox.connector.ConnectorException;
import io.algobox.connector.ConnectorOrderRequest;
import io.algobox.connector.ConnectorOrderService;
import io.algobox.order.CloseStrategy;
import io.algobox.order.OpenStrategy;
import io.algobox.order.Order;
import io.algobox.order.OrderDirection;
import io.algobox.order.OrderResponse;
import io.algobox.order.OrderState;
import io.algobox.order.OrderType;
import io.algobox.order.Trade;
import io.algobox.order.TradeState;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OandaConnectorOrderServiceIT extends AbstractOandaConnectorIT {
  private ConnectorOrderService orderService;

  @Override
  public void init() throws ConnectorException {
    super.init();
    orderService = connector.getOrderService();
  }

  @Override
  public void dispose() throws ConnectorException {
    super.dispose();
    orderService.closeAllOrdersAndPositions();
    assertTrue(orderService.findOrders(OrderState.PENDING).isEmpty());
    assertTrue(orderService.findTrades(TradeState.OPEN).isEmpty());
  }

  @Test
  public void testMarketOrder_cancelled()
      throws ConnectorException, ExecutionException, InterruptedException, TimeoutException {
    orderService.sendOrderAsync(createOrderRequest(2.0, 0.5));
    try {
      orderFuture.get(20, TimeUnit.SECONDS);
    } catch (Exception e) {
      assertTrue(orderFuture.isCompletedExceptionally());
      return;
    }
    fail("It should never arrive here.");
  }

  @Test
  public void testMarketOrder_accepted()
      throws ConnectorException, InterruptedException, ExecutionException, TimeoutException {
    orderService.sendOrderAsync(createOrderRequest(1.5, 1));
    OrderResponse orderResponse = orderFuture.get(20, TimeUnit.SECONDS);
    assertNotNull(orderResponse);
  }

  @Test
  public void testFindCancelledOrders() throws ConnectorException {
    for (Order order: orderService.findOrders(OrderState.CANCELLED)) {
      assertOrder(order);
    }
  }

  @Test
  public void testFindOpenOrders() throws ConnectorException {
    for (Order order: orderService.findOrders(OrderState.PENDING)) {
      assertOrder(order);
    }
  }

  @Test
  public void testFindFilledOrders() throws ConnectorException {
    for (Order order: orderService.findOrders(OrderState.FILLED)) {
      assertOrder(order);
    }
  }

  @Test
  public void testFindOpenTrades() throws ConnectorException {
    for (Trade trade: orderService.findTrades(TradeState.OPEN)) {
      assertTrade(trade);
    }
  }

  @Test
  public void testFindClosedTrades() throws ConnectorException {
    for (Trade trade: orderService.findTrades(TradeState.CLOSED)) {
      assertTrade(trade);
    }
  }

  private void assertTrade(Trade trade) {
    assertNotNull(trade);
    assertNotNull(trade.getState());
    assertNotNull(trade.getDirection());
    assertFalse(Strings.isNullOrEmpty(trade.getId()));
    assertFalse(Strings.isNullOrEmpty(trade.getInstrumentId()));
    assertTrue(trade.getPrice() > 0);
    assertTrue(trade.getCreatedOn() > 0);
    assertNotNull(trade.getProfitLoss());
    assertNotNull(trade.getProfitLossPips());
  }

  private void assertOrder(Order order) {
    assertNotNull(order);
    assertNotNull(order.getState());
    assertNotNull(order.getType());
    assertFalse(Strings.isNullOrEmpty(order.getId()));
    assertTrue(order.getCreatedOn() > 0);
  }

  private ConnectorOrderRequest createOrderRequest(double takeProfit, double stopLoss) {
    OpenStrategy openStrategy = new OpenStrategy(OrderDirection.LONG, OrderType.MARKET, stopLoss);
    CloseStrategy closeStrategy = new CloseStrategy(takeProfit, stopLoss);
    return new ConnectorOrderRequest(IntegrationTestingConstants.DEFAULT_ORDER_ID,
        IntegrationTestingConstants.DEFAULT_INSTRUMENT_ID, 1.0, openStrategy, closeStrategy);
  }
}
