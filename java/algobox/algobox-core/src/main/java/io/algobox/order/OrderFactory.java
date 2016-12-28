package io.algobox.order;

import io.algobox.price.PriceTick;

import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;
import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;

public final class OrderFactory {
  public static OrderRequest createOrderAtMarket(PriceTick priceTick, String orderConnectorId,
      String orderInstrumentId, OrderDirection orderDirection, double takeProfit, double stopLoss,
      double worstAcceptedPrice) {
    checkNotNullOrEmpty(orderConnectorId, "Invalid orderConnectorId");
    checkNotNullOrEmpty(orderInstrumentId, "Invalid orderInstrumentId");
    checkArgument(takeProfit > 0.0, "Invalid takeProfit");
    checkArgument(stopLoss > 0.0, "Invalid stopLoss");
    checkArgument(worstAcceptedPrice > 0.0, "Invalid worstAcceptedPrice");
    OpenStrategy openStrategy = new OpenStrategy(
        orderDirection, OrderType.MARKET, worstAcceptedPrice);
    CloseStrategy closeStrategy = new CloseStrategy(takeProfit, stopLoss);
    return new OrderRequest(UUID.randomUUID().toString(), orderConnectorId, orderInstrumentId, 0.0,
        priceTick, openStrategy, closeStrategy);
  }
}
