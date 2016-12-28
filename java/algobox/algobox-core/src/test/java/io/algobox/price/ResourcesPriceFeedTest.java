package io.algobox.price;

import com.google.common.base.Strings;
import io.algobox.price.feed.PriceFeed;
import io.algobox.price.feed.ResourcesPriceFeed;
import io.algobox.TestingConstants;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ResourcesPriceFeedTest {
  @Test
  public void shouldReturnThePrices() throws Exception {
    PriceFeed priceFeed = new ResourcesPriceFeed(
        TestingConstants.FILE_DAX_SAMPLE_SMALL, TestingConstants.DEFAULT_INSTRUMENT_DAX);
    int counter = 0;
    for (PriceTick priceTick: priceFeed.getPrices()) {
      counter += 1;
      assertFalse(Strings.isNullOrEmpty(priceTick.getInstrument()));
      assertTrue(priceTick.getTime() > 0);
      assertTrue(priceTick.getAsk() > 0);
      assertTrue(priceTick.getBid() > 0);
    }
    assertTrue(counter > 0);
  }
}
