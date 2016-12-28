package io.algobox.datacollector.module.pricestage.domain.mdb;

import io.algobox.datacollector.module.pricestage.domain.PriceTickStage;

public final class PriceTickStageMdb implements PriceTickStage {
  public static final String COLLECTION_PRICE_TICKS_STAGE = "priceTicksStage";
  public static final String FIELD_ASK = "ask";
  public static final String FIELD_BID = "bid";
  public static final String FIELD_ID = "_id";
  public static final String FIELD_INSTRUMENT = "instrument";
  public static final String FIELD_SRC = "src";
  public static final String FIELD_TIME = "time";

  private final String instrument;

  private final long time;

  private final double ask;

  private final double bid;

  private final String src;

  public PriceTickStageMdb(String instrument, long time, double ask, double bid, String src) {
    this.instrument = instrument;
    this.time = time;
    this.ask = ask;
    this.bid = bid;
    this.src = src;
  }

  @Override
  public String getInstrument() {
    return instrument;
  }

  @Override
  public long getTime() {
    return time;
  }

  @Override
  public double getAsk() {
    return ask;
  }

  @Override
  public double getBid() {
    return bid;
  }

  @Override
  public String getSrc() {
    return src;
  }
}
