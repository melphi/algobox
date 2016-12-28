package io.algobox.order;

import io.algobox.connector.ConnectorException;
import io.algobox.price.SourcedPriceTickListener;

import java.util.Collection;

public interface OrderService extends SourcedPriceTickListener {
  void sendOrderAsync(OrderRequest orderRequest);

  void sendOrder(OrderRequest orderRequest);

  Collection<Order> findOrders(String connectionId, OrderState orderState)
      throws ConnectorException;

  Collection<Trade> findTrades(String connectionId, TradeState tradeState)
      throws ConnectorException;
}
