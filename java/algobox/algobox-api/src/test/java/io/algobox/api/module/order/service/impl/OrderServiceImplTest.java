package io.algobox.api.module.order.service.impl;

import io.algobox.api.module.notification.service.NotificationClient;
import io.algobox.connector.Connector;
import io.algobox.connector.ConnectorListener;
import io.algobox.connector.ConnectorOrderRequest;
import io.algobox.connector.dummy.DummyConnector;
import io.algobox.connector.service.ConnectorService;
import io.algobox.order.CloseStrategy;
import io.algobox.order.OpenStrategy;
import io.algobox.order.OrderRequest;
import io.algobox.order.OrderResponse;
import io.algobox.order.OrderService;
import io.algobox.order.OrderState;
import io.algobox.price.PriceTick;
import io.algobox.testing.TestingConstants;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// TODO: Test this service more extensively.
public class OrderServiceImplTest {
  private static final String DEFAULT_CONNECTION_ID = "connection1";
  private static final OrderRequest DEFAULT_ORDER_REQUEST = new OrderRequest("orderId1",
      DEFAULT_CONNECTION_ID, "instrument1", 0.01, TestingConstants.DEFAULT_PRICE_TICK_1,
      new OpenStrategy(), new CloseStrategy());

  private Connector connector;
  private OrderService service;
  private NotificationClient notificationClient;
  private ConnectorService connectorService;

  @Before
  public void init() {
    connector = new DummyConnector(new DefatulConnectorListener(), 10);
    notificationClient = mock(NotificationClient.class);
    connectorService = mock(ConnectorService.class);
    when(connectorService.getConnectionById(eq(DEFAULT_CONNECTION_ID))).thenReturn(connector);
    service = new OrderServiceImpl(connectorService, notificationClient);
  }

  @Test
  public void sendOrder() throws Exception {
    service.sendOrder(DEFAULT_ORDER_REQUEST);
    verify(notificationClient, times(1)).notifyMessage(anyString());
    assertEquals(1, connector.getOrderService().findOrders(OrderState.PENDING).size());
  }

  private class DefatulConnectorListener implements ConnectorListener {
    @Override
    public void onConnected() {
      // Intentionally empty.
    }

    @Override
    public void onDisconnected() {
      // Intentionally empty.
    }

    @Override
    public void onGenericError(Throwable throwable) {
      // Intentionally empty.
    }

    @Override
    public void onPriceTick(PriceTick priceTick) {
      // Intentionally empty.
    }

    @Override
    public void onOrderOpen(OrderResponse orderResponse) {
      // Intentionally empty.
    }

    @Override
    public void onOrderError(ConnectorOrderRequest orderRequest, Throwable cause) {
      // Intentionally empty.
    }
  }
}
