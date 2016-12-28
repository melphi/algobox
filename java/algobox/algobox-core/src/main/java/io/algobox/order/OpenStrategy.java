package io.algobox.order;

import com.google.common.base.MoreObjects;

import java.io.Serializable;
import java.util.Objects;

public final class OpenStrategy implements Serializable {
  private OrderDirection orderDirection;

  private OrderType orderType;

  private Double worstAcceptedPrice;

  public OpenStrategy() {
    // Intentionally empty.
  }

  public OpenStrategy(
      OrderDirection orderDirection, OrderType openStrategyType, Double worstAcceptedPrice) {
    this.orderDirection = orderDirection;
    this.orderType = openStrategyType;
    this.worstAcceptedPrice = worstAcceptedPrice;
  }

  public OrderDirection getOrderDirection() {
    return orderDirection;
  }

  public OrderType getOrderType() {
    return orderType;
  }

  public Double getWorstAcceptedPrice() {
    return worstAcceptedPrice;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OpenStrategy that = (OpenStrategy) o;
    return Objects.equals(orderDirection, that.orderDirection) &&
        Objects.equals(orderType, that.orderType) &&
        Objects.equals(worstAcceptedPrice, that.worstAcceptedPrice);
  }

  @Override
  public int hashCode() {
    return Objects.hash(orderDirection, orderType, worstAcceptedPrice);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("orderDirection", orderDirection)
        .add("orderType", orderType)
        .add("worstAcceptedPrice", worstAcceptedPrice)
        .toString();
  }
}
