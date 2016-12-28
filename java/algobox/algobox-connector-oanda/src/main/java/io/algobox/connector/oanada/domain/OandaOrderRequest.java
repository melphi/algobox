package io.algobox.connector.oanada.domain;

import java.io.Serializable;

public final class OandaOrderRequest implements Serializable {
  private OandaOrderRequestDetails order;

  public OandaOrderRequest(OandaOrderRequestDetails order) {
    this.order = order;
  }

  public OandaOrderRequestDetails getOrder() {
    return order;
  }
}
