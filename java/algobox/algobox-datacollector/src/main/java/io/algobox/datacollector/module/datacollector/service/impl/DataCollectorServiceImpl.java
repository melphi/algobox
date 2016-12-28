package io.algobox.datacollector.module.datacollector.service.impl;

import io.algobox.datacollector.module.datacollector.service.DataCollectorService;
import io.algobox.datacollector.module.pricestage.dao.PriceTickStageDao;
import io.algobox.price.PriceTick;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

@Service
public final class DataCollectorServiceImpl implements DataCollectorService {
  private static final Logger LOGGER = LoggerFactory.getLogger(DataCollectorServiceImpl.class);

  private final PriceTickStageDao priceTickDao;

  @Inject
  public DataCollectorServiceImpl(PriceTickStageDao priceTickDao) {
    this.priceTickDao = priceTickDao;
  }

  @Override
  public void onPriceTick(String source, PriceTick priceTick) {
    priceTickDao.save(priceTick, source);
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace(String.format("[%s]: [%s].", source, priceTick));
    }
  }
}
