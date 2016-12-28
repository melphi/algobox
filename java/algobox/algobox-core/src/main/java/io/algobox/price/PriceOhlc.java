package io.algobox.price;

import java.util.Objects;

public final class PriceOhlc implements Ohlc {
  private final String instrument;

  private final double askOpen;

  private final double bidOpen;

  private final double askHigh;

  private final double bidHigh;

  private final double askLow;

  private final double bidLow;

  private final double askClose;

  private final double bidClose;

  public PriceOhlc(String instrumentId, double askOpen, double bidOpen, double askHigh,
      double bidHigh, double askLow, double bidLow, double askClose, double bidClose) {
    this.instrument = instrumentId;
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
    PriceOhlc priceOhlc = (PriceOhlc) other;
    return Objects.equals(instrument, instrument) &&
        Double.compare(priceOhlc.askOpen, askOpen) == 0 &&
        Double.compare(priceOhlc.bidOpen, bidOpen) == 0 &&
        Double.compare(priceOhlc.askHigh, askHigh) == 0 &&
        Double.compare(priceOhlc.bidHigh, bidHigh) == 0 &&
        Double.compare(priceOhlc.askLow, askLow) == 0 &&
        Double.compare(priceOhlc.bidLow, bidLow) == 0 &&
        Double.compare(priceOhlc.askClose, askClose) == 0 &&
        Double.compare(priceOhlc.bidClose, bidClose) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        instrument, askOpen, bidOpen, askHigh, bidHigh, askLow, bidLow, askClose, bidClose);
  }
}
