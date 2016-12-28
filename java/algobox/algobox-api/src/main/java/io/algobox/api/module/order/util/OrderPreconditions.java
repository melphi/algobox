package io.algobox.api.module.order.util;

import io.algobox.order.OrderRequest;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;

public final class OrderPreconditions {
  public static void checkOrderRequest(OrderRequest orderRequest) {
    // TODO(robertom): Implement more validations.
    checkNotNull(orderRequest);
    checkNotNullOrEmpty(orderRequest.getOrderRequestId(), "Missing order request id.");
    checkNotNullOrEmpty(orderRequest.getInstrumentId(), "Missing instrument id.");
    checkNotNull(orderRequest.getCloseStrategy(), "Missing close dummy.");
    checkNotNull(orderRequest.getOpenStrategy(), "Missing open dummy.");
    checkArgument(orderRequest.getAmount() > 0, "Order amount should be between 0 and 1.");
    checkArgument(orderRequest.getAmount() <= 1, "Order amount should be between 0 and 1.");
  }
}
