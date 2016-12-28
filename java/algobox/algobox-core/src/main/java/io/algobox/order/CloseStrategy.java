package io.algobox.order;

import com.google.common.base.MoreObjects;

import java.io.Serializable;
import java.util.Objects;

public final class CloseStrategy implements Serializable {
  private Double takeProfit;

  private Double stopLoss;

  public CloseStrategy() {
    // Intentionally empty.
  }

  public CloseStrategy(CloseStrategy closeStrategy) {
    this(closeStrategy.getTakeProfit(), closeStrategy.getStopLoss());
  }

  public CloseStrategy(Double takeProfit, Double stopLoss) {
    this.takeProfit = takeProfit;
    this.stopLoss = stopLoss;
  }

  public Double getTakeProfit() {
    return takeProfit;
  }

  public Double getStopLoss() {
    return stopLoss;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CloseStrategy that = (CloseStrategy) o;
    return Objects.equals(takeProfit, that.takeProfit) &&
        Objects.equals(stopLoss, that.stopLoss);
  }

  @Override
  public int hashCode() {
    return Objects.hash(takeProfit, stopLoss);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("takeProfit", takeProfit)
        .add("stopLoss", stopLoss)
        .toString();
  }
}
