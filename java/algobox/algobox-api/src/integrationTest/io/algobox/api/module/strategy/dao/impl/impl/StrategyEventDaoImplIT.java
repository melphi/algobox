package io.algobox.api.module.strategy.dao.impl.impl;

import com.mongodb.client.MongoDatabase;
import io.algobox.api.AbstractMongoIT;
import io.algobox.api.IntegrationTestConstants;
import io.algobox.api.module.strategy.dao.impl.StrategyEventDao;
import io.algobox.strategy.StrategyEvent;
import io.algobox.strategy.StrategyEventDto;
import io.algobox.strategy.StrategyEventType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StrategyEventDaoImplIT extends AbstractMongoIT<StrategyEventDao> {
  private static final String DEFAULT_INSTANCE_ID_1 = "instance1";
  private static final String DEFAULT_INSTANCE_ID_2 = "instance2";
  private static final StrategyEvent DEFAULT_EVENT_1 = new StrategyEventDto(
      123, StrategyEventType.ORDER_SENT, IntegrationTestConstants.DEFAULT_PRICE_TICK_1,
      "message1", "data1");
  private static final StrategyEvent DEFAULT_EVENT_2 = new StrategyEventDto(
      345, StrategyEventType.ERROR, IntegrationTestConstants.DEFAULT_PRICE_TICK_2,
      "message2", "data2");

  @Test
  public void testSaveAndFindEvents() {
    dao.logEvent(DEFAULT_INSTANCE_ID_1, DEFAULT_EVENT_1);
    dao.logEvent(DEFAULT_INSTANCE_ID_1, DEFAULT_EVENT_2);
    for (StrategyEvent event: dao.findEventsLog(DEFAULT_INSTANCE_ID_1, 0, 2)) {
      if ("message1".equals(event.getMessage())) {
        assertMatches(DEFAULT_EVENT_1, event);
      } else {
        assertMatches(DEFAULT_EVENT_2, event);
      }
    }
  }

  @Test
  public void testShouldFindByInstanceId() {
    dao.logEvent(DEFAULT_INSTANCE_ID_1, DEFAULT_EVENT_1);
    dao.logEvent(DEFAULT_INSTANCE_ID_1, DEFAULT_EVENT_2);
    dao.logEvent(DEFAULT_INSTANCE_ID_2, DEFAULT_EVENT_1);
    dao.logEvent(DEFAULT_INSTANCE_ID_2, DEFAULT_EVENT_2);
    assertEquals(2, dao.findEventsLog(DEFAULT_INSTANCE_ID_1, 0, 4).size());
  }

  @Override
  protected StrategyEventDao createDao(MongoDatabase mongoDatabase) {
    return new StrategyEventDaoImpl(mongoDatabase);
  }

  private void assertMatches(StrategyEvent expected, StrategyEvent actual) {
    assertEquals(expected.getMessage(), actual.getMessage());
    assertEquals(expected.getData(), actual.getData());
    assertEquals(expected.getTimestamp(), actual.getTimestamp());
    assertEquals(expected.getPriceTick(), actual.getPriceTick());
    assertEquals(expected.getStrategyEventType(), actual.getStrategyEventType());
  }
}
