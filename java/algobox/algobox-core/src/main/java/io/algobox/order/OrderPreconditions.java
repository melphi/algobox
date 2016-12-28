package io.algobox.order;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;

public final class OrderPreconditions {
  public static void checkOrderRequest(OrderRequest orderRequest) {
    checkNotNull(orderRequest);
    checkNotNullOrEmpty(orderRequest.getConnectionId(), "Missing connector id.");
    checkNotNullOrEmpty(orderRequest.getInstrumentId(), "Missing instrument id.");
    checkNotNull(orderRequest.getPriceTick(), "Missing price tick.");
    checkNotNull(orderRequest.getCloseStrategy(), "Missing close dummy.");
    checkNotNull(orderRequest.getOpenStrategy(), "Missing open dummy.");
    checkArgument(OrderType.MARKET.equals(orderRequest.getOpenStrategy().getOrderType()),
        String.format("Order dummy type [%s] not supported.",
            orderRequest.getOpenStrategy().getOrderType()));
    switch (orderRequest.getOpenStrategy().getOrderDirection()) {
      case LONG:
        checkArgument(orderRequest.getCloseStrategy().getTakeProfit() >
            orderRequest.getCloseStrategy().getStopLoss(), "Invalid TP or SL");
        break;
      case SHORT:
        checkArgument(orderRequest.getCloseStrategy().getTakeProfit() <
            orderRequest.getCloseStrategy().getStopLoss(), "Invalid TP or SL");
        break;
      default:
        throw new IllegalArgumentException(String.format("Unsupported order direction [%s].",
            orderRequest.getOpenStrategy().getOrderDirection()));
    }
  }
}
