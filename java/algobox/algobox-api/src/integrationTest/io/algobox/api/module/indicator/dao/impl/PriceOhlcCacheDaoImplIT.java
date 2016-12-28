package io.algobox.api.module.indicator.dao.impl;

import com.mongodb.client.MongoDatabase;
import io.algobox.api.AbstractMongoIT;
import io.algobox.api.module.indicator.dao.PriceOhlcCacheDao;
import io.algobox.api.module.instrument.service.impl.TestingConstants;
import io.algobox.price.Ohlc;
import io.algobox.price.PriceOhlc;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class PriceOhlcCacheDaoImplIT extends AbstractMongoIT<PriceOhlcCacheDao> {
  private static final Ohlc DEFAULT_OHLC_1 = new PriceOhlc(TestingConstants.DEFAULT_INSTRUMENT_DAX,
      0.001, 0.002, 0.003, 0.004, 0.005, 0.006, 0.007, 0.008);
  private static final Ohlc DEFAULT_OHLC_2 = new PriceOhlc(TestingConstants.DEFAULT_INSTRUMENT_DAX,
      0.008, 0.009, 0.010, 0.011, 0.012, 0.013, 0.014, 0.015);

  @Test
  public void testGetOhlcWhenPresent() {
    dao.saveOrUpdatePriceOhlc(DEFAULT_OHLC_1, 111L, 222L);
    dao.saveOrUpdatePriceOhlc(DEFAULT_OHLC_2, 333L, 444L);
    Ohlc ohlc = dao.getPriceOhlc(TestingConstants.DEFAULT_INSTRUMENT_DAX, 111L, 222L).get();
    assertEquals(DEFAULT_OHLC_1.getInstrument(), ohlc.getInstrument());
    assertEquals(DEFAULT_OHLC_1.getAskOpen(), ohlc.getAskOpen(), 0);
    assertEquals(DEFAULT_OHLC_1.getBidOpen(), ohlc.getBidOpen(), 0);
    assertEquals(DEFAULT_OHLC_1.getAskHigh(), ohlc.getAskHigh(), 0);
    assertEquals(DEFAULT_OHLC_1.getBidHigh(), ohlc.getBidHigh(), 0);
    assertEquals(DEFAULT_OHLC_1.getAskLow(), ohlc.getAskLow(), 0);
    assertEquals(DEFAULT_OHLC_1.getBidLow(), ohlc.getBidLow(), 0);
    assertEquals(DEFAULT_OHLC_1.getAskClose(), ohlc.getAskClose(), 0);
    assertEquals(DEFAULT_OHLC_1.getBidClose(), ohlc.getBidClose(), 0);
  }

  @Test
  public void testGetEmptyWhenAbsent() {
    dao.saveOrUpdatePriceOhlc(DEFAULT_OHLC_1, 111L, 222L);
    assertFalse(dao.getPriceOhlc(TestingConstants.DEFAULT_INSTRUMENT_DAX, 110L, 222L).isPresent());
    assertFalse(dao.getPriceOhlc(TestingConstants.DEFAULT_INSTRUMENT_DAX, 111L, 221L).isPresent());
    assertFalse(dao.getPriceOhlc(TestingConstants.DEFAULT_INSTRUMENT_DAX, 111L, 223L).isPresent());
    assertFalse(dao.getPriceOhlc(TestingConstants.DEFAULT_INSTRUMENT_DAX, 112L, 222L).isPresent());
  }

  @Test
  public void testAllowUpdates() {
    dao.saveOrUpdatePriceOhlc(DEFAULT_OHLC_1, 111L, 222L);
    dao.saveOrUpdatePriceOhlc(DEFAULT_OHLC_2, 111L, 222L);
  }

  @Override
  protected PriceOhlcCacheDao createDao(MongoDatabase mongoDatabase) {
    return new PriceOhlcCacheDaoImpl(mongoDatabase);
  }
}
