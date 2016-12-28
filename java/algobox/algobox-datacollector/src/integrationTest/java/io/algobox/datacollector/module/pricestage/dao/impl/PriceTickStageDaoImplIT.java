package io.algobox.datacollector.module.pricestage.dao.impl;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import io.algobox.datacollector.IntegrationTestConstants;
import io.algobox.datacollector.module.pricestage.dao.PriceTickStageDao;
import io.algobox.datacollector.module.pricestage.domain.mdb.PriceTickStageMdb;
import io.algobox.price.PriceTick;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PriceTickStageDaoImplIT {
  private PriceTickStageDao priceTickStageDao;

  @Before
  public void init() {
    MongoClient mongoClient = new MongoClient(new MongoClientURI(
        IntegrationTestConstants.DEFAULT_MONGO_CONNECTION_URL));
    MongoDatabase mongoDatabase = mongoClient.getDatabase(
        IntegrationTestConstants.DEFAULT_MONGO_DATABASE);
    mongoDatabase.drop();
    priceTickStageDao = new PriceTickStageDaoImpl(mongoDatabase);
  }

  @Test
  public void findLast() throws Exception {
    priceTickStageDao.save(IntegrationTestConstants.DEFAULT_PRICE_TICK_1,
        IntegrationTestConstants.DEFAULT_SOURCE);
    priceTickStageDao.save(IntegrationTestConstants.DEFAULT_PRICE_TICK_2,
        IntegrationTestConstants.DEFAULT_SOURCE);
    PriceTickStageMdb priceTick = priceTickStageDao.findLast(
        IntegrationTestConstants.DEFAULT_PRICE_TICK_2.getInstrument());
    assertEquals(IntegrationTestConstants.DEFAULT_PRICE_TICK_2.getTime(), priceTick.getTime());
  }

  @Test
  public void count() throws Exception {
    priceTickStageDao.save(IntegrationTestConstants.DEFAULT_PRICE_TICK_1,
        IntegrationTestConstants.DEFAULT_SOURCE);
    priceTickStageDao.save(IntegrationTestConstants.DEFAULT_PRICE_TICK_2,
        IntegrationTestConstants.DEFAULT_SOURCE);
    long count = priceTickStageDao.count(
        IntegrationTestConstants.DEFAULT_PRICE_TICK_2.getInstrument());
    assertEquals(1, count);
  }

  @Test
  public void save() throws Exception {
    PriceTick priceTick = IntegrationTestConstants.DEFAULT_PRICE_TICK_1;
    priceTickStageDao.save(priceTick, IntegrationTestConstants.DEFAULT_SOURCE);
    PriceTickStageMdb savedPriceTick = priceTickStageDao.findLast(priceTick.getInstrument());
    assertEquals(priceTick.getInstrument(), savedPriceTick.getInstrument());
    assertEquals(priceTick.getTime(), savedPriceTick.getTime());
    assertEquals(priceTick.getAsk(), savedPriceTick.getAsk(), 0);
    assertEquals(priceTick.getBid(), savedPriceTick.getBid(), 0);
  }
}
