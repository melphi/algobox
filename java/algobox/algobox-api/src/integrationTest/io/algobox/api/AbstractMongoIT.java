package io.algobox.api;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import org.junit.Before;

public abstract class AbstractMongoIT<T> {
  protected T dao;

  @Before
  public void init() {
    MongoClient mongoClient = new MongoClient(new MongoClientURI(
        IntegrationTestConstants.DEFAULT_MONGO_CONNECTION_URL));
    MongoDatabase mongoDatabase = mongoClient.getDatabase(
        IntegrationTestConstants.DEFAULT_MONGO_DATABASE);
    mongoDatabase.drop();
    this.dao = createDao(mongoDatabase);
  }

  protected abstract T createDao(MongoDatabase mongoDatabase);
}
