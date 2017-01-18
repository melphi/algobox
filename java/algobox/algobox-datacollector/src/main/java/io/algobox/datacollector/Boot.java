package io.algobox.datacollector;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import io.algobox.datacollector.container.PersistenceModule;
import io.algobox.datacollector.module.connector.ConnectorModule;
import io.algobox.datacollector.module.connector.rest.ConnectionController;
import io.algobox.datacollector.module.datacollector.DataCollectorModule;
import io.algobox.datacollector.module.pricestage.PriceStageModule;
import io.algobox.datacollector.module.pricestage.rest.PriceStageController;
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
      PriceStageController.class);
  private static final Collection<Binder> BINDERS = ImmutableList.of(
      new ConnectorModule(),
      new DataCollectorModule(),
      new PersistenceModule(),
      new PriceStageModule());

  public static void main(String[] args)
      throws IOException, ExecutionException, InterruptedException, URISyntaxException {
    AppContext appContext = new EnvironmentAppContext();
    int port = appContext.getRequiredInt("application.port");
    MicroServiceBuilder builder = MicroServiceBuilder.newBuilder()
        .withAppContext(appContext)
        .withPort(port)
        .withBinders(BINDERS)
        .withRestControllers(CONTROLLERS);
    if (appContext.getBoolean("application.enableSwagger")) {
      Info info = new Info()
          .title("Algobox datacollector API")
          .version("1");
      URI apiPath = new URI(appContext.getRequiredValue("application.apiUrl"));
      builder.withSwagger(info, apiPath);
    }
    MicroService microService = builder.build();
    microService.start();
  }
}
