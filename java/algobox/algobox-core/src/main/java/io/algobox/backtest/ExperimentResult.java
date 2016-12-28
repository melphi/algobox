package io.algobox.backtest;

import com.google.common.base.Objects;
import io.algobox.strategy.StrategyEvent;

import java.io.Serializable;
import java.util.Collection;

public final class ExperimentResult implements Serializable {
  private int closedTradesCount;

  private double closedTradesPlPips;

  private int activeTradesCount;

  private double activeTradesPlPips;

  private long processedTicksCount;

  private long processedTimeMilliseconds;

  private long activeOrdersCount;

  private Collection<StrategyEvent> strategyEvents;

  public ExperimentResult(int closedTradesCount, double closedTradesPlPips, int activeTradesCount,
      double activeTradesPlPips, long processedTicksCount, long processedTimeMilliseconds,
      Collection<StrategyEvent> strategyEvents, long activeOrdersCount) {
    this.closedTradesCount = closedTradesCount;
    this.closedTradesPlPips = closedTradesPlPips;
    this.activeTradesCount = activeTradesCount;
    this.activeTradesPlPips = activeTradesPlPips;
    this.processedTicksCount = processedTicksCount;
    this.processedTimeMilliseconds = processedTimeMilliseconds;
    this.strategyEvents = strategyEvents;
    this.activeOrdersCount = activeOrdersCount;
  }

  public double getClosedTradesPlPips() {
    return closedTradesPlPips;
  }

  public int getClosedTradesCount() {
    return closedTradesCount;
  }

  public double getActiveTradesPlPips() {
    return activeTradesPlPips;
  }

  public int getActiveTradesCount() {
    return activeTradesCount;
  }

  public long getProcessedTicksCount() {
    return processedTicksCount;
  }

  public long getProcessedTimeMilliseconds() {
    return processedTimeMilliseconds;
  }

  public Collection<StrategyEvent> getStrategyEvents() {
    return strategyEvents;
  }

  public long getActiveOrdersCount() {
    return activeOrdersCount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExperimentResult that = (ExperimentResult) o;
    return Objects.equal(closedTradesCount, that.closedTradesCount) &&
        Objects.equal(that.closedTradesPlPips, closedTradesPlPips) &&
        Objects.equal(activeTradesCount, that.activeTradesCount) &&
        Objects.equal(that.activeTradesPlPips, activeTradesPlPips) &&
        Objects.equal(processedTicksCount, that.processedTicksCount) &&
        Objects.equal(processedTimeMilliseconds, that.processedTimeMilliseconds) &&
        Objects.equal(strategyEvents, that.strategyEvents) &&
        Objects.equal(activeOrdersCount, that.activeOrdersCount);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(closedTradesCount, closedTradesPlPips, activeTradesCount,
        activeTradesPlPips, processedTicksCount, processedTimeMilliseconds, strategyEvents,
        activeOrdersCount);
  }
}
