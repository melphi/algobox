package io.algobox.backtest.spark.optimisation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import io.algobox.backtest.ExperimentResult;
import io.algobox.backtest.spark.common.client.impl.TestingAlgoboxClient;
import io.algobox.backtest.spark.optimisation.domain.OptimisationRequest;
import io.algobox.backtest.spark.optimisation.domain.ParameterValues;
import io.algobox.backtest.spark.optimisation.factory.ParameterValuesFactory;
import io.algobox.strategy.InstrumentMapping;
import io.algobox.strategy.dummy.DummyStrategy;
import io.algobox.testing.TestingConstants;
import org.junit.Test;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SparkOptimisationTaskTest {
  private static final String DEFAULT_STRATEGY_CLASS = DummyStrategy.class.getName();

  @Test
  public void shouldProduceSortedExperimentResults() {
    Collection<InstrumentMapping> instrumentMappings = ImmutableList.of(new InstrumentMapping(
        TestingConstants.DEFAULT_CONNECTION_ID, TestingConstants.DEFAULT_INSTRUMENT_DAX,
        TestingConstants.DEFAULT_CONNECTION_ID, TestingConstants.DEFAULT_INSTRUMENT_DAX));
    Map<String, ParameterValues> parameters = ImmutableMap.of(
        "parameterA", ParameterValuesFactory.createList(ImmutableSet.of("a", "b", "c")));
    OptimisationRequest experimentRequest = new OptimisationRequest(instrumentMappings,
        TestingConstants.DEFAULT_FROM_TIMESTAMP, TestingConstants.DEFAULT_TO_TIMESTAMP,
        DEFAULT_STRATEGY_CLASS, parameters, null);
    List<ExperimentResult> result = new SparkOptimisationTask().run(
        new TestingAlgoboxClient(), experimentRequest);
    assertEquals(3, result.size());
    List<ExperimentResult> sortedResults = result.stream()
        .sorted(Comparator.comparingDouble(ExperimentResult::getClosedTradesPlPips))
        .collect(Collectors.toList());
    assertTrue(Iterables.elementsEqual(result, sortedResults));
  }
}
