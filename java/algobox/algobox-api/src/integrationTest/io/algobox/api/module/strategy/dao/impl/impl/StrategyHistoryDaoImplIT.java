package io.algobox.api.module.strategy.dao.impl.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mongodb.client.MongoDatabase;
import io.algobox.api.AbstractMongoIT;
import io.algobox.api.module.strategy.dao.impl.StrategyHistoryDao;
import io.algobox.api.module.strategy.domain.StrategyHistory;
import io.algobox.api.module.strategy.domain.StrategyRegistration;
import io.algobox.api.module.strategy.domain.dto.StrategyRegistrationDto;
import io.algobox.strategy.InstrumentMapping;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class StrategyHistoryDaoImplIT extends AbstractMongoIT<StrategyHistoryDao> {
  private static final InstrumentMapping DEFAULT_INSTRUMENT_MAPPING = new InstrumentMapping(
      "priceConnection1", "priceInstrument1", "orderConnection1", "orderInstrument1");
  private static final long DEFAULT_TIMESTAMP = 123;
  private static final long DEFAULT_RECEIVED_TICKS = 456;
  private static final StrategyRegistration DEFAULT_STRATEGY_REGISTRATION_1 =
      new StrategyRegistrationDto("instanceId1", "strategyId1", "title1",
          ImmutableMap.of("param1", "value1"), ImmutableList.of(DEFAULT_INSTRUMENT_MAPPING));
  private static final StrategyRegistration DEFAULT_STRATEGY_REGISTRATION_2 =
      new StrategyRegistrationDto("instanceId2", "strategyId2", "title2",
          ImmutableMap.of("param1", "value1"), ImmutableList.of(DEFAULT_INSTRUMENT_MAPPING));

  @Test
  public void testSaveAndFindAll() throws Exception {
    Exception exception = new Exception("test");
    dao.save(DEFAULT_STRATEGY_REGISTRATION_1, DEFAULT_TIMESTAMP, Optional.of(exception),
        DEFAULT_RECEIVED_TICKS);
    dao.save(DEFAULT_STRATEGY_REGISTRATION_2, DEFAULT_TIMESTAMP, Optional.of(exception),
        DEFAULT_RECEIVED_TICKS);
    for (StrategyHistory history: dao.findAll(0, 2)) {
      if (history.getInstanceId().equals(DEFAULT_STRATEGY_REGISTRATION_1.getInstanceId())) {
        assertMatches(history, DEFAULT_STRATEGY_REGISTRATION_1, exception);
      } else {
        assertMatches(history, DEFAULT_STRATEGY_REGISTRATION_2, exception);
      }
    }
  }

  @Test(expected = Exception.class)
  public void testAvoidDuplicates() throws Exception {
    StrategyRegistration strategyRegistration = new StrategyRegistrationDto("instanceId",
        "strategyId", "title", ImmutableMap.of("param1", "value1"),
        ImmutableList.of(DEFAULT_INSTRUMENT_MAPPING));
    dao.save(strategyRegistration, DEFAULT_TIMESTAMP, Optional.empty(), DEFAULT_RECEIVED_TICKS);
    dao.save(strategyRegistration, DEFAULT_TIMESTAMP, Optional.empty(), DEFAULT_RECEIVED_TICKS);
  }

  @Override
  protected StrategyHistoryDao createDao(MongoDatabase mongoDatabase) {
    return new StrategyHistoryDaoImpl(mongoDatabase);
  }

  private void assertMatches(StrategyHistory history, StrategyRegistration strategyRegistration,
                             Exception exception) {
    assertEquals(strategyRegistration.getParameters(), history.getParameters());
    assertEquals(strategyRegistration.getInstanceId(), history.getInstanceId());
    assertEquals(strategyRegistration.getStrategyId(), history.getStrategyId());
    assertEquals(strategyRegistration.getTitle(), history.getTitle());
    assertEquals(ImmutableList.of(DEFAULT_INSTRUMENT_MAPPING), history.getInstrumentsMapping());
    assertEquals(exception.getMessage(), history.getExceptionMessage());
  }
}
