package io.algobox.price.feed;

import io.algobox.price.PriceTick;

public interface PriceFeed {
  Iterable<PriceTick> getPrices();
}
