package io.algobox.api.module.indicator;

import io.algobox.api.module.indicator.dao.PriceOhlcCacheDao;
import io.algobox.api.module.indicator.dao.impl.PriceOhlcCacheDaoImpl;
import io.algobox.api.module.indicator.service.impl.IndicatorServiceImpl;
import io.algobox.indicator.IndicatorService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

public class IndicatorModule extends AbstractBinder {
  @Override
  protected void configure() {
    bind(PriceOhlcCacheDaoImpl.class).to(PriceOhlcCacheDao.class).in(Singleton.class);
    bind(IndicatorServiceImpl.class).to(IndicatorService.class).in(Singleton.class);
  }
}
