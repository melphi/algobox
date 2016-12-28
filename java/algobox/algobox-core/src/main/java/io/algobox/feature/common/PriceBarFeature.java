package io.algobox.feature.common;

import io.algobox.feature.StrategyFeature;
import io.algobox.price.PriceBar;
import io.algobox.price.PriceTick;
import io.algobox.price.StandardTimeFrame;
import io.algobox.util.MoreDateTimeUtils;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;

/**
 * Returns a price bar when a new price tick triggers a new bar formation.
 */
public final class PriceBarFeature implements StrategyFeature {
  private final StandardTimeFrame timeFrame;
  private final String instrumentId;

  private Optional<PriceBar> newPriceBar = Optional.empty();

  private boolean skipFirstBar;
  private long nextBarTimestamp = -1;

  private long currentBarTimestamp = -1;
  private double askOpen = -1;
  private double bidOpen = -1;
  private double askHigh = -1;
  private double bidHigh = -1;
  private double askLow = -1;
  private double bidLow = -1;
  private double askClose = -1;
  private double bidClose = -1;

  public PriceBarFeature(StandardTimeFrame timeFrame, String instrumentId, boolean skipFirstBar) {
    this.timeFrame = checkNotNull(timeFrame);
    this.instrumentId = checkNotNullOrEmpty(instrumentId);
    this.skipFirstBar = skipFirstBar;
  }

  @Override
  public synchronized void onPriceTick(PriceTick priceTick) {
    checkArgument(this.instrumentId.equals(priceTick.getInstrument()));

    // Returns a new bar when tick overcomes closing timestamp and initialisation is done.
    newPriceBar = Optional.empty();
    if (priceTick.getTime() >= nextBarTimestamp) {
      nextBarTimestamp = MoreDateTimeUtils.getTimeFrameEnd(priceTick.getTime(), timeFrame);
      if (!skipFirstBar) {
        newPriceBar = Optional.ofNullable(buildPriceBarIfDefined());
      }
      skipFirstBar = false;
      setUpNewValues(MoreDateTimeUtils.getTimeFrameStart(priceTick.getTime(), timeFrame), priceTick);
    // Otherwise update the current values.
    } else {
      updateValues(priceTick);
    }
  }

  public synchronized PriceBar getPriceBar() {
    return newPriceBar.orElse(null);
  }

  private void updateValues(PriceTick priceTick) {
    askHigh = Math.max(askHigh, priceTick.getAsk());
    bidHigh = Math.max(bidHigh, priceTick.getBid());
    askLow = Math.min(askLow, priceTick.getAsk());
    bidLow = Math.min(bidLow, priceTick.getBid());
    askClose = priceTick.getAsk();
    bidClose = priceTick.getBid();
  }

  private void setUpNewValues(long frameStartTimestamp, PriceTick priceTick) {
    currentBarTimestamp = frameStartTimestamp;
    askOpen = priceTick.getAsk();
    bidOpen = priceTick.getBid();
    askClose = priceTick.getAsk();
    bidClose = priceTick.getBid();
    askHigh = priceTick.getAsk();
    bidHigh = priceTick.getBid();
    askLow = priceTick.getAsk();
    bidLow = priceTick.getBid();
    askClose = priceTick.getAsk();
    bidClose = priceTick.getBid();
  }

  private PriceBar buildPriceBarIfDefined() {
    if (currentBarTimestamp > 0) {
      return new PriceBar(instrumentId, timeFrame.getValue(), currentBarTimestamp, askOpen, bidOpen,
          askHigh, bidHigh, askLow, bidLow, askClose, bidClose);
    }
    return null;
  }
}
