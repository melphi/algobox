package io.algobox.feature.common;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.algobox.TestingConstants;
import io.algobox.price.PriceBar;
import io.algobox.price.PriceTick;
import io.algobox.price.StandardTimeFrame;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PriceBarFeatureTest {
  @Test
  public void shouldAggregate15mBars_hasBarsSkipFirst() throws Exception {
    PriceBarFeature priceBarFeature = new PriceBarFeature(
        StandardTimeFrame.M15, TestingConstants.DEFAULT_INSTRUMENT_DAX, true);
    List<PriceBar> priceBars = Lists.newArrayList();
    for (PriceTick priceTick: TestingConstants.PRICES_FEED_DAX_MORNING.getPrices()) {
      priceBarFeature.onPriceTick(priceTick);
      if (priceBarFeature.getPriceBar() != null) {
        PriceBar priceBar = priceBarFeature.getPriceBar();
        assertFalse(Strings.isNullOrEmpty(priceBar.getInstrument()));
        assertEquals(StandardTimeFrame.M15.getValue(), priceBar.getTimeFrame());
        assertTrue(priceBar.getTime() > 0);
        assertTrue(priceBar.getAskOpen() > 0);
        assertTrue(priceBar.getBidOpen() > 0);
        assertTrue(priceBar.getAskHigh() > 0);
        assertTrue(priceBar.getBidHigh() > 0);
        assertTrue(priceBar.getAskLow() > 0);
        assertTrue(priceBar.getBidLow() > 0);
        assertTrue(priceBar.getAskClose() > 0);
        assertTrue(priceBar.getBidClose() > 0);
        priceBars.add(priceBar);
      }
    }
    assertEquals(15, priceBars.size());
  }

  @Test
  public void shouldAggregate15mBars_hasBarsNoSkipFirst() throws Exception {
    PriceBarFeature priceBarFeature = new PriceBarFeature(
        StandardTimeFrame.M15, TestingConstants.DEFAULT_INSTRUMENT_DAX, false);
    List<PriceBar> priceBars = Lists.newArrayList();
    PriceTick fistTick = null;
    for (PriceTick priceTick: TestingConstants.PRICES_FEED_DAX_SMALL.getPrices()) {
      if (fistTick == null) {
        fistTick = priceTick;
      }
      priceBarFeature.onPriceTick(priceTick);
      if (priceBarFeature.getPriceBar() != null) {
        PriceBar priceBar = priceBarFeature.getPriceBar();
        assertFalse(Strings.isNullOrEmpty(priceBar.getInstrument()));
        assertEquals(StandardTimeFrame.M15.getValue(), priceBar.getTimeFrame());
        assertTrue(priceBar.getTime() > 0);
        assertTrue(priceBar.getAskOpen() > 0);
        assertTrue(priceBar.getBidOpen() > 0);
        assertTrue(priceBar.getAskHigh() > 0);
        assertTrue(priceBar.getBidHigh() > 0);
        assertTrue(priceBar.getAskLow() > 0);
        assertTrue(priceBar.getBidLow() > 0);
        assertTrue(priceBar.getAskClose() > 0);
        assertTrue(priceBar.getBidClose() > 0);
        priceBars.add(priceBar);
      }
    }
    assertEquals(1, priceBars.size());
    assertEquals(priceBars.get(0).getAskOpen(), fistTick.getAsk(), 0.0);
    assertEquals(priceBars.get(0).getBidOpen(), fistTick.getBid(), 0.0);
  }
}
