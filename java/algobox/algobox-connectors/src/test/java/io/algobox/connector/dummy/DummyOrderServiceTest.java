package io.algobox.connector.dummy;

import io.algobox.connector.ConnectorException;
import io.algobox.connector.ConnectorOrderRequest;
import io.algobox.connector.ConnectorOrderService;
import io.algobox.order.CloseStrategy;
import io.algobox.order.OpenStrategy;
import io.algobox.order.OrderDirection;
import io.algobox.order.OrderState;
import io.algobox.order.OrderType;
import io.algobox.order.TradeState;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DummyOrderServiceTest extends AbstractDummyConnectorTest {
  private ConnectorOrderService orderService;

  @Test
  public void shouldNeverFillOrders() throws ConnectorException {
    orderService.sendOrderAsync(createOrderRequest());
    assertTrue(orderService.findOrders(OrderState.FILLED).isEmpty());
    assertEquals(1, orderService.findOrders(OrderState.PENDING).size());
    assertTrue(orderService.findTrades(TradeState.OPEN).isEmpty());
    assertTrue(orderService.findTrades(TradeState.CLOSED).isEmpty());
  }

  @Test
  public void testShouldCloseAllOrders() throws ConnectorException {
    orderService.sendOrderAsync(createOrderRequest());
    orderService.closeAllOrdersAndPositions();
    assertTrue(orderService.findOrders(OrderState.PENDING).isEmpty());
  }

  @Override
  protected void completeInit() throws ConnectorException {
    orderService = connector.getOrderService();
  }

  private ConnectorOrderRequest createOrderRequest() {
    OpenStrategy openStrategy = new OpenStrategy(OrderDirection.LONG, OrderType.MARKET, 1.0);
    CloseStrategy closeStrategy = new CloseStrategy(1.0, 1.0);
    return new ConnectorOrderRequest(
        "orderRequestId", "instrumentId", 1.01, openStrategy, closeStrategy);
  }
}
