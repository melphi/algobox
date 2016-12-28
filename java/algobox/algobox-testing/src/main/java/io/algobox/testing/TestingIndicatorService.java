package io.algobox.testing;

import com.google.common.collect.ImmutableMap;
import io.algobox.common.domain.Triplet;
import io.algobox.indicator.IndicatorService;
import io.algobox.price.PriceOhlc;

import java.io.Serializable;
import java.util.Map;

public class TestingIndicatorService implements IndicatorService, Serializable {
  private final Map<Triplet<String, Long, Long>, PriceOhlc> OHLC_VALUES =
      ImmutableMap.<Triplet<String, Long, Long>, PriceOhlc>builder()
          .put(createKey(TestingConstants.DEFAULT_INSTRUMENT_DAX, 1455868800000L, 1455900300000L),
              createOhlc(TestingConstants.DEFAULT_INSTRUMENT_DAX, 9470, 9471, 9500, 9501, 9430,
                  9431, 9475, 9476))
          .put(createKey(TestingConstants.DEFAULT_INSTRUMENT_DAX, 1456128000000L, 1456159500000L),
              createOhlc(TestingConstants.DEFAULT_INSTRUMENT_DAX, 9471, 9472, 9501, 9502, 9431,
                  9432, 9476, 9477))
          .put(createKey(TestingConstants.DEFAULT_INSTRUMENT_DAX, 1473922800000L, 1473954300000L),
              createOhlc(TestingConstants.DEFAULT_INSTRUMENT_DAX, 10367.45, 10366.55, 10454.95,
                  10454.05, 10335.45, 10334.55, 10425.95, 10425.05))
          .build();

  @Override
  public PriceOhlc getOhlc(String instrumentId, Long fromTimestamp, Long toTimestamp) {
    Triplet<String, Long, Long> key = new Triplet<>(instrumentId, fromTimestamp, toTimestamp);
    return OHLC_VALUES.get(key);
  }

  private static PriceOhlc createOhlc(String instrumentId, double askOpen, double bidOpen,
      double askHigh, double bidHigh, double askLow, double bidLow, double askClose,
      double bidClose) {
    return new PriceOhlc(
        instrumentId, askOpen, bidOpen, askHigh, bidHigh, askLow, bidLow, askClose, bidClose);
  }

  private static Triplet<String, Long, Long> createKey(
      String instrumentId, Long fromTimestamp, Long toTimestamp) {
    return new Triplet<>(instrumentId, fromTimestamp, toTimestamp);
  }
}
