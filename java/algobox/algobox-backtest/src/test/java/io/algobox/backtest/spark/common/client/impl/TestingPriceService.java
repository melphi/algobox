package io.algobox.backtest.spark.common.client.impl;

import io.algobox.price.PriceService;
import io.algobox.price.PriceTick;
import io.algobox.testing.TestingConstants;

import java.io.Serializable;
import java.util.Iterator;

import static com.clearspring.analytics.util.Preconditions.checkArgument;
import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;

public class TestingPriceService implements PriceService, Serializable {
  private final boolean generatedPrices;

  public TestingPriceService() {
    this(false);
  }

  /**
   * Constructor.
   * @param generatedPrices When true prices are always generated, when false they are read from
   *                        a file price feed.
   */
  public TestingPriceService(boolean generatedPrices) {
    this.generatedPrices = generatedPrices;
  }

  @Override
  public Iterable<PriceTick> getPriceTicks(
      String instrumentId, Long fromTimestampUtc, Long toTimestampUtc) {
    checkNotNullOrEmpty(instrumentId);
    checkArgument(fromTimestampUtc > 0);
    checkArgument(toTimestampUtc > fromTimestampUtc);
    if (generatedPrices) {
      return generatePrices(instrumentId, fromTimestampUtc, toTimestampUtc);
    } else {
      return loadPrices(instrumentId, fromTimestampUtc, toTimestampUtc);
    }
  }

  private Iterable<PriceTick> generatePrices(
      final String instrumentId, final Long fromTimestampUtc, final Long toTimestampUtc) {
    return new Iterable<PriceTick>() {
      @Override
      public Iterator<PriceTick> iterator() {
        return new PricesGenerator(instrumentId, fromTimestampUtc, toTimestampUtc);
      }
    };
  }

  private Iterable<PriceTick> loadPrices(
      String instrumentId, Long fromTimestampUtc, Long toTimestampUtc) {
    if (TestingConstants.DEFAULT_INSTRUMENT_DAX.equals(instrumentId)
        && TestingConstants.DEFAULT_FROM_TIMESTAMP == fromTimestampUtc
        && TestingConstants.DEFAULT_TO_TIMESTAMP == toTimestampUtc) {
      return TestingConstants.PRICES_FEED_DAX_SMALL.getPrices();
    } else {
      throw new IllegalArgumentException("No testing prices available with these parameters.");
    }
  }

  private class PricesGenerator implements Iterator<PriceTick> {
    private static final long TIMESTAMP_INCREMENT_MILLISECONDS = 1000L;
    private static final double FIRST_PRICE = 1.000001;
    private static final double PRICE_INCREMENT = 0.000002;

    private final String instrumentId;
    private final long toTimestamp;

    private long nextTimestamp;
    private double nextPrice;

    public PricesGenerator(String instrumentId, long fromTimestamp, long toTimestamp) {
      this.instrumentId = instrumentId;
      this.toTimestamp = toTimestamp;
      this.nextTimestamp = fromTimestamp;
      this.nextPrice = FIRST_PRICE;
    }

    @Override
    public synchronized boolean hasNext() {
      return nextTimestamp <= toTimestamp;
    }

    @Override
    public synchronized PriceTick next() {
      PriceTick priceTick = new PriceTick(
          instrumentId, nextTimestamp, nextPrice, nextPrice + PRICE_INCREMENT);
      nextTimestamp += TIMESTAMP_INCREMENT_MILLISECONDS;
      nextPrice += TIMESTAMP_INCREMENT_MILLISECONDS;
      return priceTick;
    }
  }
}
