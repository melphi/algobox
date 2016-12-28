package io.algobox.api.module.instrument;

import io.algobox.instrument.InstrumentService;
import io.algobox.api.module.instrument.service.impl.InstrumentServiceImpl;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

public class InstrumentModule extends AbstractBinder {
  @Override
  protected void configure() {
    bind(InstrumentServiceImpl.class).to(InstrumentService.class).in(Singleton.class);
  }
}
