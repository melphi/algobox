package io.algobox.backtest.spark.optimisation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import io.algobox.backtest.ExperimentResult;
import io.algobox.backtest.spark.common.AbstractSparkTask;
import io.algobox.backtest.spark.common.client.AlgoboxClient;
import io.algobox.backtest.spark.optimisation.domain.ExperimentContext;
import io.algobox.backtest.spark.optimisation.domain.OptimisationRequest;
import io.algobox.backtest.spark.optimisation.factory.ExperimentContextFactory;
import io.algobox.backtest.spark.optimisation.function.ExecuteExperimentFunction;
import io.algobox.price.PriceTick;
import io.algobox.strategy.InstrumentMapping;
import io.algobox.strategy.Strategy;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;
import static jersey.repackaged.com.google.common.base.Preconditions.checkArgument;
import static jersey.repackaged.com.google.common.base.Preconditions.checkNotNull;

public final class SparkOptimisationTask extends AbstractSparkTask {
  private static final long DEFAULT_LATENCY = 50L;
  private static final Logger LOGGER = LoggerFactory.getLogger(SparkOptimisationTask.class);
  private static final Collection<Class<?>> KRYO_CLASSES = ImmutableList.<Class<?>>builder()
      .add(ExperimentContext.class)
      .add(ExperimentResult.class)
      .add(InstrumentMapping.class)
      .add(PriceTick.class)
      .build();

  public SparkOptimisationTask() {
    super(KRYO_CLASSES);
  }

  public static void main(String[] args) {
    throw new IllegalArgumentException("Not yet implemented.");
  }

  public List<ExperimentResult> run(
      AlgoboxClient algoboxService, OptimisationRequest experimentRequest) {
    checkExperimentRequest(experimentRequest);
    JavaSparkContext sparkContext = createSparkContext(
        String.format("BackTest [%s].", experimentRequest.getStrategyClass()));
    // Broadcast price ticks.
    Broadcast<Collection<PriceTick>> priceTicks =
        sparkContext.broadcast(getPriceTicks(algoboxService, experimentRequest));
    // Get all possible experiments in a RDD.
    List<ExperimentContext> experimentContexts = ExperimentContextFactory.createExperimentContexts(
        experimentRequest.getStrategyClass(), experimentRequest.getParameters(),
        experimentRequest.getInstrumentMappings());
    LOGGER.info(String.format("Processing [%d] experiments.", experimentContexts.size()));
    // Run dummy and return results.
    List<ExperimentResult> result = sparkContext.parallelize(experimentContexts)
        .map(new ExecuteExperimentFunction(algoboxService, priceTicks, DEFAULT_LATENCY))
        .sortBy(ExperimentResult::getClosedTradesPlPips, true, 1)
        .collect();
    LOGGER.info(String.format("[%d] experiment processed, returning result.", result.size()));
    sparkContext.stop();
    return result;
  }

  private Collection<PriceTick> getPriceTicks(
      AlgoboxClient apiClient, OptimisationRequest experimentRequest) {
    checkArgument(experimentRequest.getFromTimestamp() > 0, "Invalid from timestamp.");
    checkArgument(experimentRequest.getToTimestamp() > experimentRequest.getFromTimestamp(),
        "To timestamp should be greater than from timestamp.");
    InstrumentMapping instrumentMapping = Iterables.getOnlyElement(
        experimentRequest.getInstrumentMappings());
    String instrumentId = checkNotNullOrEmpty(instrumentMapping.getPriceInstrumentId());
    LOGGER.info(String.format("Loading prices for [%s] from [%d] to [%d].",
        instrumentId, experimentRequest.getFromTimestamp(), experimentRequest.getToTimestamp()));
    Iterable<PriceTick> priceTicks = apiClient.getPriceService().getPriceTicks(
        instrumentId, experimentRequest.getFromTimestamp(), experimentRequest.getToTimestamp());
    Collection<PriceTick> result = (priceTicks instanceof Collection)
        ? (Collection<PriceTick>) priceTicks : ImmutableList.copyOf(priceTicks);
    LOGGER.info(String.format("Loaded [%d] prices ticks.", result.size()));
    return result;
  }

  private void checkExperimentRequest(OptimisationRequest experimentRequest) {
    checkNotNull(experimentRequest);
    checkNotNull(experimentRequest.getParameters(), "Empty parameters.");
    checkArgument(!experimentRequest.getParameters().isEmpty(), "Empty parameters.");
    try {
      Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass(
          experimentRequest.getStrategyClass());
      checkArgument(Strategy.class.isAssignableFrom(clazz), String.format(
          "Class [%s] is not of type [%s].", experimentRequest.getStrategyClass(),
          Strategy.class.getName()));
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException(
          String.format("Class [%s] not found.", experimentRequest.getStrategyClass()));
    }
  }
}
