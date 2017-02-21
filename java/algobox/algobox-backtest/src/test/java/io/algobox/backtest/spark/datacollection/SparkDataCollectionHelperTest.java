package io.algobox.backtest.spark.datacollection;

import io.algobox.instrument.InstrumentInfoDetailed;
import io.algobox.instrument.InstrumentService;
import io.algobox.testing.TestingInstrumentService;
import org.junit.Before;
import org.junit.Test;
import scala.Tuple3;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SparkDataCollectionHelperTest {
  private static long DEFAULT_FROM_DAY = ZonedDateTime.of(
      2017, 2, 3, 7, 0, 0, 0, ZoneId.of("UTC")).toInstant().toEpochMilli();
  private static long DEFAULT_TO_DAY = ZonedDateTime.of(
      2017, 2, 6, 19, 0, 0, 0, ZoneId.of("UTC")).toInstant().toEpochMilli();

  private InstrumentService instrumentService;

  @Before
  public void init() {
    instrumentService = new TestingInstrumentService();
  }

  @Test
  public void getOrderedDays_non24Hours() throws Exception {
    InstrumentInfoDetailed instrumentInfo =
        instrumentService.getInstrumentInfo(TestingInstrumentService.INSTRUMENT_DAX);
    List<Tuple3<String, Long, Long>> result = SparkDataCollectionHelper.getOrderedDays(
        DEFAULT_FROM_DAY, DEFAULT_TO_DAY, TestingInstrumentService.INSTRUMENT_DAX, instrumentInfo);
    assertEquals(2, result.size());
    assertOrderedValues(result, TestingInstrumentService.INSTRUMENT_DAX);
  }

  @Test
  public void getOrderedDays_24Hours() throws Exception {
    InstrumentInfoDetailed instrumentInfo =
        instrumentService.getInstrumentInfo(TestingInstrumentService.INSTRUMENT_EURUSD);
    List<Tuple3<String, Long, Long>> result = SparkDataCollectionHelper.getOrderedDays(
        DEFAULT_FROM_DAY, DEFAULT_TO_DAY, TestingInstrumentService.INSTRUMENT_EURUSD,
        instrumentInfo);
    assertEquals(1, result.size());
    assertOrderedValues(result, TestingInstrumentService.INSTRUMENT_EURUSD);
  }

  private void assertOrderedValues(List<Tuple3<String, Long, Long>> result, String instrumentId) {
    checkNotNullOrEmpty(instrumentId);
    long lastFrom = 0;
    long lastTo = 0;
    for (Tuple3<String, Long, Long> day: result) {
      assertEquals(instrumentId, day._1());
      assertTrue(day._2() > lastFrom);
      assertTrue(day._3() > lastTo);
      lastFrom = day._2();
      lastTo = day._3();
      assertTrue(lastFrom < lastTo);
    }
  }
}
