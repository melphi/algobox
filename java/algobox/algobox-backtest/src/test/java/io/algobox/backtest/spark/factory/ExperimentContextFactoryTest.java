package io.algobox.backtest.spark.factory;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import io.algobox.backtest.spark.domain.ExperimentContext;
import io.algobox.backtest.spark.domain.ParameterValues;
import io.algobox.strategy.dummy.DummyStrategy;
import io.algobox.testing.TestingConstants;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class ExperimentContextFactoryTest {
  private static final String DEFAULT_STRATEGY_CLASS = DummyStrategy.class.getName();

  @Test
  public void createExperimentContexts() {
    Map<String, ParameterValues> parameters = ImmutableMap.<String, ParameterValues>builder()
        .put("parameterA", ParameterValuesFactory.createList(ImmutableSet.of("a", "b")))
        .put("parameterB", ParameterValuesFactory.createRange(-2, 2, 2))
        .put("parameterC", ParameterValuesFactory.createSigle("c"))
        .build();
    List<ExperimentContext> result = ExperimentContextFactory.createExperimentContexts(
        DEFAULT_STRATEGY_CLASS, parameters, TestingConstants.DEFAULT_INSTRUMENT_MAPPINGS);
    assertEquals(6, result.size());
    Map<String, Set<String>> expected = ImmutableMap.<String, Set<String>>builder()
        .put("parameterA", ImmutableSet.of("a", "b"))
        .put("parameterB", ImmutableSet.of("-2.0", "0.0", "2.0"))
        .put("parameterC", ImmutableSet.of("c"))
        .build();
    List<String> buffer = Lists.newArrayList();
    for (ExperimentContext experimentContext: result) {
      assertEquals(DEFAULT_STRATEGY_CLASS, experimentContext.getStrategyClass());
      // Parameters should contain expected keys and values only.
      for (Map.Entry<String, String> entry: experimentContext.getParameters().entrySet()) {
        assertTrue(expected.containsKey(entry.getKey()));
        Set<String> expectedValues = expected.get(entry.getKey());
        assertTrue(String.format("[%s] does not contain [%s].",
            Joiner.on(",").join(expectedValues), entry.getValue()),
            expectedValues.contains(entry.getValue()));
      }
      // Parameters should not be repeated.
      String hash = Joiner.on("-").join(experimentContext.getParameters().values());
      assertFalse(buffer.contains(hash));
      buffer.add(hash);
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void createExperimentContextsInvalidRange_step() {
    Map<String, ParameterValues> parameters =
        ImmutableMap.of("A", ParameterValuesFactory.createRange(-2, 2, 3));
    ExperimentContextFactory.createExperimentContexts(
        DEFAULT_STRATEGY_CLASS, parameters, TestingConstants.DEFAULT_INSTRUMENT_MAPPINGS);
  }

  @Test(expected = IllegalArgumentException.class)
  public void createExperimentContextsInvalidRange_ZeroStep() {
    Map<String, ParameterValues> parameters =
        ImmutableMap.of("A", ParameterValuesFactory.createRange(-2, 2, 0));
    ExperimentContextFactory.createExperimentContexts(
        DEFAULT_STRATEGY_CLASS, parameters, TestingConstants.DEFAULT_INSTRUMENT_MAPPINGS);
  }

  @Test(expected = IllegalArgumentException.class)
  public void createExperimentContextsInvalidRange_FromTo() {
    Map<String, ParameterValues> parameters =
        ImmutableMap.of("A", ParameterValuesFactory.createRange(1, 1, 1));
    ExperimentContextFactory.createExperimentContexts(
        DEFAULT_STRATEGY_CLASS, parameters, TestingConstants.DEFAULT_INSTRUMENT_MAPPINGS);
  }
}
