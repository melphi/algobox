package io.algobox.connector.oanada.domain;

import java.io.Serializable;

public enum OandaOrderPositionFill implements Serializable {
  /**
   * When the Order is filled, use REDUCE_FIRST behaviour for non-client hedging Accounts,
   * and OPEN_ONLY behaviour for client hedging Accounts.
   */
  DEFAULT("DEFAULT");

  private final String value;

  OandaOrderPositionFill(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return value;
  }
}
