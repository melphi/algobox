package io.algobox.connector;

import com.google.common.base.MoreObjects;
import io.algobox.order.CloseStrategy;
import io.algobox.order.OpenStrategy;

import java.io.Serializable;

public final class ConnectorOrderRequest implements Serializable {
  private String orderRequestId;

  private String instrumentId;

  private Double amount;

  private OpenStrategy openStrategy;

  private CloseStrategy closeStrategy;

  public ConnectorOrderRequest() {
    // Intentionally empty.
  }

  public ConnectorOrderRequest(String orderRequestId, String instrumentId, Double amount,
        OpenStrategy openStrategy, CloseStrategy closeStrategy) {
    this.orderRequestId = orderRequestId;
    this.instrumentId = instrumentId;
    this.amount = amount;
    this.openStrategy = openStrategy;
    this.closeStrategy = closeStrategy;
  }

  public String getOrderRequestId() {
    return orderRequestId;
  }

  public String getInstrumentId() {
    return instrumentId;
  }

  public Double getAmount() {
    return amount;
  }

  public OpenStrategy getOpenStrategy() {
    return openStrategy;
  }

  public CloseStrategy getCloseStrategy() {
    return closeStrategy;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("orderRequestId", orderRequestId)
        .add("instrumentId", instrumentId)
        .add("amount", amount)
        .add("openStrategy", openStrategy)
        .add("closeStrategy", closeStrategy)
        .toString();
  }
}
