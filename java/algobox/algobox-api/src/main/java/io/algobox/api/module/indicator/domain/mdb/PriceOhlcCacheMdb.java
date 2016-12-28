package io.algobox.api.module.indicator.domain.mdb;

import io.algobox.price.Ohlc;

public final class PriceOhlcCacheMdb implements Ohlc {
  public static final String COLLECTION_PRICE_OHLC_CACHE = "priceOhlcCache";
  public static final String FIELD_INSTRUMENT = "instrument";
  public static final String FIELD_FROM_TIME = "fromTime";
  public static final String FIELD_TO_TIME = "toTime";

  private String instrument;

  private double askOpen;

  private double bidOpen;

  private double askHigh;

  private double bidHigh;

  private double askLow;

  private double bidLow;

  private double askClose;

  private double bidClose;

  private long fromTime;

  private long toTime;

  private long createdOn;

  public PriceOhlcCacheMdb() {
    // Intentionally empty.
  }

  public PriceOhlcCacheMdb(Ohlc ohlc, long fromTime, long toTime, long createdOn) {
    this(ohlc.getInstrument(), ohlc.getAskOpen(), ohlc.getBidOpen(), ohlc.getAskHigh(),
        ohlc.getBidHigh(), ohlc.getAskLow(), ohlc.getBidLow(), ohlc.getAskClose(),
        ohlc.getBidClose(), fromTime, toTime, createdOn);
  }

  private PriceOhlcCacheMdb(String instrument, double askOpen, double bidOpen, double askHigh,
      double bidHigh, double askLow, double bidLow, double askClose, double bidClose,
      long fromTime, long toTime, long createdOn) {
    this.instrument = instrument;
    this.askOpen = askOpen;
    this.bidOpen = bidOpen;
    this.askHigh = askHigh;
    this.bidHigh = bidHigh;
    this.askLow = askLow;
    this.bidLow = bidLow;
    this.askClose = askClose;
    this.bidClose = bidClose;
    this.fromTime = fromTime;
    this.toTime = toTime;
    this.createdOn = createdOn;
  }

  @Override
  public String getInstrument() {
    return instrument;
  }

  public void setInstrument(String instrument) {
    this.instrument = instrument;
  }

  @Override
  public double getAskOpen() {
    return askOpen;
  }

  public void setAskOpen(double askOpen) {
    this.askOpen = askOpen;
  }

  @Override
  public double getBidOpen() {
    return bidOpen;
  }

  public void setBidOpen(double bidOpen) {
    this.bidOpen = bidOpen;
  }

  @Override
  public double getAskHigh() {
    return askHigh;
  }

  public void setAskHigh(double askHigh) {
    this.askHigh = askHigh;
  }

  @Override
  public double getBidHigh() {
    return bidHigh;
  }

  public void setBidHigh(double bidHigh) {
    this.bidHigh = bidHigh;
  }

  @Override
  public double getAskLow() {
    return askLow;
  }

  public void setAskLow(double askLow) {
    this.askLow = askLow;
  }

  @Override
  public double getBidLow() {
    return bidLow;
  }

  public void setBidLow(double bidLow) {
    this.bidLow = bidLow;
  }

  @Override
  public double getAskClose() {
    return askClose;
  }

  public void setAskClose(double askClose) {
    this.askClose = askClose;
  }

  @Override
  public double getBidClose() {
    return bidClose;
  }

  public void setBidClose(double bidClose) {
    this.bidClose = bidClose;
  }

  public long getFromTime() {
    return fromTime;
  }

  public void setFromTime(long fromTime) {
    this.fromTime = fromTime;
  }

  public long getToTime() {
    return toTime;
  }

  public void setToTime(long toTime) {
    this.toTime = toTime;
  }

  public long getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(long createdOn) {
    this.createdOn = createdOn;
  }
}
