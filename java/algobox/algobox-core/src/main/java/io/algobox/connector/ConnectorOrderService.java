package io.algobox.connector;

import io.algobox.order.Order;
import io.algobox.order.OrderState;
import io.algobox.order.Trade;
import io.algobox.order.TradeState;

import java.util.Collection;

public interface ConnectorOrderService {
  void sendOrderAsync(ConnectorOrderRequest orderRequest) throws ConnectorException;

  void closeAllOrdersAndPositions() throws ConnectorException;

  Collection<Order> findOrders(OrderState orderState) throws ConnectorException;

  Collection<Trade> findTrades(TradeState tradeState) throws ConnectorException;

  void closeOpenOrder(String orderId) throws ConnectorException;

  void closeOpenTrade(String tradeId) throws ConnectorException;
}
