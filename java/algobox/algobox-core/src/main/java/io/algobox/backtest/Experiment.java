package io.algobox.backtest;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.algobox.connector.ConnectorException;
import io.algobox.indicator.IndicatorService;
import io.algobox.instrument.InstrumentService;
import io.algobox.order.OrderService;
import io.algobox.price.PriceTick;
import io.algobox.price.feed.CollectionPriceFeed;
import io.algobox.price.feed.PriceFeed;
import io.algobox.strategy.InstrumentMapping;
import io.algobox.strategy.Strategy;
import io.algobox.strategy.StrategyContext;
import io.algobox.strategy.StrategyEventService;
import io.algobox.util.DateTimeUtils;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;

public final class Experiment {
  private static final String DEFAULT_CONTEXT_TITLE = "Experiment";

  private final long latencyMilliseconds;
  private final PriceFeed priceFeed;
  private final Class<? extends Strategy> strategyClass;
  private final Map<String, String> strategyParameters;
  private final Collection<InstrumentMapping> instrumentsMapping;
  private final IndicatorService indicatorService;
  private final InstrumentService instrumentService;

  private Experiment(PriceFeed priceFeed, Class<? extends Strategy> strategyClass,
      Map<String, String> strategyParameters, Collection<InstrumentMapping> instrumentMapping,
      IndicatorService indicatorService, InstrumentService instrumentService,
      long latencyMilliseconds) {
    this.latencyMilliseconds = latencyMilliseconds;
    this.priceFeed = priceFeed;
    this.strategyClass = strategyClass;
    this.strategyParameters = ImmutableMap.copyOf(strategyParameters);
    this.instrumentsMapping = ImmutableList.copyOf(instrumentMapping);
    this.indicatorService = indicatorService;
    this.instrumentService = instrumentService;
  }

  @SuppressWarnings("unchecked")
  public static Experiment newExperiment(Collection<PriceTick> priceTicks,
      String strategyClassName, Map<String, String> strategyParameters,
      Collection<InstrumentMapping> instrumentMapping, IndicatorService indicatorService,
      InstrumentService instrumentService, long latencyMilliseconds) {
    Class<? extends Strategy> strategyClass;
    try {
      strategyClass = (Class<? extends Strategy>) Class.forName(strategyClassName);
    } catch (Exception e) {
      throw new IllegalArgumentException(String.format(
          "Error while creating class [%s]: [%s].", strategyClassName, e.getMessage()), e);
    }
    PriceFeed priceFeed = new CollectionPriceFeed(priceTicks);
    return new Experiment(priceFeed, strategyClass, strategyParameters, instrumentMapping,
        indicatorService, instrumentService, latencyMilliseconds);
  }

  public static Experiment newExperiment(PriceFeed priceFeed,
      Class<? extends Strategy> strategyClass, Map<String, String> strategyParameters,
      Collection<InstrumentMapping> instrumentMapping, IndicatorService indicatorService,
      InstrumentService instrumentService, long latencyMilliseconds) {
    return new Experiment(priceFeed, strategyClass, strategyParameters, instrumentMapping,
        indicatorService, instrumentService, latencyMilliseconds);
  }

  public ExperimentResult run() {
    ExperimentOrderService orderService =
        new ExperimentOrderService(instrumentService, latencyMilliseconds);
    ExperimentStrategyEventService strategyEventService = new ExperimentStrategyEventService();
    Strategy strategy = createStrategy(strategyClass, orderService, strategyEventService);
    long startTime = DateTimeUtils.getCurrentUtcTimestampMilliseconds();
    long processedTicks = processTicks(strategy, orderService);
    long endTime = DateTimeUtils.getCurrentUtcTimestampMilliseconds();
    try {
      return createExperimentResult(
          orderService, strategyEventService, processedTicks, endTime - startTime);
    } catch (ConnectorException e) {
      throw new IllegalArgumentException(e);
    }
  }

  private long processTicks(Strategy strategy, ExperimentOrderService orderService) {
    long count = 0;
    for (PriceTick priceTick: priceFeed.getPrices()) {
      orderService.onPriceTick(ExperimentOrderService.DEFAULT_CONNECTION_ID, priceTick);
      strategy.onPriceTick(priceTick);
      count += 1;
    }
    return count;
  }

  private ExperimentResult createExperimentResult(ExperimentOrderService orderService,
      ExperimentStrategyEventService strategyEventService, long processedTicksCount,
      long processedTimeMilliseconds) throws ConnectorException {
    int closedTradesCount = orderService.getClosedTradesCount();
    double closedTradesPlPips = orderService.getClosedTradesPlPips();
    int activeTradesCount = orderService.getActiveTradesCount();
    double activeTradesPlPips = orderService.getActiveTradesPlPips();
    int activeOrdersCount = orderService.getActiveOrdersCount();
    return new ExperimentResult(closedTradesCount, closedTradesPlPips, activeTradesCount,
        activeTradesPlPips, processedTicksCount, processedTimeMilliseconds,
        strategyEventService.getAllStrategyEvents(), activeOrdersCount);
  }

  private Strategy createStrategy(Class<? extends Strategy> strategyClass,
      OrderService orderService, StrategyEventService strategyEventService) {
    checkArgument(strategyClass.getConstructors().length == 1,
        "Expected exactly one default constructor for the dummy.");
    Constructor strategyConstructor = strategyClass.getConstructors()[0];
    StrategyContext strategyContext = new StrategyContext(orderService, indicatorService,
        instrumentService, strategyEventService, strategyParameters, instrumentsMapping,
        UUID.randomUUID().toString(), DEFAULT_CONTEXT_TITLE);
    try {
      return (Strategy) strategyConstructor.newInstance(strategyContext);
    } catch (Exception e) {
      throw new IllegalArgumentException(String.format("Can not instantiate dummy [%s]: [%s]",
          strategyClass.getName(), e.getMessage()), e);
    }
  }
}
