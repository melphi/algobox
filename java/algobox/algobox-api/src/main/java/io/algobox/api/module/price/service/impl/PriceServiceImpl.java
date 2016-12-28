package io.algobox.api.module.price.service.impl;

import io.algobox.api.module.price.dao.PriceTickDao;
import io.algobox.price.PriceService;
import io.algobox.price.PriceTick;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.io.IOException;

@Service
public final class PriceServiceImpl implements PriceService {
  private final PriceTickDao priceTickDao;

  @Inject
  public PriceServiceImpl(PriceTickDao priceTickDao) throws IOException {
    this.priceTickDao = priceTickDao;
  }

  @Override
  public Iterable<PriceTick> getPriceTicks(
      String instrumentId, Long fromTimestampUtc, Long toTimestampUtc) {
    return priceTickDao.find(instrumentId, fromTimestampUtc, toTimestampUtc);
  }
}
