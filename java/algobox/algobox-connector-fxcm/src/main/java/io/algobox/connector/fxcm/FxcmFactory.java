package io.algobox.connector.fxcm;

import com.fxcm.fix.pretrade.MarketDataSnapshot;
import io.algobox.price.PriceTick;

import static com.google.common.base.Preconditions.checkNotNull;

public final class FxcmFactory {
  public static PriceTick createPriceTick(MarketDataSnapshot marketDataSnapshot) {
    checkNotNull(marketDataSnapshot);
    try {
      return new PriceTick(marketDataSnapshot.getInstrument().getSymbol(),
          marketDataSnapshot.getTickTime().getTime(),
          marketDataSnapshot.getAskClose(),
          marketDataSnapshot.getBidClose());
    } catch (Exception e) {
      throw new IllegalArgumentException(
          String.format("Error while creating price tick: [%s]", e.getMessage()), e);
    }
  }
}
