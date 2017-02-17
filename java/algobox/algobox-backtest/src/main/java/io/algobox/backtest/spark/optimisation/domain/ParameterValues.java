package io.algobox.backtest.spark.optimisation.domain;

import java.io.Serializable;
import java.util.Set;

public final class ParameterValues implements Serializable {
  private ParameterValuesType parameterValuesType;

  private String fromValue;

  private String toValue;

  private String step;

  private Set<String> values;

  public ParameterValues() {
    // Intentionally empty.
  }

  public ParameterValues(ParameterValuesType parameterValuesType, String fromValue,
      String toValue, String step, Set<String> values) {
    this.parameterValuesType = parameterValuesType;
    this.fromValue = fromValue;
    this.toValue = toValue;
    this.step = step;
    this.values = values;
  }

  public ParameterValuesType getParameterValuesType() {
    return parameterValuesType;
  }

  public String getFromValue() {
    return fromValue;
  }

  public String getToValue() {
    return toValue;
  }

  public String getStep() {
    return step;
  }

  public Set<String> getValues() {
    return values;
  }
}
