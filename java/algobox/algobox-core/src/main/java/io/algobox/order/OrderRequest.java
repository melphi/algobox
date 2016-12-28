package io.algobox.order;

import com.google.common.base.MoreObjects;
import io.algobox.price.PriceTick;

import java.io.Serializable;
import java.util.Objects;

public final class OrderRequest implements Serializable {
  private String orderRequestId;

  private String connectionId;

  private String instrumentId;

  private double amount;

  private PriceTick priceTick;

  private OpenStrategy openStrategy;

  private CloseStrategy closeStrategy;

  public OrderRequest() {
    // Intentionally empty.
  }

  public OrderRequest(String orderRequestId, String connectionId, String instrumentId,
      double amount, PriceTick priceTick, OpenStrategy openStrategy, CloseStrategy closeStrategy) {
    this.orderRequestId = orderRequestId;
    this.connectionId = connectionId;
    this.instrumentId = instrumentId;
    this.amount = amount;
    this.priceTick = priceTick;
    this.openStrategy = openStrategy;
    this.closeStrategy = closeStrategy;
  }

  public String getOrderRequestId() {
    return orderRequestId;
  }

  public String getConnectionId() {
    return connectionId;
  }

  public String getInstrumentId() {
    return instrumentId;
  }

  public PriceTick getPriceTick() {
    return priceTick;
  }

  public OpenStrategy getOpenStrategy() {
    return openStrategy;
  }

  public CloseStrategy getCloseStrategy() {
    return closeStrategy;
  }

  public double getAmount() {
    return amount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OrderRequest that = (OrderRequest) o;
    return Objects.equals(orderRequestId, that.orderRequestId) &&
        Objects.equals(instrumentId, that.instrumentId) &&
        Objects.equals(connectionId, that.connectionId) &&
        Objects.equals(amount, that.amount) &&
        Objects.equals(priceTick, that.priceTick) &&
        Objects.equals(openStrategy, that.openStrategy) &&
        Objects.equals(closeStrategy, that.closeStrategy);
  }

  @Override
  public int hashCode() {
    return Objects.hash(orderRequestId, instrumentId, connectionId, amount, priceTick,
        openStrategy, closeStrategy);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("orderRequestId", orderRequestId)
        .add("instrumentId", instrumentId)
        .add("connectionId", connectionId)
        .add("amount", amount)
        .add("openStrategy", openStrategy)
        .add("closeStrategy", closeStrategy)
        .add("priceTick", priceTick)
        .toString();
  }
}
