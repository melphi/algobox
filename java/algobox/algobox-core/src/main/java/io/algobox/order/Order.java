package io.algobox.order;

import java.io.Serializable;
import java.util.Objects;

public final class Order implements Serializable {
  private String id;

  private long createdOn;

  private Long updatedOn;

  private OrderState state;

  private double amount;

  private String instrument;

  private OrderType type;

  private OrderDirection direction;

  private Double worstAcceptedPrice;

  private CloseStrategy closeStrategy;

  public Order() {
    // Intentionally empty.
  }

  public Order(String id, long createdOn, OrderState state, double amount, OrderType type,
      String instrument, Long updatedOn, OrderDirection direction, Double worstAcceptedPrice,
      CloseStrategy closeStrategy) {
    this.id = id;
    this.createdOn = createdOn;
    this.state = state;
    this.amount = amount;
    this.type = type;
    this.instrument = instrument;
    this.updatedOn = updatedOn;
    this.direction = direction;
    this.worstAcceptedPrice = worstAcceptedPrice;
    this.closeStrategy = closeStrategy;
  }

  public String getId() {
    return id;
  }

  public long getCreatedOn() {
    return createdOn;
  }

  public OrderState getState() {
    return state;
  }

  public double getAmount() {
    return amount;
  }

  public OrderType getType() {
    return type;
  }

  public String getInstrument() {
    return instrument;
  }

  public Long getUpdatedOn() {
    return updatedOn;
  }

  public OrderDirection getDirection() {
    return direction;
  }

  public Double getWorstAcceptedPrice() {
    return worstAcceptedPrice;
  }

  public CloseStrategy getCloseStrategy() {
    return closeStrategy;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Order order = (Order) o;
    return Objects.equals(createdOn, order.createdOn) &&
        Objects.equals(updatedOn, order.updatedOn) &&
        Objects.equals(amount, order.amount) &&
        Objects.equals(id, order.id) &&
        Objects.equals(state, order.state) &&
        Objects.equals(instrument, order.instrument) &&
        Objects.equals(type, order.type) &&
        Objects.equals(direction, order.direction) &&
        Objects.equals(worstAcceptedPrice, order.worstAcceptedPrice) &&
        Objects.equals(closeStrategy, order.closeStrategy);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, createdOn, updatedOn, state, amount, instrument, type, direction,
        worstAcceptedPrice, closeStrategy);
  }
}
