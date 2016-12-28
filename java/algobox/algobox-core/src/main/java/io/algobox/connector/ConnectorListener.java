package io.algobox.connector;

import io.algobox.order.OrderResponse;
import io.algobox.price.PriceTick;

public interface ConnectorListener {
  void onConnected();

  void onDisconnected();

  void onGenericError(Throwable throwable);

  void onPriceTick(PriceTick priceTick);

  void onOrderOpen(OrderResponse orderResponse);

  void onOrderError(ConnectorOrderRequest orderRequest, Throwable cause);
}
