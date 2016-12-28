package io.algobox.connector.service.impl;

import com.google.common.collect.ImmutableMap;
import io.algobox.connector.service.ConnectorManager;
import org.junit.Before;
import org.junit.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConnectorManagerHelperTest {
  private static final ZonedDateTime DEFAULT_MONDAY_MORNING =
      ZonedDateTime.of(2016, 2, 29, 6, 0, 0, 0, ZoneOffset.UTC);
  private static final ZonedDateTime DEFAULT_FRIDAY_AFTERNOON =
      ZonedDateTime.of(2016, 8, 19, 14, 22, 0, 0, ZoneOffset.UTC);
  private static final ZonedDateTime DEFAULT_FRIDAY_NIGHT =
      ZonedDateTime.of(2016, 8, 19, 22, 1, 0, 0, ZoneOffset.UTC);
  private static final ZonedDateTime DEFAULT_SATURDAY =
      ZonedDateTime.of(2016, 8, 20, 12, 0, 0, 0, ZoneOffset.UTC);
  private static final ZonedDateTime DEFAULT_SUNDAY_AFTERNOON =
      ZonedDateTime.of(2016, 8, 21, 9, 0, 0, 0, ZoneOffset.UTC);
  private static final ZonedDateTime DEFAULT_SUNDAY_NIGHT =
      ZonedDateTime.of(2016, 8, 21, 23, 1, 0, 0, ZoneOffset.UTC);

  private ConnectorManager connectorManager;

  @Before
  public void init() {
    Map<String, String> parameters = ImmutableMap.of(
        "market.marketOpenTimeUtc", "22:30",
        "market.marketCloseTimeUtc", "22:00");
    this.connectorManager = new ConnectorManagerImpl(parameters);
  }

  @Test
  public void testShouldReturnMarketOpenDuringTheWeek() throws Exception {
    assertTrue(connectorManager.isMarketOpen(DEFAULT_MONDAY_MORNING));
  }

  @Test
  public void testShouldReturnMarketOpenOnFridayAfternoon() throws Exception {
    assertTrue(connectorManager.isMarketOpen(DEFAULT_FRIDAY_AFTERNOON));
  }

  @Test
  public void testShouldReturnMarketClosedOnFridayNight() throws Exception {
    assertFalse(connectorManager.isMarketOpen(DEFAULT_FRIDAY_NIGHT));
  }

  @Test
  public void testShouldReturnMarketOpenOnSundayNight() throws Exception {
    assertTrue(connectorManager.isMarketOpen(DEFAULT_SUNDAY_NIGHT));
  }

  @Test
  public void testShouldReturnMarketClosedOnSundayAfternoon() throws Exception {
    assertFalse(connectorManager.isMarketOpen(DEFAULT_SUNDAY_AFTERNOON));
  }

  @Test
  public void testShouldReturnMarketClosedOnSaturday() throws Exception {
    assertFalse(connectorManager.isMarketOpen(DEFAULT_SATURDAY));
  }
}
