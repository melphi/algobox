package io.algobox.microservice;

import com.google.common.collect.ImmutableList;
import io.algobox.microservice.container.context.AppContext;
import io.algobox.microservice.container.context.BootConfigurator;
import io.algobox.microservice.impl.AbstractMicroService;
import io.algobox.microservice.impl.SwaggerConfiguration;
import io.algobox.microservice.impl.grizzly.GrizzlyMicroService;
import io.swagger.models.Info;
import org.glassfish.hk2.utilities.Binder;

import java.net.URI;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class MicroServiceBuilder {
  public static final int DEFAULT_PORT = 8080;

  private Collection<Class<?>> restControllers = ImmutableList.of();
  private Collection<Binder> binders = ImmutableList.of();
  private SwaggerConfiguration swaggerConfiguration = null;
  private Class<? extends AbstractMicroService> implementationClass = GrizzlyMicroService.class;
  private AppContext appContext;
  private int port = DEFAULT_PORT;

  private MicroServiceBuilder() {
    // Intentionally empty.
  }

  public static MicroServiceBuilder newBuilder() {
    return new MicroServiceBuilder();
  }

  public MicroServiceBuilder withAppContext(AppContext appContext) {
    checkNotNull(appContext);
    this.appContext = appContext;
    return this;
  }

  public MicroServiceBuilder withRestControllers(Collection<Class<?>> controllers) {
    checkNotNull(controllers);
    this.restControllers = ImmutableList.copyOf(controllers);
    return this;
  }

  public MicroServiceBuilder withBinders(Collection<Binder> binders) {
    checkNotNull(binders);
    this.binders = ImmutableList.copyOf(binders);
    return this;
  }

  public MicroServiceBuilder withPort(int port) {
    checkArgument(port > 0, "Port should be positive.");
    this.port = port;
    return this;
  }

  public MicroServiceBuilder withSwagger(Info info, URI apiUrl) {
    checkNotNull(info, "Info can no be null.");
    this.swaggerConfiguration = new SwaggerConfiguration(info, apiUrl);
    return this;
  }

  public MicroServiceBuilder withSwagger(Info info) {
    return withSwagger(info, null);
  }

  public MicroServiceBuilder withImplementationClass(
      Class<? extends AbstractMicroService> implementation) {
    this.implementationClass = checkNotNull(implementation);
    return this;
  }

  public MicroService build() {
    try {
      BootConfigurator.getInstance().setConfiguration(
          restControllers, binders, port, swaggerConfiguration, appContext);
      return implementationClass.newInstance();
    } catch (Exception e) {
      throw new IllegalArgumentException(String.format(
          "Can not instantiate [%s] as micro service implementationClass. Reason: [%s].",
          this.implementationClass.getName(), e.getMessage()), e);
    }
  }
}
