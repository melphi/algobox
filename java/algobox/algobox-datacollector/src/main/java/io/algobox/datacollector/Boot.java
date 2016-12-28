package io.algobox.datacollector;

import com.google.common.base.Strings;
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
  private static final String SERVICE_NAME = "datacollector";
  private static final String DEFAULT_CONSUL_SERVER_HOST = "127.0.0.1";
  private static final String ENVIRONMENT_CONSUL_CLIENT_HOST = "CONSUL_CLIENT_HOST";

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
    AppContext appContext = getAppContext();
    int port = appContext.getRequiredInt("application.port");
    MicroServiceBuilder builder = MicroServiceBuilder.newBuilder()
        .withAppContext(appContext)
        .withPort(port)
        .withBinders(BINDERS)
        .withRestControllers(CONTROLLERS);
    if (appContext.getBoolean("application.enableSwagger")) {
      Info info = new Info().title("Algobox Trading API");
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
