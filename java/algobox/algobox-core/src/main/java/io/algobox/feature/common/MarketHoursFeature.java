package io.algobox.feature.common;

import io.algobox.feature.StrategyFeature;
import io.algobox.instrument.InstrumentService;
import io.algobox.instrument.MarketHours;
import io.algobox.price.PriceTick;
import io.algobox.util.DateTimeUtils;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;

/**
 * Returns the market hours for the given instrument.
 */
public final class MarketHoursFeature implements StrategyFeature {
  private final InstrumentService instrumentService;
  private final String instrumentId;

  private long endOfDay = -1;
  private long lastTickTimeStamp = -1;
  private Optional<MarketHours> marketHours;

  public MarketHoursFeature(InstrumentService instrumentService, String instrumentId) {
    this.instrumentService = checkNotNull(instrumentService);
    this.instrumentId = checkNotNullOrEmpty(instrumentId);
  }

  @Override
  public synchronized void onPriceTick(PriceTick priceTick) {
    checkArgument(instrumentId.equals(priceTick.getInstrument()));
    if (priceTick.getTime() > endOfDay) {
      marketHours = instrumentService.getMarketHours(instrumentId, priceTick.getTime());
      endOfDay = DateTimeUtils.getEndOfDayTimestamp(priceTick.getTime());
    }
    lastTickTimeStamp = priceTick.getTime();
  }

  public synchronized MarketHours getMarketHours() {
    return marketHours.orElse(null);
  }

  public synchronized  boolean isMarketOpen() {
    return marketHours.isPresent()
        && lastTickTimeStamp >= marketHours.get().getMarketOpen()
        && lastTickTimeStamp < marketHours.get().getMarketClose();
  }
}
