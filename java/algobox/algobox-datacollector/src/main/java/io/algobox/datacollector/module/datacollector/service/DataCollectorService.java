package io.algobox.datacollector.module.datacollector.service;

import io.algobox.price.PriceTick;
import io.algobox.price.SourcedPriceTickListener;

public interface DataCollectorService extends SourcedPriceTickListener {
  void onPriceTick(String source, PriceTick priceTick);
}
