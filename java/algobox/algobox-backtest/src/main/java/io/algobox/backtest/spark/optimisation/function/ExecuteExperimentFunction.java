package io.algobox.backtest.spark.optimisation.function;

import io.algobox.backtest.Experiment;
import io.algobox.backtest.ExperimentResult;
import io.algobox.backtest.spark.common.client.AlgoboxClient;
import io.algobox.backtest.spark.optimisation.domain.ExperimentContext;
import io.algobox.price.PriceTick;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;

public final class ExecuteExperimentFunction implements Function<
    ExperimentContext, ExperimentResult> {
  private final Broadcast<Collection<PriceTick>> priceTicks;
  private final AlgoboxClient algoboxService;
  private final long latencyMilliseconds;

  public ExecuteExperimentFunction(AlgoboxClient algoboxService,
      Broadcast<Collection<PriceTick>> priceTicks, long latencyMilliseconds) {
    checkArgument(latencyMilliseconds >= 0);
    this.algoboxService = algoboxService;
    this.priceTicks = priceTicks;
    this.latencyMilliseconds = latencyMilliseconds;
  }

  @Override
  public ExperimentResult call(ExperimentContext experimentContext) throws Exception {
    Experiment experiment = Experiment.newExperiment(priceTicks.getValue(),
        experimentContext.getStrategyClass(), experimentContext.getParameters(),
        experimentContext.getInstrumentMappings(), algoboxService.getIndicatorService(),
        algoboxService.getInstrumentService(), latencyMilliseconds);
    return experiment.run();
  }
}
