package io.algobox.strategy;

import io.algobox.price.PriceTick;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;

public abstract class AbstractStrategy implements Strategy {
  private final StrategyContext strategyContext;
  private final String strategyId;

  public AbstractStrategy(StrategyContext strategyContext, String strategyId) {
    this.strategyContext = checkNotNull(strategyContext);
    this.strategyId = checkNotNullOrEmpty(strategyId);
  }

  @Override
  public synchronized void onPriceTick(final PriceTick priceTick) {
    checkArgument(strategyContext.getOnlyPriceInstrumentId().equals(priceTick.getInstrument()));
    strategyContext.incrementReceivedTicks();
    internalOnPriceTick(priceTick);
  }

  @Override
  public StrategyContext getStrategyContext() {
    return strategyContext;
  }

  @Override
  public String getStrategyId() {
    return strategyId;
  }

  protected abstract void internalOnPriceTick(PriceTick priceTick);
}
