package io.algobox.price.util;

public final class PriceUtils {
  /**
   * Returns the number of pips, in positive or negative integer value.
   * @param toPrice For Long orders is the selling price, for short is the purchase price.
   * @param fromPrice For Long orders is the purchase price, for short is the selling price.
   */
  public static long deltaPips(int pipsDecimals, double toPrice, double fromPrice) {
    double factor = Math.pow(10, pipsDecimals + 1);
    long toPriceValue = (long) (toPrice * factor);
    long fromPriceValue = (long) (fromPrice * factor);
    return (long) ((toPriceValue - fromPriceValue) / 10.0);
  }

  public static long getPlPipsForLong(int pipsDecimals, double sellPrice, double buyPrice) {
    return deltaPips(pipsDecimals, sellPrice, buyPrice);
  }

  public static long getPlPipsForShort(int pipsDecimals, double sellPrice, double buyPrice) {
    return deltaPips(pipsDecimals, buyPrice, sellPrice);
  }

  /**
   * Converts the pips value in terms of price value.
   */
  public static double pipAsPriceValue(double pips, int pipsDecimals) {
    return pips / Math.pow(10, pipsDecimals);
  }
}
