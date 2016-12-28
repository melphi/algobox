package io.algobox.price;

import com.google.common.base.MoreObjects;

import java.util.Objects;

public final class PriceTick implements Tick {
  private final String instrument;

  private final long time;

  private final double ask;

  private final double bid;

  public PriceTick(String instrument, long time, double ask, double bid) {
    this.instrument = instrument;
    this.time = time;
    this.ask = ask;
    this.bid = bid;
  }

  public String getInstrument() {
    return instrument;
  }

  public long getTime() {
    return time;
  }

  public double getAsk() {
    return ask;
  }

  public double getBid() {
    return bid;
  }

  @Override
  public int hashCode() {
    return Objects.hash(instrument, time, ask, bid);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof PriceTick)) {
      return false;
    }
    PriceTick other = (PriceTick) obj;
    return Objects.equals(instrument, other.getInstrument())
        && Objects.equals(time, other.getTime())
        && Objects.equals(ask, other.getAsk())
        && Objects.equals(bid, other.getBid());
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("instrument", instrument)
        .add("time", time)
        .add("ask", ask)
        .add("bid", bid)
        .toString();
  }
}
