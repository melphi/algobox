package io.algobox.indicator;

import io.algobox.price.PriceOhlc;

public interface IndicatorService {
  PriceOhlc getOhlc(String instrumentId, Long fromTimestamp, Long toTimestamp);
}
