package io.algobox.api.module.strategy.dao.impl.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.mongodb.client.MongoDatabase;
import io.algobox.api.AbstractMongoIT;
import io.algobox.api.module.strategy.dao.impl.StrategyRegistrationDao;
import io.algobox.api.module.strategy.domain.StrategyRegistration;
import io.algobox.api.module.strategy.domain.dto.StrategyRegistrationDto;
import io.algobox.strategy.InstrumentMapping;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StrategyRegistrationDaoImplIT extends AbstractMongoIT<StrategyRegistrationDao> {
  private static final String INSTANCE_1 = "instance1";
  private static final String INSTANCE_2 = "instance2";
  private static final String STRATEGY_1 = "strategy1";
  private static final String STRATEGY_2 = "strategy2";
  private static final Collection<InstrumentMapping> INSTRUMENT_MAPPINGS = ImmutableList.of(
      new InstrumentMapping(
          "priceConnection", "priceInstrument", "orderConnection", "orderInstrument"));
  private static final StrategyRegistration STRATEGY_REGISTRATION_1 = new StrategyRegistrationDto(
      INSTANCE_1, STRATEGY_1, "title1", ImmutableMap.of("key1", "value1"), INSTRUMENT_MAPPINGS);
  private static final StrategyRegistration STRATEGY_REGISTRATION_2 = new StrategyRegistrationDto(
      INSTANCE_2, STRATEGY_2, "title2", ImmutableMap.of("key2", "value2"), INSTRUMENT_MAPPINGS);

  @Test
  public void testFindByInstanceId() {
    dao.save(STRATEGY_REGISTRATION_1);
    dao.save(STRATEGY_REGISTRATION_2);
    assertMatches(STRATEGY_REGISTRATION_1, dao.findByInstanceId(INSTANCE_1));
  }

  @Test
  public void testFindAll() {
    dao.save(STRATEGY_REGISTRATION_1);
    dao.save(STRATEGY_REGISTRATION_2);
    assertEquals(2, Iterables.size(dao.findAll()));
  }

  @Test
  public void testExists() {
    dao.save(STRATEGY_REGISTRATION_1);
    assertTrue(dao.exists(INSTANCE_1));
    assertFalse(dao.exists(STRATEGY_1));
  }

  @Test
  public void testDeleteById() {
    dao.save(STRATEGY_REGISTRATION_1);
    dao.deleteById(INSTANCE_1);
    assertFalse(dao.exists(INSTANCE_1));
    assertTrue(Iterables.isEmpty(dao.findAll()));
  }

  @Test
  public void testFindByStrategyId() {
    dao.save(STRATEGY_REGISTRATION_1);
    dao.save(STRATEGY_REGISTRATION_2);
    assertMatches(
        STRATEGY_REGISTRATION_2, Iterables.getOnlyElement(dao.findByStrategyId(STRATEGY_2)));
  }

  @Test(expected = Exception.class)
  public void testAvoidDuplicatedInstances() {
    dao.save(STRATEGY_REGISTRATION_1);
    dao.save(STRATEGY_REGISTRATION_1);
  }

  @Test(expected = Exception.class)
  public void testAvoidDuplicatedTitles() {
    dao.save(STRATEGY_REGISTRATION_1);
    StrategyRegistration newRegistration = new StrategyRegistrationDto(
        STRATEGY_REGISTRATION_2.getInstanceId(), STRATEGY_REGISTRATION_2.getStrategyId(),
        STRATEGY_REGISTRATION_1.getTitle(), STRATEGY_REGISTRATION_2.getParameters(),
        STRATEGY_REGISTRATION_2.getInstrumentsMapping());
    dao.save(newRegistration);
  }

  @Override
  protected StrategyRegistrationDao createDao(MongoDatabase mongoDatabase) {
    return new StrategyRegistrationDaoImpl(mongoDatabase);
  }

  private void assertMatches(StrategyRegistration expected, StrategyRegistration actual) {
    assertEquals(expected.getInstanceId(), actual.getInstanceId());
    assertEquals(expected.getStrategyId(), actual.getStrategyId());
    assertEquals(expected.getTitle(), actual.getTitle());
    assertEquals(expected.getInstrumentsMapping(), actual.getInstrumentsMapping());
    assertEquals(expected.getParameters(), actual.getParameters());
  }
}
