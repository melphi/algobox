package io.algobox.backtest.spark.factory;

import com.google.common.collect.ImmutableSet;
import io.algobox.backtest.spark.domain.ParameterValues;
import io.algobox.backtest.spark.domain.ParameterValuesType;

import java.io.Serializable;
import java.util.Set;

public final class ParameterValuesFactory implements Serializable {
  public static ParameterValues createList(Set<String> values) {
    return new ParameterValues(
        ParameterValuesType.LIST, null, null, null, ImmutableSet.copyOf(values));
  }

  public static ParameterValues createRange(double from, double to, double step) {
    return new ParameterValues(ParameterValuesType.RANGE, String.valueOf(from), String.valueOf(to),
        String.valueOf(step), ImmutableSet.of());
  }

  public static ParameterValues createSigle(String value) {
    return new ParameterValues(ParameterValuesType.SINGLE, value, value, null, ImmutableSet.of());
  }
}
