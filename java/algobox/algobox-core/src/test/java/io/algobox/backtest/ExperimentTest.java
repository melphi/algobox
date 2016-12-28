package io.algobox.backtest;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.algobox.indicator.IndicatorService;
import io.algobox.instrument.InstrumentService;
import io.algobox.price.feed.PriceFeed;
import io.algobox.price.feed.ResourcesPriceFeed;
import io.algobox.strategy.InstrumentMapping;
import io.algobox.strategy.Strategy;
import io.algobox.strategy.dummy.DummyStrategy;
import io.algobox.TestingConstants;
import io.algobox.TestingIndicatorService;
import io.algobox.TestingInstrumentService;
import org.junit.Test;

import java.util.Collection;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static org.junit.Assert.assertEquals;

public class ExperimentTest {
  @Test
  public void shouldRunExperiment() {
    ExperimentResult experimentResult = runExperiment(
        DummyStrategy.class, "/dax_ticks_small_sample.csv", ImmutableMap.of());
    long requestsCount = experimentResult.getActiveOrdersCount()
        + experimentResult.getActiveTradesCount()
        + experimentResult.getActiveTradesCount();
    assertEquals(1, requestsCount);
  }

  private ExperimentResult runExperiment(
      Class<? extends Strategy> strategyClass, String fileName, Map<String, String> parameters) {
    checkArgument(fileName.startsWith("/"), "File name should be a part starting with /.");
    Collection<InstrumentMapping> instrumentMapping = ImmutableList.of(
        new InstrumentMapping(TestingConstants.DEFAULT_CONNECTION_ID,
            TestingConstants.DEFAULT_INSTRUMENT_DAX, TestingConstants.DEFAULT_CONNECTION_ID,
            TestingConstants.DEFAULT_INSTRUMENT_DAX));
    IndicatorService indicatorService = new TestingIndicatorService();
    InstrumentService instrumentService = new TestingInstrumentService();
    PriceFeed priceFeed = new ResourcesPriceFeed(fileName, TestingConstants.DEFAULT_INSTRUMENT_DAX);
    Experiment experiment = Experiment.newExperiment(priceFeed, strategyClass, parameters,
        instrumentMapping, indicatorService, instrumentService, 0);
    return experiment.run();
  }
}
