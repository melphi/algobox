package io.algobox.connector.oanada.domain;

import java.io.Serializable;
import java.util.List;

public final class OandaOrderResponse implements Serializable {
  /**
   * The Transaction that created the Order specified by the request.
   */
  private OandaTransaction orderCreateTransaction;

  /**
   * The Transaction that cancelled the newly created Order. Only provided when the Order was
   * immediately cancelled.
   */
  private OandaOrderCancelTransaction orderCancelTransaction;

  /**
   * The IDs of all Transactions that were created while satisfying the request.
   */
  private List<String> relatedTransactionIDs;

  public OandaTransaction getOrderCreateTransaction() {
    return orderCreateTransaction;
  }

  public List<String> getRelatedTransactionIDs() {
    return relatedTransactionIDs;
  }

  public OandaOrderCancelTransaction getOrderCancelTransaction() {
    return orderCancelTransaction;
  }
}
