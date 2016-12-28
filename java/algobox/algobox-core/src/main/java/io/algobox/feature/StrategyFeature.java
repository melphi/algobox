package io.algobox.feature;

import io.algobox.price.PriceTick;

public interface StrategyFeature {
    void onPriceTick(PriceTick priceTick);
}
