package io.algobox.api.module.order.service.impl;

import avro.shaded.com.google.common.collect.Maps;
import io.algobox.api.component.exception.ValueNotFound;
import io.algobox.api.module.notification.service.NotificationClient;
import io.algobox.connector.Connector;
import io.algobox.connector.ConnectorException;
import io.algobox.connector.ConnectorOrderRequest;
import io.algobox.connector.ConnectorOrderService;
import io.algobox.connector.service.ConnectorService;
import io.algobox.order.Order;
import io.algobox.order.OrderRequest;
import io.algobox.order.OrderService;
import io.algobox.order.OrderState;
import io.algobox.order.Trade;
import io.algobox.order.TradeState;
import io.algobox.price.PriceTick;
import io.algobox.util.DateTimeUtils;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.algobox.api.module.order.util.OrderPreconditions.checkOrderRequest;
import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;

@Service
public final class OrderServiceImpl implements OrderService {
  private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);
  private static final double MAX_ORDER_AMOUNT = 0.01;
  private static final long MAX_DAILY_ORDERS = 2;

  private final ConnectorService connectorService;
  private final NotificationClient notificationClient;
  private final Map<String, ConnectorOrderService> orderServices = Maps.newConcurrentMap();

  private volatile long nextDayTimestamp = 0;
  private volatile long dailyOrdersCount = 0;

  @Inject
  public OrderServiceImpl(ConnectorService connectorService, NotificationClient notificationClient) {
    this.connectorService = connectorService;
    this.notificationClient = notificationClient;
  }

  @Override
  public void sendOrderAsync(final OrderRequest orderRequest) {
    new Thread(() -> sendOrder(orderRequest)).start();
  }

  @Override
  public void sendOrder(final OrderRequest orderRequest) {
    try {
      checkCanSendOrder(orderRequest);
    } catch (Exception e) {
      LOGGER.warn(String.format(
          "Order not send, reason: [%s], order: [%s].", e.getMessage(), orderRequest));
    }
    ConnectorOrderRequest connectorOrderRequest = createConnectorOrderRequest(orderRequest);
    try {
      ConnectorOrderService connectorOrderService =
          getOrLoadOrderService(orderRequest.getConnectionId());
      connectorOrderService.sendOrderAsync(connectorOrderRequest);
      notificationClient.notifyMessage(String.format("Order request sent to [%s]:[%s].",
          connectorOrderRequest.getInstrumentId(), orderRequest.getConnectionId()));
    } catch (Exception e) {
      LOGGER.warn(String.format("Error while sending order [%s] to [%s]: [%s]",
          orderRequest, orderRequest.getConnectionId(), e.getMessage()), e);
    }
  }

  @Override
  public Collection<Order> findOrders(String connectionId, OrderState orderState)
      throws ConnectorException {
    return getConnectorOrderService(connectionId).findOrders(orderState);
  }

  @Override
  public Collection<Trade> findTrades(String connectionId, TradeState tradeState)
      throws ConnectorException {
    return getConnectorOrderService(connectionId).findTrades(tradeState);
  }

  @Override
  public void onPriceTick(String source, PriceTick priceTick) {
    if (priceTick.getTime() > nextDayTimestamp) {
      initDay();
    }
  }

  private void initDay() {
    nextDayTimestamp = DateTimeUtils.getEndOfDayTimestamp(DateTimeUtils.getCurrentUtcDateTime());
    dailyOrdersCount = 0;
  }

  private void checkCanSendOrder(OrderRequest orderRequest) {
    checkNotNull(orderRequest);
    if (DateTimeUtils.getCurrentUtcTimestampMilliseconds() > nextDayTimestamp) {
      initDay();
    }
    if (dailyOrdersCount >= MAX_DAILY_ORDERS) {
      throw new IllegalArgumentException(String.format(
          "Reached maximum limit of [%d] daily orders", MAX_DAILY_ORDERS));
    }
    dailyOrdersCount++;
  }

  private ConnectorOrderService getConnectorOrderService(String connectionId) {
    checkNotNullOrEmpty(connectionId);
    ConnectorOrderService connectorOrderService = orderServices.get(connectionId);
    if (connectorOrderService == null) {
      Connector connector = connectorService.getConnectionById(connectionId);
      if (connector == null) {
        throw new ValueNotFound(String.format("Connection id [%s] not found.", connectionId));
      }
      try {
        connectorOrderService = connector.getOrderService();
      } catch (ConnectorException e) {
        throw new IllegalArgumentException(String.format(
            "Error while getting order service for connection [%s]: [%s]",
            connectionId, e.getMessage()));
      }
      orderServices.put(connectionId, connectorOrderService);
    }
    return connectorOrderService;
  }

  private ConnectorOrderRequest createConnectorOrderRequest(OrderRequest orderRequest) {
    checkOrderRequest(orderRequest);
    // TODO(robertom): Convert the orderRequest amount to connector order request amount.
    return new ConnectorOrderRequest(orderRequest.getOrderRequestId(),
        orderRequest.getInstrumentId(), MAX_ORDER_AMOUNT, orderRequest.getOpenStrategy(),
        orderRequest.getCloseStrategy());
  }

  private ConnectorOrderService getOrLoadOrderService(String accountId) {
    checkNotNullOrEmpty(accountId);
    ConnectorOrderService connectorOrderService = orderServices.get(accountId);
    if (connectorOrderService == null) {
      try {
        Connector connector = connectorService.getConnectionById(accountId);
        connectorOrderService = connector.getOrderService();
        orderServices.put(accountId, connectorOrderService);
      } catch (Exception e) {
        LOGGER.error(String.format("Error while loading order service for [%s]", accountId), e);
        throw new IllegalArgumentException(e);
      }
    }
    return connectorOrderService;
  }
}
