package io.algobox.order;

import java.io.Serializable;

public enum OrderDirection implements Serializable {
  SHORT("SHORT"),
  LONG("LONG");

  public static final String SHORT_TEXT = "SHORT";
  public static final String LONG_TEXT = "LONG";

  OrderDirection(String value) {
    this.value = value;
  }

  private final String value;

  @Override
  public String toString() {
    return value;
  }

  public String getValue() {
    return value;
  }
}
