package io.algobox.datacollector.module.pricestage.service;

import io.algobox.datacollector.module.pricestage.domain.PriceTickStage;

public interface PriceTickStageService {
  long countPrices(String instrumentId);

  PriceTickStage findLastPrice(String instrumentId);
}
