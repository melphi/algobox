package io.algobox.api;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import io.algobox.api.container.PersistenceModule;
import io.algobox.api.module.connector.ConnectorModule;
import io.algobox.api.module.connector.rest.ConnectionController;
import io.algobox.api.module.indicator.IndicatorModule;
import io.algobox.api.module.indicator.rest.IndicatorController;
import io.algobox.api.module.instrument.InstrumentModule;
import io.algobox.api.module.instrument.rest.InstrumentController;
import io.algobox.api.module.notification.NotificationModule;
import io.algobox.api.module.order.OrderModule;
import io.algobox.api.module.order.rest.OrderController;
import io.algobox.api.module.order.rest.TradesController;
import io.algobox.api.module.price.PriceModule;
import io.algobox.api.module.price.rest.PriceController;
import io.algobox.api.module.strategy.StrategyModule;
import io.algobox.api.module.strategy.rest.StrategyController;
import io.algobox.microservice.MicroService;
import io.algobox.microservice.MicroServiceBuilder;
import io.algobox.microservice.container.context.AppContext;
import io.algobox.microservice.container.context.ConsulAppContext;
import io.swagger.models.Info;
import org.glassfish.hk2.utilities.Binder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public final class Boot {
  private static final String SERVICE_NAME = "api";
  private static final String DEFAULT_CONSUL_SERVER_HOST = "127.0.0.1";
  private static final String ENVIRONMENT_CONSUL_CLIENT_HOST = "CONSUL_CLIENT_HOST";

  private static final Set<Class<?>> CONTROLLERS = ImmutableSet.of(
      ConnectionController.class,
      IndicatorController.class,
      InstrumentController.class,
      OrderController.class,
      PriceController.class,
      StrategyController.class,
      TradesController.class);
  private static final Collection<Binder> BINDERS = ImmutableList.of(
      new ConnectorModule(),
      new IndicatorModule(),
      new InstrumentModule(),
      new NotificationModule(),
      new OrderModule(),
      new PersistenceModule(),
      new PriceModule(),
      new StrategyModule());

  public static void main(String[] args)
      throws IOException, ExecutionException, InterruptedException, URISyntaxException {
    AppContext appContext = getAppContext();
    int port = appContext.getRequiredInt("application.port");
    MicroServiceBuilder builder = MicroServiceBuilder.newBuilder()
        .withAppContext(appContext)
        .withPort(port)
        .withBinders(BINDERS)
        .withRestControllers(CONTROLLERS);
    if (appContext.getBoolean("application.enableSwagger")) {
      Info info = new Info().title("Algobox Trading API")
          .version("1");
      URI apiPath = new URI(appContext.getRequiredValue("application.apiUrl"));
      builder.withSwagger(info, apiPath);
    }
    MicroService microService = builder.build();
    microService.start();
  }

  private static AppContext getAppContext() {
    if (Strings.isNullOrEmpty(System.getenv(ENVIRONMENT_CONSUL_CLIENT_HOST))) {
      return new ConsulAppContext(DEFAULT_CONSUL_SERVER_HOST, true, SERVICE_NAME);
    } else {
      return new ConsulAppContext(
          System.getenv(ENVIRONMENT_CONSUL_CLIENT_HOST), false, SERVICE_NAME);
    }
  }
}
