package io.algobox.feature.common;

import io.algobox.price.PriceTick;
import io.algobox.TestingConstants;
import io.algobox.TestingInstrumentService;
import org.junit.Before;
import org.junit.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Iterator;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class MarketHoursFeatureTest {
  private MarketHoursFeature marketHourFeature;

  @Before
  public void setUp() {
    marketHourFeature = new MarketHoursFeature(
        new TestingInstrumentService(), TestingConstants.DEFAULT_INSTRUMENT_DAX);
  }

  @Test
  public void shouldReturnMarketHoursDaxIntraday() {
    Long expectedOpening = getTimestamp(2016, 2, 22, 8, 0);
    Long expectedClosing = getTimestamp(2016, 2, 22, 16, 45);
    for (PriceTick priceTick: TestingConstants.PRICES_FEED_DAX_MORNING.getPrices()) {
      marketHourFeature.onPriceTick(priceTick);
      assertEquals(expectedOpening, marketHourFeature.getMarketHours().getMarketOpen());
      assertEquals(expectedClosing, marketHourFeature.getMarketHours().getMarketClose());
    }
  }

  @Test
  public void shouldReturnMarketIsOpen() {
    Iterator<PriceTick> priceTickIterator =
        TestingConstants.PRICES_FEED_DAX_TWO_DAYS.getPrices().iterator();
    PriceTick priceTick = priceTickIterator.next();
    marketHourFeature.onPriceTick(priceTick);
    assertFalse(marketHourFeature.isMarketOpen());

    priceTick = priceTickIterator.next();
    marketHourFeature.onPriceTick(priceTick);
    assertTrue(marketHourFeature.isMarketOpen());
  }

  @Test
  public void shouldRenewMarketHoursInNewDay() {
    Long expectedOpening = getTimestamp(2016, 2, 23, 8, 0);
    Long expectedClosing = getTimestamp(2016, 2, 23, 16, 45);
    for (PriceTick priceTick: TestingConstants.PRICES_FEED_DAX_TWO_DAYS.getPrices()) {
      marketHourFeature.onPriceTick(priceTick);
    }
    assertEquals(expectedOpening, marketHourFeature.getMarketHours().getMarketOpen());
    assertEquals(expectedClosing, marketHourFeature.getMarketHours().getMarketClose());
  }

  private static long getTimestamp(int year, int month, int day, int hour, int minute) {
    return ZonedDateTime.of(year, month, day, hour, minute, 0, 0, ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli();
  }
}
