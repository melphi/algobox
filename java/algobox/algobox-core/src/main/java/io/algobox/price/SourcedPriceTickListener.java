package io.algobox.price;

/**
 * A price tick listener with the source.
 */
public interface SourcedPriceTickListener {
  void onPriceTick(String source, PriceTick priceTick);
}
