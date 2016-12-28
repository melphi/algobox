package io.algobox.testing;

import com.google.common.collect.ImmutableList;
import io.algobox.backtest.Experiment;
import io.algobox.backtest.ExperimentResult;
import io.algobox.indicator.IndicatorService;
import io.algobox.instrument.InstrumentService;
import io.algobox.price.feed.PriceFeed;
import io.algobox.price.feed.ResourcesPriceFeed;
import io.algobox.strategy.InstrumentMapping;
import io.algobox.strategy.Strategy;

import java.util.Collection;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

public final class TestingExperimentUtil {
  public static ExperimentResult runExperiment(
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
