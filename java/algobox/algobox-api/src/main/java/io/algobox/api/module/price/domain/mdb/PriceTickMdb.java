package io.algobox.api.module.price.domain.mdb;

import io.algobox.price.Tick;

public final class PriceTickMdb implements Tick {
  public static final String COLLECTION_PRICE_TICKS_PREFIX = "priceTicks_";
  public static final String FIELD_ASK = "ask";
  public static final String FIELD_BID = "bid";
  public static final String FIELD_ID = "_id";
  public static final String FIELD_INSTRUMENT = "instrument";
  public static final String FIELD_SRC = "src";
  public static final String FIELD_TIME = "time";

  private String src;

  private String instrument;

  private long time;

  private double ask;

  private double bid;

  public PriceTickMdb(
      String src, String instrumentId, long timestampUtc, double askPrice, double bidPrice) {
    this.src = src;
    this.instrument = instrumentId;
    this.time = timestampUtc;
    this.ask = askPrice;
    this.bid = bidPrice;
  }

  public String getSrc() {
    return src;
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
}
