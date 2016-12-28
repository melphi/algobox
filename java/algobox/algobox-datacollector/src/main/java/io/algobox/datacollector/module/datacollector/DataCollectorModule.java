package io.algobox.datacollector.module.datacollector;

import io.algobox.datacollector.module.datacollector.service.DataCollectorService;
import io.algobox.datacollector.module.datacollector.service.impl.DataCollectorServiceImpl;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

public class DataCollectorModule extends AbstractBinder {
  @Override
  protected void configure() {
    bind(DataCollectorServiceImpl.class).to(DataCollectorService.class).in(Singleton.class);
  }
}
