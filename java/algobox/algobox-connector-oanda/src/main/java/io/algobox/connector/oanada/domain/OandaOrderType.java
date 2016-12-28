package io.algobox.connector.oanada.domain;

import java.io.Serializable;

public enum OandaOrderType implements Serializable {
  MARKET("MARKET"),
  LIMIT("LIMIT"),
  STOP("STOP"),
  MARKET_IF_TOUCHED("MARKET_IF_TOUCHED"),
  TAKE_PROFIT("TAKE_PROFIT"),
  STOP_LOSS("STOP_LOSS"),
  TRAILING_STOP_LOSS("TRAILING_STOP_LOSS");

  private final String value;

  OandaOrderType(String value) {
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
