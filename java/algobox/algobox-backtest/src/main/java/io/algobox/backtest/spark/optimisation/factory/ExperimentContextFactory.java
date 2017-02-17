package io.algobox.backtest.spark.optimisation.factory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.algobox.backtest.spark.optimisation.domain.ExperimentContext;
import io.algobox.backtest.spark.optimisation.domain.ParameterValues;
import io.algobox.strategy.InstrumentMapping;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;

public final class ExperimentContextFactory {
  public static List<ExperimentContext> createExperimentContexts(String strategyClass,
      Map<String, ParameterValues> parameters, Collection<InstrumentMapping> instrumentMappings) {
    List<Set<String>> allParametersValues = createAllParametersValues(parameters);
    ImmutableList.Builder<ExperimentContext> result = ImmutableList.builder();
    for (List<String> parameterValues: Sets.cartesianProduct(allParametersValues)) {
      result.add(createExperimentContext(
          strategyClass, parameters.keySet(), parameterValues, instrumentMappings));
    }
    return result.build();
  }

  private static ExperimentContext createExperimentContext(String strategyClass,
      Collection<String> parameterNames, Collection<String> parametersValues,
      Collection<InstrumentMapping> instrumentMappings) {
    Map<String, String> parameters = Maps.newHashMap();
    Iterator<String> values = parametersValues.iterator();
    for (String name: parameterNames) {
      parameters.put(name, values.next());
    }
    checkArgument(!values.hasNext(), "Number of keys and values not matching.");
    return new ExperimentContext(Lists.newArrayList(instrumentMappings), strategyClass, parameters);
  }

  private static Set<String> createParameterValues(ParameterValues values) {
    checkNotNull(values.getParameterValuesType());
    switch (values.getParameterValuesType()) {
      case LIST:
        checkNotNull(values.getValues(), "Parameter has null values.");
        checkArgument(!values.getValues().isEmpty(), "Parameter has empty values.");
        return ImmutableSet.copyOf(values.getValues());
      case RANGE:
        checkNotNullOrEmpty(values.getStep(), "Parameter should not have empty step.");
        checkNotNullOrEmpty(values.getFromValue(), "Parameter should not have empty from value.");
        checkNotNullOrEmpty(values.getToValue(), "Parameter should not have empty to value.");
        checkArgument(!values.getFromValue().equals(values.getToValue()),
            "Parameter should have different from value and to value.");
        ImmutableSet.Builder<String> result = ImmutableSet.builder();
        try {
          double step = Double.parseDouble(values.getStep());
          checkArgument(step > 0.0, "Parameter should have a positive step.");
          double fromValue = Double.parseDouble(values.getFromValue());
          double toValue = Double.parseDouble(values.getToValue());
          checkArgument((toValue - fromValue) % step == 0,
              "Parameter step never reaches to value starting from from value.");
          for (double d = fromValue; d <= toValue; d += step) {
            result.add(String.valueOf(d));
          }
          return result.build();
        } catch (NumberFormatException e) {
          throw new IllegalArgumentException("Parameter should have numeric values.");
        }
      case SINGLE:
        checkNotNullOrEmpty(values.getFromValue(), "Parameter should not have empty from value");
        checkArgument(values.getFromValue().equals(values.getToValue()),
            "Parameter should have same from value and to value.");
        return ImmutableSet.of(values.getFromValue());
      default:
        throw new IllegalArgumentException("Parameter has unsupported type.");
    }
  }

  private static List<Set<String>> createAllParametersValues(
      Map<String, ParameterValues> parameters) {
    ImmutableList.Builder<Set<String>> result = ImmutableList.builder();
    for (Map.Entry<String, ParameterValues> entry: parameters.entrySet()) {
      try {
        result.add(createParameterValues(entry.getValue()));
      } catch (Exception e) {
        throw new IllegalArgumentException(String.format(
            "Invalid parameter [%s] of type [%s]: [%s]", entry.getKey(),
            entry.getValue().getParameterValuesType(), e.getMessage()));
      }
    }
    return result.build();
  }
}
