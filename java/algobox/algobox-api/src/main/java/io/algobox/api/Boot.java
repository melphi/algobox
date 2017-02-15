package io.algobox.api;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import io.algobox.api.container.ContextParameters;
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
import io.algobox.microservice.container.context.EnvironmentAppContext;
import io.swagger.models.Info;
import org.glassfish.hk2.utilities.Binder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public final class Boot {
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
    AppContext appContext = new EnvironmentAppContext();
    int port = appContext.getRequiredInt(ContextParameters.APPLICATION_PORT);
    MicroServiceBuilder builder = MicroServiceBuilder.newBuilder()
        .withAppContext(appContext)
        .withPort(port)
        .withBinders(BINDERS)
        .withRestControllers(CONTROLLERS);
    if (appContext.getBoolean(ContextParameters.APPLICATION_ENABLE_SWAGGER)) {
      Info info = new Info()
          .title("Algobox Trading API")
          .version("1");
      URI apiPath = new URI(appContext.getRequiredValue(ContextParameters.APPLICATION_API_URL));
      builder.withSwagger(info, apiPath);
    }
    MicroService microService = builder.build();
    microService.start();
  }
}
