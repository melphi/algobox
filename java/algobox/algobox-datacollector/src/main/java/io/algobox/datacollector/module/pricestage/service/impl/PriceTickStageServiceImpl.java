package io.algobox.datacollector.module.pricestage.service.impl;

import io.algobox.datacollector.module.pricestage.dao.PriceTickStageDao;
import io.algobox.datacollector.module.pricestage.domain.PriceTickStage;
import io.algobox.datacollector.module.pricestage.service.PriceTickStageService;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;

@Service
public final class PriceTickStageServiceImpl implements PriceTickStageService {
  private final PriceTickStageDao priceTickStageDao;

  @Inject
  public PriceTickStageServiceImpl(PriceTickStageDao priceTickStageDao) {
    this.priceTickStageDao = priceTickStageDao;
  }

  @Override
  public long countPrices(String instrumentId) {
    return priceTickStageDao.count(instrumentId);
  }

  @Override
  public PriceTickStage findLastPrice(String instrumentId) {
    return priceTickStageDao.findLast(instrumentId);
  }
}
