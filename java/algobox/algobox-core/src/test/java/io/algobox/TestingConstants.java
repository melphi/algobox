package io.algobox;

import io.algobox.price.PriceTick;
import io.algobox.price.feed.PriceFeed;
import io.algobox.price.feed.ResourcesPriceFeed;
import io.algobox.util.DateTimeUtils;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public final class TestingConstants {
  public static final String DEFAULT_CONNECTION_ID = "connectionId1";
  public static final String DEFAULT_INSTRUMENT_DAX = "DAX";

  public static ZonedDateTime DEFAULT_DATE = ZonedDateTime.of(
      2016, 2, 22, 9, 0, 0, 0, ZoneOffset.UTC);
  public static final long DEFAULT_TIMESTAMP = DateTimeUtils.getUtcMilliseconds(DEFAULT_DATE);
  public static final String FILE_DAX_SAMPLE_SMALL = "dax_ticks_small_sample.csv";
  public static final String FILE_DAX_SAMPLE_MORNING = "dax_ticks_morning_22.02.2016.csv";
  public static final String FILE_DAX_SAMPLE_TWO_DAYS = "dax_ticks_small_sample_two_days.csv";

  public static final PriceFeed PRICES_FEED_DAX_MORNING =
      new ResourcesPriceFeed(FILE_DAX_SAMPLE_MORNING, DEFAULT_INSTRUMENT_DAX);
  public static final PriceFeed PRICES_FEED_DAX_SMALL =
      new ResourcesPriceFeed(FILE_DAX_SAMPLE_SMALL, DEFAULT_INSTRUMENT_DAX);
  public static final PriceFeed PRICES_FEED_DAX_TWO_DAYS =
      new ResourcesPriceFeed(FILE_DAX_SAMPLE_TWO_DAYS, DEFAULT_INSTRUMENT_DAX);

  public static PriceTick DEFAULT_PRICE_TICK = new PriceTick(
      DEFAULT_INSTRUMENT_DAX, DEFAULT_TIMESTAMP, 9570, 9569);
}
