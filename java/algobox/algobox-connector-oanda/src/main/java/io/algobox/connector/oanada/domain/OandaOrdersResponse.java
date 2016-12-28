package io.algobox.connector.oanada.domain;

import java.util.Collection;

public final class OandaOrdersResponse {
  private Collection<OandaOrder> orders;

  public Collection<OandaOrder> getOrders() {
    return orders;
  }
}
