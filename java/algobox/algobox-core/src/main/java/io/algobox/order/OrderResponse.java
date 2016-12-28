package io.algobox.order;

import java.io.Serializable;

public final class OrderResponse implements Serializable {
  private String orderRequestId;

  private OrderStatus orderStatus;

  public OrderResponse(String orderRequestId) {
    this.orderRequestId = orderRequestId;
  }

  public String getOrderRequestId() {
    return orderRequestId;
  }
}
