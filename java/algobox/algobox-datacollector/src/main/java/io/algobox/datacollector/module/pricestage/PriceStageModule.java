package io.algobox.datacollector.module.pricestage;

import io.algobox.datacollector.module.pricestage.dao.PriceTickStageDao;
import io.algobox.datacollector.module.pricestage.dao.impl.PriceTickStageDaoImpl;
import io.algobox.datacollector.module.pricestage.service.PriceTickStageService;
import io.algobox.datacollector.module.pricestage.service.impl.PriceTickStageServiceImpl;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

public class PriceStageModule extends AbstractBinder {
  @Override
  protected void configure() {
    bind(PriceTickStageDaoImpl.class).to(PriceTickStageDao.class).in(Singleton.class);
    bind(PriceTickStageServiceImpl.class).to(PriceTickStageService.class).in(Singleton.class);
  }
}
