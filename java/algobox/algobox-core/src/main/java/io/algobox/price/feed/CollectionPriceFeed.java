package io.algobox.price.feed;

import io.algobox.price.PriceTick;

import java.util.Collection;

public final class CollectionPriceFeed implements PriceFeed {
  private final Collection<PriceTick> priceTicks;

  public CollectionPriceFeed(Collection<PriceTick> priceTicks) {
    this.priceTicks = priceTicks;
  }

  @Override
  public Iterable<PriceTick> getPrices() {
    return priceTicks;
  }
}
