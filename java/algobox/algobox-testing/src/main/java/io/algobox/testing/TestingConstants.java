package io.algobox.testing;

import com.google.common.collect.ImmutableList;
import io.algobox.price.PriceBar;
import io.algobox.price.PriceTick;
import io.algobox.price.StandardTimeFrame;
import io.algobox.price.feed.PriceFeed;
import io.algobox.price.feed.ResourcesPriceFeed;
import io.algobox.strategy.InstrumentMapping;
import io.algobox.util.DateTimeUtils;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collection;

public final class TestingConstants {
  public static final String DEFAULT_CONNECTION_ID = "connectionId1";
  public static final String DEFAULT_INSTRUMENT_DAX = "DAX";
  public static final String DEFAULT_INSTRUMENT_EURUSD = "EURUSD";
  public static final long DEFAULT_FROM_TIMESTAMP = 1481094056000L;
  public static final long DEFAULT_TO_TIMESTAMP = 1481107076000L;

  public static final PriceTick DEFAULT_PRICE_TICK_1 =
      new PriceTick(DEFAULT_INSTRUMENT_DAX, 123, 1.000002, 1.000001);
  public static final PriceTick DEFAULT_PRICE_TICK_2 =
      new PriceTick(DEFAULT_INSTRUMENT_EURUSD, 124, 1.100004, 1.100003);
  public static final PriceTick DEFAULT_PRICE_TICK_3 =
      new PriceTick(DEFAULT_INSTRUMENT_DAX, 125, 1235, 1234);

  public static final InstrumentMapping DEFAULT_INSTRUMENT_MAPPING = new InstrumentMapping(
      DEFAULT_CONNECTION_ID, DEFAULT_INSTRUMENT_DAX, DEFAULT_CONNECTION_ID, DEFAULT_INSTRUMENT_DAX);
  public static final Collection<InstrumentMapping> DEFAULT_INSTRUMENT_MAPPINGS =
      ImmutableList.of(DEFAULT_INSTRUMENT_MAPPING);

  public static ZonedDateTime DEFAULT_DATE = ZonedDateTime.of(
      2016, 2, 22, 9, 0, 0, 0, ZoneOffset.UTC);
  public static final long DEFAULT_TIMESTAMP = DateTimeUtils.getUtcMilliseconds(DEFAULT_DATE);
  public static final String FILE_DAX_SAMPLE_SMALL = "dax_ticks_small_sample.csv";
  public static final String FILE_DAX_SAMPLE_MORNING = "dax_ticks_morning_22.02.2016.csv";
  public static final String FILE_DAX_SAMPLE_TWO_DAYS = "dax_ticks_small_sample_two_days.csv";
  public static final String FILE_DAX_SAMPLE_PRE_MARKET =
      "dax_ticks_small_sample_with_premarket.csv";

  public static final PriceFeed PRICES_FEED_DAX_MORNING =
      new ResourcesPriceFeed(FILE_DAX_SAMPLE_MORNING, DEFAULT_INSTRUMENT_DAX);
  public static final PriceFeed PRICES_FEED_DAX_SMALL =
      new ResourcesPriceFeed(FILE_DAX_SAMPLE_SMALL, DEFAULT_INSTRUMENT_DAX);
  public static final PriceFeed PRICES_FEED_DAX_TWO_DAYS =
      new ResourcesPriceFeed(FILE_DAX_SAMPLE_TWO_DAYS, DEFAULT_INSTRUMENT_DAX);
  public static final PriceFeed PRICES_FEED_DAX_PRE_MARKET =
      new ResourcesPriceFeed(FILE_DAX_SAMPLE_PRE_MARKET, DEFAULT_INSTRUMENT_DAX);

  public static PriceTick DEFAULT_PRICE_TICK = new PriceTick(
      DEFAULT_INSTRUMENT_DAX, DEFAULT_TIMESTAMP, 9570, 9569);

  public static final PriceBar DEFAULT_PRICE_BAR = new PriceBar(DEFAULT_INSTRUMENT_DAX,
      StandardTimeFrame.M15.getValue(), DEFAULT_TIMESTAMP, 9570, 9569, 9580, 9579, 9568, 9567,
      9578, 9577);
  public static final PriceBar DEFAULT_PRICE_BAR_SMALL = new PriceBar(DEFAULT_INSTRUMENT_DAX,
      StandardTimeFrame.M15.getValue(), DEFAULT_TIMESTAMP, 9570, 9569, 9872, 9871, 9568, 9567,
      9578, 9577);
  public static final PriceBar DEFAULT_PRICE_BAR_BIG = new PriceBar(DEFAULT_INSTRUMENT_DAX,
      StandardTimeFrame.M15.getValue(), DEFAULT_TIMESTAMP, 9570, 9569, 9600, 9599, 9568, 9567,
      9578, 9577);
}
