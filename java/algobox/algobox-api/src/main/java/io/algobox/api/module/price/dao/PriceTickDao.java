package io.algobox.api.module.price.dao;

import io.algobox.price.PriceTick;

public interface PriceTickDao {
  void save(PriceTick priceTick, String source);

  Iterable<PriceTick> find(String instrumentId, long fromTimestampUtc, long toTimeStampUtc);
}
