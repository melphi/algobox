package io.algobox.util;

import io.algobox.price.util.PriceUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PriceUtilsTest {
  @Test
  public void testDeltaPipsZero() {
    assertEquals(0.0, PriceUtils.deltaPips(5, 0.00001, 0.00001), 0.0);
  }

  @Test
  public void testDeltaPipsPositive() {
    assertEquals(2.0, PriceUtils.deltaPips(5, 0.00003, 0.00001), 0.0);
  }

  @Test
  public void testDeltaPipsNegative() {
    assertEquals(-1.0, PriceUtils.deltaPips(5, 0.00001, 0.00002), 0.0);
  }

  @Test
  public void testDeltaPipsZeroDecimalsPositive() {
    assertEquals(2, PriceUtils.deltaPips(0, 5, 3), 0.0);
  }

  @Test
  public void testDeltaPipsZeroDecimalsNegative() {
    assertEquals(-2, PriceUtils.deltaPips(0, 3, 5), 0.0);
  }

  @Test
  public void testPlPipsForLong() {
    assertEquals(1, PriceUtils.getPlPipsForLong(0, 2, 1));
    assertEquals(0, PriceUtils.getPlPipsForLong(3, 0.001, 0.001));
    assertEquals(-10, PriceUtils.getPlPipsForLong(3, 0.001, 0.011));
  }

  @Test
  public void testPlPipsForShort() {
    assertEquals(-1, PriceUtils.getPlPipsForShort(0, 2, 1));
    assertEquals(0, PriceUtils.getPlPipsForShort(2, 0.01, 0.01));
    assertEquals(10, PriceUtils.getPlPipsForShort(2, 0.01, 0.11));
  }

  @Test
  public void testPipsAsPricePositive() {
    assertEquals(1.2, PriceUtils.pipAsPriceValue(1.2, 0), 0.0);
    assertEquals(0.012, PriceUtils.pipAsPriceValue(1.2, 2), 0.0);
    assertEquals(0.00000001234, PriceUtils.pipAsPriceValue(1.234, 8), 0.0);
  }

  @Test
  public void testPipsAsPriceNegative() {
    assertEquals(-1.2, PriceUtils.pipAsPriceValue(-1.2, 0), 0.0);
    assertEquals(-0.012, PriceUtils.pipAsPriceValue(-1.2, 2), 0.0);
    assertEquals(-0.00000001234, PriceUtils.pipAsPriceValue(-1.234, 8), 0.0);
  }
}
