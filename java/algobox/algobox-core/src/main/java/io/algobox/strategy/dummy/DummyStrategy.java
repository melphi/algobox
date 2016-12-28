package io.algobox.strategy.dummy;

import com.google.common.base.Strings;
import io.algobox.instrument.InstrumentInfoDetailed;
import io.algobox.order.OrderDirection;
import io.algobox.order.OrderFactory;
import io.algobox.order.OrderRequest;
import io.algobox.price.PriceTick;
import io.algobox.price.util.PriceUtils;
import io.algobox.strategy.AbstractStrategy;
import io.algobox.strategy.StrategyContext;
import io.algobox.strategy.StrategyStatus;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A dummy which sends an order every 5 ticks to the dummy connector and instrument TEST.
 */
public final class DummyStrategy extends AbstractStrategy {
  public static final int DEFAULT_TICKS_COUNT_ORDER_TRIGGER = 5;
  public static final String STRATEGY_ID = "dummy";

  private volatile double deltaTpSl = 0.0;
  private volatile int ticksCount = 0;

  public DummyStrategy(StrategyContext strategyContext) {
    super(strategyContext, STRATEGY_ID);
  }

  @Override
  protected void internalOnPriceTick(PriceTick priceTick) {
    checkPriceTick(priceTick);

    if (deltaTpSl <= 0) {
      deltaTpSl = getPipAsPriceValue(getStrategyContext().getOnlyPriceInstrumentId()) * 10.0;
    }

    getStrategyContext().setStatus(StrategyStatus.PROCESSING);
    if (++ticksCount % DEFAULT_TICKS_COUNT_ORDER_TRIGGER == 0) {
      getStrategyContext().setStatus(StrategyStatus.PATTERN_FOUND);
      OrderDirection orderDirection = Math.random() >= 0.5
          ? OrderDirection.LONG : OrderDirection.SHORT;
      double takeProfit = OrderDirection.LONG.equals(orderDirection)
          ? priceTick.getBid() + deltaTpSl : priceTick.getAsk() - deltaTpSl;
      double stopLoss = OrderDirection.LONG.equals(orderDirection)
          ? priceTick.getBid() - deltaTpSl : priceTick.getAsk() + deltaTpSl;
      OrderRequest orderRequest = OrderFactory.createOrderAtMarket(priceTick,
          getStrategyContext().getOnlyOrderConnectorId(),
          getStrategyContext().getOnlyOrderInstrumentId(), orderDirection, takeProfit, stopLoss,
          stopLoss);
      getStrategyContext().sendOrderAsync(orderRequest);
      getStrategyContext().setStatus(StrategyStatus.PROCESSING);
    }
  }

  private double getPipAsPriceValue(String priceInstrumentId) {
    InstrumentInfoDetailed instrumentInfo = getStrategyContext().getInstrumentService()
        .getInstrumentInfo(priceInstrumentId);
    checkNotNull(instrumentInfo, String.format("Instrument [%s] not found.", priceInstrumentId));
    return PriceUtils.pipAsPriceValue(1, instrumentInfo.getPipsDecimals());
  }

  private void checkPriceTick(PriceTick priceTick) {
    checkNotNull(priceTick);
    checkArgument(priceTick.getAsk() > 0.0);
    checkArgument(priceTick.getBid() > 0.0);
    checkArgument(!Strings.isNullOrEmpty(priceTick.getInstrument()));
    checkArgument(priceTick.getTime() > 0);
  }
}
