package io.algobox.price;

import java.util.Objects;

public final class PriceBar implements Bar {
  private final String instrument;

  private final String timeFrame;

  private final long time;

  private final double askOpen;

  private final double bidOpen;

  private final double askHigh;

  private final double bidHigh;

  private final double askLow;

  private final double bidLow;

  private final double askClose;

  private final double bidClose;

  public PriceBar(String instrument, String timeFrame, long time, Ohlc ohlc) {
    this(instrument, timeFrame, time, ohlc.getAskOpen(), ohlc.getBidOpen(), ohlc.getAskHigh(),
        ohlc.getBidHigh(), ohlc.getAskLow(), ohlc.getBidLow(), ohlc.getAskClose(),
        ohlc.getBidClose());
  }

  public PriceBar(String instrument, String timeFrame, long time, double askOpen, double bidOpen,
      double askHigh, double bidHigh, double askLow, double bidLow, double askClose,
      double bidClose) {
    this.instrument = instrument;
    this.timeFrame = timeFrame;
    this.time = time;
    this.askOpen = askOpen;
    this.bidOpen = bidOpen;
    this.askHigh = askHigh;
    this.bidHigh = bidHigh;
    this.askLow = askLow;
    this.bidLow = bidLow;
    this.askClose = askClose;
    this.bidClose = bidClose;
  }

  @Override
  public String getInstrument() {
    return instrument;
  }

  @Override
  public String getTimeFrame() {
    return timeFrame;
  }

  @Override
  public long getTime() {
    return time;
  }

  @Override
  public double getAskOpen() {
    return askOpen;
  }

  @Override
  public double getBidOpen() {
    return bidOpen;
  }

  @Override
  public double getAskHigh() {
    return askHigh;
  }

  @Override
  public double getBidHigh() {
    return bidHigh;
  }

  @Override
  public double getAskLow() {
    return askLow;
  }

  @Override
  public double getBidLow() {
    return bidLow;
  }

  @Override
  public double getAskClose() {
    return askClose;
  }

  @Override
  public double getBidClose() {
    return bidClose;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    PriceBar priceBar = (PriceBar) other;
    return time == priceBar.time &&
        Double.compare(priceBar.askOpen, askOpen) == 0 &&
        Double.compare(priceBar.bidOpen, bidOpen) == 0 &&
        Double.compare(priceBar.askHigh, askHigh) == 0 &&
        Double.compare(priceBar.bidHigh, bidHigh) == 0 &&
        Double.compare(priceBar.askLow, askLow) == 0 &&
        Double.compare(priceBar.bidLow, bidLow) == 0 &&
        Double.compare(priceBar.askClose, askClose) == 0 &&
        Double.compare(priceBar.bidClose, bidClose) == 0 &&
        Objects.equals(instrument, priceBar.instrument) &&
        Objects.equals(timeFrame, priceBar.timeFrame);
  }

  @Override
  public int hashCode() {
    return Objects.hash(instrument, timeFrame, time, askOpen, bidOpen, askHigh, bidHigh, askLow,
        bidLow, askClose, bidClose);
  }
}
