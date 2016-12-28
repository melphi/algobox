package io.algobox.backtest.spark.client.impl;

import io.algobox.price.PriceService;
import io.algobox.price.PriceTick;
import io.algobox.price.feed.PriceFeed;
import io.algobox.price.feed.ResourcesPriceFeed;
import io.algobox.testing.TestingConstants;

import java.io.Serializable;

public class TestingPriceService implements PriceService, Serializable {
  private static final String FILE_DAX_SAMPLE_SMALL = "dax_ticks_small_sample.csv";
  private static final PriceFeed DEFAULT_PRICE_FEED =
      new ResourcesPriceFeed(FILE_DAX_SAMPLE_SMALL, TestingConstants.DEFAULT_INSTRUMENT_DAX);

  @Override
  public Iterable<PriceTick> getPriceTicks(
      String instrumentId, Long fromTimestampUtc, Long toTimestampUtc) {
    if (TestingConstants.DEFAULT_INSTRUMENT_DAX.equals(instrumentId)
        && TestingConstants.DEFAULT_FROM_TIMESTAMP == fromTimestampUtc
        && TestingConstants.DEFAULT_TO_TIMESTAMP == toTimestampUtc) {
      return DEFAULT_PRICE_FEED.getPrices();
    } else {
      throw new IllegalArgumentException("No testing prices available with these parameters.");
    }
  }
}
