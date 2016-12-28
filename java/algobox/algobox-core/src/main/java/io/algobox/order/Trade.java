package io.algobox.order;

import com.google.common.base.Objects;

import java.io.Serializable;

public final class Trade implements Serializable {
  private String id;

  private String connectionId;

  private String instrumentId;

  private long createdOn;

  private Long updatedOn;

  private double price;

  private TradeState state;

  private double amount;

  private OrderDirection direction;

  private Double profitLossPips;

  private Double profitLoss;

  private CloseStrategy closeStrategy;

  public Trade() {
    // Intentionally empty.
  }

  public Trade(String id, String connectionId, String instrumentId, long createdOn,
      Long updatedOn, double price, TradeState state, double amount, OrderDirection direction,
      Double profitLossPips, Double profitLoss, CloseStrategy closeStrategy) {
    this.id = id;
    this.connectionId = connectionId;
    this.instrumentId = instrumentId;
    this.createdOn = createdOn;
    this.updatedOn = updatedOn;
    this.price = price;
    this.state = state;
    this.amount = amount;
    this.direction = direction;
    this.profitLossPips = profitLossPips;
    this.profitLoss = profitLoss;
    this.closeStrategy = closeStrategy;
  }

  public String getId() {
    return id;
  }

  public String getConnectionId() {
    return connectionId;
  }

  public String getInstrumentId() {
    return instrumentId;
  }

  public long getCreatedOn() {
    return createdOn;
  }

  public Long getUpdatedOn() {
    return updatedOn;
  }

  public double getPrice() {
    return price;
  }

  public TradeState getState() {
    return state;
  }

  public double getAmount() {
    return amount;
  }

  public OrderDirection getDirection() {
    return direction;
  }

  public CloseStrategy getCloseStrategy() {
    return closeStrategy;
  }

  public Double getProfitLossPips() {
    return profitLossPips;
  }

  public Double getProfitLoss() {
    return profitLoss;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Trade trade = (Trade) o;
    return Objects.equal(createdOn, trade.createdOn) &&
        Objects.equal(updatedOn, trade.updatedOn) &&
        Objects.equal(price, trade.price) &&
        Objects.equal(amount, trade.amount) &&
        Objects.equal(id, trade.id) &&
        Objects.equal(connectionId, trade.connectionId) &&
        Objects.equal(instrumentId, trade.instrumentId) &&
        Objects.equal(state, trade.state) &&
        Objects.equal(direction, trade.direction) &&
        Objects.equal(profitLossPips, trade.profitLossPips) &&
        Objects.equal(profitLoss, trade.profitLoss) &&
        Objects.equal(closeStrategy, trade.closeStrategy);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id, connectionId, instrumentId, createdOn, updatedOn, price, state,
        amount, direction, profitLossPips, profitLoss, direction, closeStrategy);
  }
}
