package io.algobox.datacollector.module.pricestage.dao;

import io.algobox.datacollector.module.pricestage.domain.mdb.PriceTickStageMdb;
import io.algobox.price.PriceTick;

public interface PriceTickStageDao {
  void save(PriceTick priceTick, String source);

  long count(String instrumentId);

  PriceTickStageMdb findLast(String instrumentId);
}
