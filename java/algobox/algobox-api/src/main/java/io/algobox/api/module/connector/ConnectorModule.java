package io.algobox.api.module.connector;

import com.mongodb.client.MongoDatabase;
import io.algobox.connector.dao.ConnectionInstrumentSubscriptionDao;
import io.algobox.connector.dao.ConnectionRegistrationDao;
import io.algobox.connector.dao.impl.ConnectionInstrumentSubscriptionDaoImpl;
import io.algobox.connector.dao.impl.ConnectionRegistrationDaoImpl;
import io.algobox.connector.service.ConnectorManager;
import io.algobox.connector.service.ConnectorService;
import io.algobox.connector.service.impl.ConnectorManagerImpl;
import io.algobox.connector.service.impl.ConnectorServiceImpl;
import io.algobox.microservice.container.context.AppContext;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.Immediate;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

public class ConnectorModule extends AbstractBinder {
  private static final Logger LOGGER = LoggerFactory.getLogger(ConnectorModule.class);

  @Override
  protected void configure() {
    bindFactory(ConnectorServiceFactory.class).to(ConnectorService.class).in(Singleton.class);
    bind(ConnectorListener.class).in(Immediate.class);
  }

  private static class ConnectorServiceFactory implements Factory<ConnectorService> {
    private final ConnectorService connectorService;

    @Inject
    public ConnectorServiceFactory(AppContext appContext, MongoDatabase mongoDatabase) {
      this.connectorService = createConnectorService(appContext, mongoDatabase);
    }

    @Override
    public ConnectorService provide() {
      return connectorService;
    }

    @Override
    public void dispose(ConnectorService instance) {
      // Intentionally empty.
    }

    private ConnectorService createConnectorService(
        AppContext appContext, MongoDatabase mongoDatabase) {
      ConnectorManager connectorManager = new ConnectorManagerImpl(appContext.getAllValues());
      ConnectionRegistrationDao connectorKeepAliveDao =
          new ConnectionRegistrationDaoImpl(mongoDatabase);
      ConnectionInstrumentSubscriptionDao connectorInstrumentSubscriptionDao =
          new ConnectionInstrumentSubscriptionDaoImpl(mongoDatabase);
      try {
        return new ConnectorServiceImpl(
            connectorManager, connectorKeepAliveDao, connectorInstrumentSubscriptionDao);
      } catch (Exception e) {
        LOGGER.error(String.format("Error while loading connectors [%s].", e.getMessage()), e);
        throw new IllegalArgumentException(e);
      }
    }
  }
}
