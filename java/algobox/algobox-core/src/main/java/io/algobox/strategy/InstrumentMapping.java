package io.algobox.strategy;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.io.Serializable;

public final class InstrumentMapping implements Serializable {
  private String priceConnectionId;

  private String priceInstrumentId;

  private String orderConnectionId;

  private String orderInstrumentId;

  public InstrumentMapping() {
    // Intentionally empty.
  }

  public InstrumentMapping(String priceConnectionId, String priceInstrumentId,
      String orderConnectionId, String orderInstrumentId) {
    this.priceConnectionId = priceConnectionId;
    this.priceInstrumentId = priceInstrumentId;
    this.orderConnectionId = orderConnectionId;
    this.orderInstrumentId = orderInstrumentId;
  }

  public String getPriceConnectionId() {
    return priceConnectionId;
  }

  public String getOrderInstrumentId() {
    return orderInstrumentId;
  }

  public String getOrderConnectionId() {
    return orderConnectionId;
  }

  public String getPriceInstrumentId() {
    return priceInstrumentId;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("priceConnectionId", priceConnectionId)
        .add("priceInstrumentId", priceInstrumentId)
        .add("orderConnectionId", orderConnectionId)
        .add("orderInstrumentId", orderInstrumentId)
        .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InstrumentMapping that = (InstrumentMapping) o;
    return Objects.equal(priceConnectionId, that.priceConnectionId) &&
        Objects.equal(priceInstrumentId, that.priceInstrumentId) &&
        Objects.equal(orderConnectionId, that.orderConnectionId) &&
        Objects.equal(orderInstrumentId, that.orderInstrumentId);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(
        priceConnectionId, priceInstrumentId, orderConnectionId, orderInstrumentId);
  }
}
