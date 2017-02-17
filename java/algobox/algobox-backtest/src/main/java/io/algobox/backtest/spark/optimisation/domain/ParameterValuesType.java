package io.algobox.backtest.spark.optimisation.domain;

import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;

public enum ParameterValuesType {
  LIST("LIST"),
  RANGE("RANGE"),
  SINGLE("SINGLE");

  public static final String LIST_TEXT = "LIST";
  public static final String RANGE_TEXT = "RANGE";
  public static final String SINGLE_TEXT = "SINGLE";

  private final String value;

  ParameterValuesType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return value;
  }

  public static ParameterValuesType fromString(String value) {
    checkNotNullOrEmpty(value);
    switch (value) {
      case LIST_TEXT:
        return LIST;
      case RANGE_TEXT:
        return RANGE;
      case SINGLE_TEXT:
        return SINGLE;
      default:
        throw new IllegalArgumentException(String.format("Unsupported value [%s].", value));
    }
  }
}
