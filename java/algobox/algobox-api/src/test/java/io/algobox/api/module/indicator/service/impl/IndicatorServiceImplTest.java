package io.algobox.api.module.indicator.service.impl;

import com.google.common.collect.ImmutableList;
import io.algobox.api.module.indicator.dao.PriceOhlcCacheDao;
import io.algobox.api.module.indicator.dao.impl.TestingPriceOhlcCacheDao;
import io.algobox.indicator.IndicatorService;
import io.algobox.price.Ohlc;
import io.algobox.price.PriceService;
import io.algobox.price.PriceTick;
import io.algobox.testing.TestingConstants;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class IndicatorServiceImplTest {
  private static final Collection<PriceTick> DEFAULT_PRICE_TICKS =
      ImmutableList.of(TestingConstants.DEFAULT_PRICE_TICK_1);
  private static final String DEFAULT_INSTRUMENT_ID = "instrument1";

  private IndicatorService indicatorService;
  private PriceService priceService;

  @Before
  public void init() {
    priceService = mock(PriceService.class);
    when(priceService.getPriceTicks(eq(DEFAULT_INSTRUMENT_ID),
        eq(TestingConstants.DEFAULT_FROM_TIMESTAMP),
        eq(TestingConstants.DEFAULT_TO_TIMESTAMP)))
        .thenReturn(DEFAULT_PRICE_TICKS);
    PriceOhlcCacheDao priceOhlcCacheDao = new TestingPriceOhlcCacheDao();
    indicatorService = new IndicatorServiceImpl(priceOhlcCacheDao, priceService);
  }

  @Test
  public void testGetOhlcUsesCache() throws Exception {
    Ohlc ohlc1 = indicatorService.getOhlc(DEFAULT_INSTRUMENT_ID,
        TestingConstants.DEFAULT_FROM_TIMESTAMP, TestingConstants.DEFAULT_TO_TIMESTAMP);
    verify(priceService, times(1)).getPriceTicks(anyString(), anyLong(), anyLong());
    reset(priceService);
    Ohlc ohlc2 = indicatorService.getOhlc(DEFAULT_INSTRUMENT_ID,
        TestingConstants.DEFAULT_FROM_TIMESTAMP, TestingConstants.DEFAULT_TO_TIMESTAMP);
    verify(priceService, never()).getPriceTicks(anyString(), anyLong(), anyLong());
    assertEquals(ohlc1, ohlc2);
  }
}
