package io.algobox.microservice.container;

import io.algobox.microservice.container.context.AppContext;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;

public class ContainerModule extends AbstractBinder {
  private final Factory<AppContext> appContextFactory;

  public ContainerModule(AppContext appContext) {
    this.appContextFactory = new ApplicationContextFactory(appContext);
  }

  @Override
  protected void configure() {
    bindFactory(appContextFactory).to(AppContext.class).in(Singleton.class);
  }

  private class ApplicationContextFactory implements Factory<AppContext> {
    private final AppContext appContext;

    public ApplicationContextFactory(AppContext appContext) {
      this.appContext = checkNotNull(appContext);
    }

    @Override
    public AppContext provide() {
      return appContext;
    }

    @Override
    public void dispose(AppContext instance) {
      // Intentionally empty.
    }
  }
}
