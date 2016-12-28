package io.algobox.api.module.instrument;

import io.algobox.api.module.instrument.exception.InstrumentNotFoundException;
import io.algobox.api.module.instrument.service.impl.InstrumentServiceImpl;
import io.algobox.instrument.InstrumentInfoDetailed;
import io.algobox.instrument.InstrumentService;
import io.algobox.instrument.MarketHours;
import io.algobox.testing.TestingConstants;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class InstrumentServiceImplTest {
  private static final long DEFAULT_FRIDAY_DATE_SUMMER = 1470398194518L;
  private static final long DEFAULT_FRIDAY_DATE_WINTER = 1448002800000L;
  private static final long DEFAULT_SATURDAY_DATE = 1470484595000L;
  private static final long DEFAULT_SUNDAY_DATE_SUMMER = 1470571011000L;
  private static final long DEFAULT_SUNDAY_DATE_WINTER = 1448193630000L;

  private InstrumentService instrumentService;

  @Before
  public void init() {
    this.instrumentService = new InstrumentServiceImpl();
  }

  @Test
  public void testShouldReturnInstrumentInfo() throws Exception {
    InstrumentInfoDetailed instrumentInfo = instrumentService.getInstrumentInfo(
        TestingConstants.DEFAULT_INSTRUMENT_DAX);
    assertNotNull(instrumentInfo);
    assertEquals(TestingConstants.DEFAULT_INSTRUMENT_DAX, instrumentInfo.getInstrumentId());
  }

  @Test(expected = InstrumentNotFoundException.class)
  public void testGetInstrumentInfo_shouldReturnNotFoundException() throws Exception {
    instrumentService.getInstrumentInfo("xyz");
  }

  @Test(expected = InstrumentNotFoundException.class)
  public void testGetMarketHours_shouldReturnNotFoundException() throws Exception {
    instrumentService.getMarketHours("xyz", DEFAULT_FRIDAY_DATE_SUMMER);
  }

  @Test
  public void testGetMarketHours_shouldReturnEmptyOnSaturday() {
    assertFalse(instrumentService.getMarketHours(
        TestingConstants.DEFAULT_INSTRUMENT_DAX, DEFAULT_SATURDAY_DATE).isPresent());
    assertFalse(instrumentService.getMarketHours(
        TestingConstants.DEFAULT_INSTRUMENT_EURUSD, DEFAULT_SATURDAY_DATE).isPresent());
  }

  @Test
  public void testGetMarketHours_shouldReturnEmptyOnSundayForIndex() {
    assertFalse(instrumentService.getMarketHours(
        TestingConstants.DEFAULT_INSTRUMENT_DAX, DEFAULT_SUNDAY_DATE_SUMMER).isPresent());
  }

  @Test
  public void testGetMarketHours_shouldReturnOnFridayForIndexInSummer() {
    MarketHours marketHours = instrumentService.getMarketHours(
        TestingConstants.DEFAULT_INSTRUMENT_DAX, DEFAULT_FRIDAY_DATE_SUMMER).get();
    assertEquals(1470380400000L, (long) marketHours.getMarketOpen());
    assertEquals(1470411900000L, (long) marketHours.getMarketClose());
    assertEquals(1470380400000L, (long) marketHours.getOrb5minOpen());
    assertEquals(1470294000000L, (long) marketHours.getPreviousMarketOpen());
  }

  @Test
  public void testGetMarketHours_shouldReturnOnFridayForIndexInWinter() {
    MarketHours marketHours = instrumentService.getMarketHours(
        TestingConstants.DEFAULT_INSTRUMENT_DAX, DEFAULT_FRIDAY_DATE_WINTER).get();
    assertEquals(1448006400000L, (long) marketHours.getMarketOpen());
    assertEquals(1448037900000L, (long) marketHours.getMarketClose());
    assertEquals(1448006400000L, (long) marketHours.getOrb5minOpen());
    assertEquals(1447920000000L, (long) marketHours.getPreviousMarketOpen());
  }

  @Test
  public void testGetMarketHours_shouldReturnOnFridayForCurrencyWinter() {
    MarketHours marketHours = instrumentService.getMarketHours(
        TestingConstants.DEFAULT_INSTRUMENT_EURUSD, DEFAULT_FRIDAY_DATE_WINTER).get();
    assertEquals(1447970400000L, (long) marketHours.getMarketOpen());
    assertEquals(1448056800000L, (long) marketHours.getMarketClose());
    assertEquals(1448024400000L, (long) marketHours.getOrb5minOpen());
    assertEquals(1447884000000L, (long) marketHours.getPreviousMarketOpen());
  }

  @Test
  public void testGetMarketHours_shouldReturnOnFridayForCurrencySummer() {
    MarketHours marketHours = instrumentService.getMarketHours(
        TestingConstants.DEFAULT_INSTRUMENT_EURUSD, DEFAULT_FRIDAY_DATE_SUMMER).get();
    assertEquals(1470344400000L, (long) marketHours.getMarketOpen());
    assertEquals(1470430800000L, (long) marketHours.getMarketClose());
    assertEquals(1470398400000L, (long) marketHours.getOrb5minOpen());
    assertEquals(1470258000000L, (long) marketHours.getPreviousMarketOpen());
  }

  @Test
  public void testGetMarketHours_shouldReturnOnSundayForCurrencyWinter() {
    MarketHours marketHours = instrumentService.getMarketHours(
        TestingConstants.DEFAULT_INSTRUMENT_EURUSD, DEFAULT_SUNDAY_DATE_WINTER).get();
    assertEquals(1448229600000L, (long) marketHours.getMarketOpen());
    assertEquals(1448316000000L, (long) marketHours.getMarketClose());
    assertEquals(1448283600000L, (long) marketHours.getOrb5minOpen());
    assertEquals(1447970400000L, (long) marketHours.getPreviousMarketOpen());
  }

  @Test
  public void testGetMarketHours_shouldReturnOnSundayForCurrencySummer() {
    MarketHours marketHours = instrumentService.getMarketHours(
        TestingConstants.DEFAULT_INSTRUMENT_EURUSD, DEFAULT_SUNDAY_DATE_SUMMER).get();
    assertEquals(1470603600000L, (long) marketHours.getMarketOpen());
    assertEquals(1470690000000L, (long) marketHours.getMarketClose());
    assertEquals(1470657600000L, (long) marketHours.getOrb5minOpen());
    assertEquals(1470344400000L, (long) marketHours.getPreviousMarketOpen());
  }
}
