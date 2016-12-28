package io.algobox.api.module.price;

import io.algobox.api.module.price.dao.PriceTickDao;
import io.algobox.api.module.price.dao.impl.PriceTickDaoImpl;
import io.algobox.api.module.price.service.impl.PriceServiceImpl;
import io.algobox.price.PriceService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

public class PriceModule extends AbstractBinder {
  @Override
  protected void configure() {
    bind(PriceTickDaoImpl.class).to(PriceTickDao.class).in(Singleton.class);
    bind(PriceServiceImpl.class).to(PriceService.class).in(Singleton.class);
  }
}
