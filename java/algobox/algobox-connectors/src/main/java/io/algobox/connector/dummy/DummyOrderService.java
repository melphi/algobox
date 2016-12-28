package io.algobox.connector.dummy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.algobox.connector.ConnectorException;
import io.algobox.connector.ConnectorOrderRequest;
import io.algobox.connector.ConnectorOrderService;
import io.algobox.order.Order;
import io.algobox.order.OrderState;
import io.algobox.order.OrderType;
import io.algobox.order.Trade;
import io.algobox.order.TradeState;
import io.algobox.util.DateTimeUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class DummyOrderService implements ConnectorOrderService {
  private Map<String, Order> pendingOrders = Maps.newHashMap();
  private List<Order> cancelledOrders = Lists.newArrayList();

  @Override
  public synchronized void sendOrderAsync(ConnectorOrderRequest orderRequest)
      throws ConnectorException {
    Order order = new Order(UUID.randomUUID().toString(),
        DateTimeUtils.getCurrentUtcTimestampMilliseconds(), OrderState.FILLED,
        orderRequest.getAmount(), OrderType.MARKET, orderRequest.getInstrumentId(),
        null, orderRequest.getOpenStrategy().getOrderDirection(),
        orderRequest.getOpenStrategy().getWorstAcceptedPrice(), orderRequest.getCloseStrategy());
    pendingOrders.put(order.getId(), order);
  }

  @Override
  public synchronized void closeAllOrdersAndPositions() throws ConnectorException {
    for (Order order: pendingOrders.values()) {
      cancelledOrders.add(order);
    }
    pendingOrders.clear();
  }

  @Override
  public synchronized Collection<Order> findOrders(OrderState orderState)
      throws ConnectorException {
    switch (orderState) {
      case PENDING:
        return ImmutableList.copyOf(pendingOrders.values());
      case CANCELLED:
        return ImmutableList.copyOf(cancelledOrders);
      case FILLED:
        return ImmutableList.of();
      default:
        throw new IllegalArgumentException();
    }
  }

  @Override
  public synchronized Collection<Trade> findTrades(TradeState tradeState)
      throws ConnectorException {
    return ImmutableList.of();
  }

  @Override
  public synchronized void closeOpenOrder(String orderId) throws ConnectorException {
    if (pendingOrders.remove(orderId) == null) {
      throw new IllegalArgumentException("Order not found.");
    }
  }

  @Override
  public synchronized void closeOpenTrade(String tradeId) throws ConnectorException {
    throw new IllegalAccessError(String.format("Trade id [%s] not found or closed.", tradeId));
  }
}
