package io.algobox.microservice.container.context;

import com.google.common.base.Optional;
import io.algobox.microservice.impl.SwaggerConfiguration;
import org.glassfish.hk2.utilities.Binder;

import java.util.Collection;

/**
 * This class stores the objects used by the servlet during the startup. The parametrisation of
 * servlet is limited and this class provides a handy helper during initialization.
 */
public final class BootConfigurator {
  private static final BootConfigurator INSTANCE = new BootConfigurator();

  private Collection<Class<?>> restControllers;
  private Collection<Binder> binders;
  private SwaggerConfiguration swaggerConfiguration;
  private AppContext appContext;
  private int port;

  public static BootConfigurator getInstance() {
    return INSTANCE;
  }

  public synchronized void setConfiguration(
      Collection<Class<?>> restControllers, Collection<Binder> binders, int port,
      SwaggerConfiguration swaggerConfiguration, AppContext appContext) {
    this.restControllers = restControllers;
    this.binders = binders;
    this.port = port;
    this.swaggerConfiguration = swaggerConfiguration;
    this.appContext = appContext;
  }

  public synchronized Collection<Class<?>> getRestControllers() {
    return restControllers;
  }

  public synchronized Collection<Binder> getBinders() {
    return binders;
  }

  public synchronized Optional<SwaggerConfiguration> getSwaggerConfiguration() {
    return Optional.fromNullable(swaggerConfiguration);
  }

  public synchronized int getPort() {
    return port;
  }

  public synchronized Optional<AppContext> getAppContext() {
    return Optional.fromNullable(appContext);
  }
}
