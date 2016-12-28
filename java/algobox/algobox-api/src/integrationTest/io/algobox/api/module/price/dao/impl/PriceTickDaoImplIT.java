package io.algobox.api.module.price.dao.impl;

import com.google.common.collect.Iterables;
import com.mongodb.client.MongoDatabase;
import io.algobox.api.AbstractMongoIT;
import io.algobox.api.IntegrationTestConstants;
import io.algobox.api.module.price.dao.PriceTickDao;
import io.algobox.price.PriceTick;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PriceTickDaoImplIT extends AbstractMongoIT<PriceTickDao> {
  private static final String DEFAULT_SOURCE = "source1";

  @Test
  public void testFind() throws Exception {
    dao.save(IntegrationTestConstants.DEFAULT_PRICE_TICK_1, DEFAULT_SOURCE);
    dao.save(IntegrationTestConstants.DEFAULT_PRICE_TICK_2, DEFAULT_SOURCE);
    PriceTick expectedTick = IntegrationTestConstants.DEFAULT_PRICE_TICK_1;
    PriceTick actualTick = Iterables.getOnlyElement(
        dao.find(expectedTick.getInstrument(), expectedTick.getTime(), expectedTick.getTime() + 1));
    assertEquals(expectedTick.getInstrument(), actualTick.getInstrument());
    assertEquals(expectedTick.getAsk(), actualTick.getAsk(), 0);
    assertEquals(expectedTick.getBid(), actualTick.getBid(), 0);
    assertEquals(expectedTick.getTime(), actualTick.getTime());
  }

  @Override
  protected PriceTickDao createDao(MongoDatabase mongoDatabase) {
    return new PriceTickDaoImpl(mongoDatabase);
  }
}
