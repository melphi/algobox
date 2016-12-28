package io.algobox.api.module.indicator.dao;

import io.algobox.price.Ohlc;

import java.util.Optional;

public interface PriceOhlcCacheDao {
  Optional<Ohlc> getPriceOhlc(String instrumentId, Long fromTimestamp, Long toTimestamp);

  void saveOrUpdatePriceOhlc(Ohlc ohlc, Long fromTimestamp, Long toTimestamp);
}
