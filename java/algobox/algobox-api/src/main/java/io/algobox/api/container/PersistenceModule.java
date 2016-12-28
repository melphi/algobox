package io.algobox.api.container;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import io.algobox.microservice.container.context.AppContext;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

public class PersistenceModule extends AbstractBinder {
  private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceModule.class);

  @Override
  protected void configure() {
    bindFactory(MongoDatabaseFactory.class).to(MongoDatabase.class).in(Singleton.class);
  }

  private static class MongoDatabaseFactory implements Factory<MongoDatabase> {
    private AppContext appContext;

    @Inject
    public MongoDatabaseFactory(AppContext appContext) {
      this.appContext = appContext;
    }

    @Override
    public MongoDatabase provide() {
      String connectionUrl = appContext.getRequiredValue("mongo.connectionUrl");
      String databaseName = appContext.getRequiredValue("mongo.database");
      LOGGER.info(
          String.format("Connecting to MongoDb [%s] database [%s].", connectionUrl, databaseName));
      try {
        MongoClient mongoClient = new MongoClient(new MongoClientURI(connectionUrl));
        return mongoClient.getDatabase(databaseName);
      } catch (Exception e) {
        LOGGER.error(String.format("Connection error to MongoDb: [%s].", e.getMessage()), e);
        throw e;
      }
    }

    @Override
    public void dispose(MongoDatabase instance) {
      // Intentionally empty.
    }
  }
}
