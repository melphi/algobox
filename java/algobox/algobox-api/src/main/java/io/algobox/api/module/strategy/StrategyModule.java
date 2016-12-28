package io.algobox.api.module.strategy;

import io.algobox.api.module.strategy.dao.impl.StrategyEventDao;
import io.algobox.api.module.strategy.dao.impl.StrategyHistoryDao;
import io.algobox.api.module.strategy.dao.impl.StrategyRegistrationDao;
import io.algobox.api.module.strategy.dao.impl.impl.StrategyEventDaoImpl;
import io.algobox.api.module.strategy.dao.impl.impl.StrategyHistoryDaoImpl;
import io.algobox.api.module.strategy.dao.impl.impl.StrategyRegistrationDaoImpl;
import io.algobox.api.module.strategy.service.StrategyManager;
import io.algobox.api.module.strategy.service.StrategyService;
import io.algobox.api.module.strategy.service.impl.StrategyEventServiceImpl;
import io.algobox.api.module.strategy.service.impl.StrategyManagerImpl;
import io.algobox.api.module.strategy.service.impl.StrategyServiceImpl;
import io.algobox.strategy.StrategyEventService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

public class StrategyModule extends AbstractBinder {
  @Override
  protected void configure() {
    bind(StrategyEventDaoImpl.class).to(StrategyEventDao.class).in(Singleton.class);
    bind(StrategyHistoryDaoImpl.class).to(StrategyHistoryDao.class).in(Singleton.class);
    bind(StrategyRegistrationDaoImpl.class).to(StrategyRegistrationDao.class).in(Singleton.class);
    bind(StrategyEventServiceImpl.class).to(StrategyEventService.class).in(Singleton.class);
    bind(StrategyManagerImpl.class).to(StrategyManager.class).in(Singleton.class);
    bind(StrategyServiceImpl.class).to(StrategyService.class).in(Singleton.class);
  }
}
