package io.algobox.api.module.strategy.service.impl;

import avro.shaded.com.google.common.base.Joiner;
import avro.shaded.com.google.common.collect.ImmutableSet;
import avro.shaded.com.google.common.collect.Maps;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import io.algobox.api.component.exception.ValueNotFound;
import io.algobox.api.module.strategy.dao.impl.StrategyHistoryDao;
import io.algobox.api.module.strategy.dao.impl.StrategyRegistrationDao;
import io.algobox.api.module.strategy.domain.StrategyHistory;
import io.algobox.api.module.strategy.domain.StrategyInfo;
import io.algobox.api.module.strategy.domain.StrategyRegistration;
import io.algobox.api.module.strategy.domain.dto.StrategyInfoDto;
import io.algobox.api.module.strategy.domain.dto.StrategyRegistrationDto;
import io.algobox.api.module.strategy.domain.dto.StrategyRegistrationRequestDto;
import io.algobox.api.module.strategy.service.StrategyManager;
import io.algobox.api.module.strategy.service.StrategyService;
import io.algobox.indicator.IndicatorService;
import io.algobox.instrument.InstrumentService;
import io.algobox.order.OrderService;
import io.algobox.price.PriceTick;
import io.algobox.strategy.Strategy;
import io.algobox.strategy.StrategyContext;
import io.algobox.strategy.StrategyEventService;
import io.algobox.strategy.StrategyEventType;
import io.algobox.util.DateTimeUtils;
import io.algobox.util.ExceptionUtils;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static avro.shaded.com.google.common.collect.Iterables.elementsEqual;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;
import static io.algobox.util.MorePreconditions.checkPagination;

@Service
public final class StrategyServiceImpl implements StrategyService {
  private static final Logger LOGGER = LoggerFactory.getLogger(StrategyServiceImpl.class);

  private final StrategyEventService strategyEventService;
  private final StrategyManager strategyManager;
  private final OrderService orderService;
  private final IndicatorService indicatorService;
  private final InstrumentService instrumentService;
  private final StrategyHistoryDao strategyHistoryDao;
  private final StrategyRegistrationDao strategyRegistrationDao;
  private final Map<String, Strategy> activeStrategies = Maps.newHashMap();
  private final Lock activeStrategiesLock = new ReentrantLock();

  @Inject
  public StrategyServiceImpl(StrategyEventService strategyEventService,
      StrategyRegistrationDao strategyRegistrationDao, StrategyHistoryDao strategyHistoryDao,
      OrderService orderService, IndicatorService indicatorService,
      InstrumentService instrumentService, StrategyManager strategyManager) {
    this.strategyEventService = strategyEventService;
    this.strategyRegistrationDao = strategyRegistrationDao;
    this.strategyHistoryDao = strategyHistoryDao;
    this.orderService = orderService;
    this.indicatorService = indicatorService;
    this.instrumentService = instrumentService;
    this.strategyManager = strategyManager;
    initStrategies();
  }

  @Override
  public void onPriceTick(String source, PriceTick priceTick) {
    activeStrategiesLock.lock();
    try {
      for (Strategy strategy : activeStrategies.values()) {
        StrategyContext strategyContext = strategy.getStrategyContext();
        if (strategyContext.getOnlyPriceInstrumentId().equals(priceTick.getInstrument())
            && strategyContext.getOnlyPriceConnectorId().equals(source)) {
          new ProcessPriceTickThread(strategy, priceTick).start();
        }
      }
    } catch (Exception e) {
      LOGGER.error(String.format("Unexpected error while dispatching price tick [%s]: [%s].",
          priceTick, e.getMessage()), e);
    } finally {
      activeStrategiesLock.unlock();
    }
  }

  @Override
  public Collection<StrategyRegistration> getActiveInstances() {
    // TODO: Improve this code to make sure that the strategies in memory match the one in db.
    Set<String> instancesId;
    activeStrategiesLock.lock();
    try {
      instancesId = ImmutableSet.copyOf(activeStrategies.keySet());
    } finally {
      activeStrategiesLock.unlock();
    }
    Collection<StrategyRegistration> strategies = ImmutableList.copyOf(
        strategyRegistrationDao.findAll());
    Set<String> strategiesId = strategies.stream()
        .map(StrategyRegistration::getInstanceId)
        .sorted()
        .collect(Collectors.toSet());
    instancesId = instancesId.stream()
        .sorted()
        .collect(Collectors.toSet());
    checkArgument(Iterables.elementsEqual(instancesId, strategiesId), String.format(
        "Strategies loaded do not match strategies in database. Loaded: [%s], Database: [%s]",
        Joiner.on(", ").join(instancesId), Joiner.on(", ").join(strategiesId)));
    return strategies;
  }

  @Override
  public Collection<StrategyHistory> getInstancesHistory(int pageNumber, int pageSize) {
    checkPagination(pageNumber, pageSize);
    return ImmutableList.copyOf(strategyHistoryDao.findAll(pageNumber, pageSize));
  }

  @Override
  public String createInstance(StrategyRegistrationRequestDto strategyCreationRequest) {
    LOGGER.info(String.format("Registering dummy [%s].", strategyCreationRequest));
    StrategyRegistrationDto strategyRegistration =
        createStrategyRegistrationIfValid(strategyCreationRequest);
    if (similarStrategyExistsInDao(strategyRegistration)) {
      throw new IllegalArgumentException(String.format(
          "A dummy similar to [%s] is already present in database.", strategyRegistration));
    }
    try {
      String instanceId = registerStrategy(strategyRegistration);
      strategyRegistrationDao.save(strategyRegistration);
      return instanceId;
    } catch (Exception e) {
      removeInstance(strategyRegistration.getInstanceId());
      throw new IllegalArgumentException(
          String.format("Error [%s] while registering dummy [%s].",
              e.getMessage(), strategyCreationRequest));
    }
  }

  @Override
  public void removeInstance(String instanceId) {
    removeStrategy(instanceId, Optional.empty());
  }

  public void removeStrategy(String instanceId, Optional<Throwable> exception) {
    checkNotNullOrEmpty(instanceId);
    Strategy strategy;
    activeStrategiesLock.lock();
    try {
      strategy = activeStrategies.remove(instanceId);
    } finally {
      activeStrategiesLock.unlock();
    }
    try {
      StrategyRegistration strategyRegistration =
          strategyRegistrationDao.findByInstanceId(instanceId);
      if (strategy != null && strategyRegistration != null) {
        strategyHistoryDao.save(strategyRegistration,
            DateTimeUtils.getCurrentUtcTimestampMilliseconds(), exception,
            strategy.getStrategyContext().getReceivedTicks());
      }
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      throw new IllegalArgumentException(e);
    } finally {
      strategyRegistrationDao.deleteById(instanceId);
      if (strategy != null) {
        LOGGER.info(String.format(
            "Strategy [%s], instance [%s] removed.", strategy.getStrategyId(), instanceId));
      }
    }
  }

  @Override
  public StrategyInfo getInstanceStatus(String instanceId) {
    checkNotNullOrEmpty(instanceId);
    activeStrategiesLock.lock();
    Strategy strategy = null;
    try {
      strategy = activeStrategies.get(instanceId);
    } finally {
      activeStrategiesLock.unlock();
    }
    if (strategy == null) {
      throw new ValueNotFound(String.format("Strategy [%s] not found.", instanceId));
    }
    return new StrategyInfoDto(
        strategy.getStrategyContext().getStatus(),
        strategy.getStrategyContext().getReceivedTicks());
  }

  private boolean similarStrategyExistsInDao(StrategyRegistrationDto strategyRegistration) {
    if (strategyRegistrationDao.exists(strategyRegistration.getInstanceId())) {
      return true;
    }
    for (StrategyRegistration registration:
        strategyRegistrationDao.findByStrategyId(strategyRegistration.getStrategyId())) {
      if (strategyRegistration.getStrategyId().equals(registration.getStrategyId())
          && strategyRegistration.getTitle().equals(registration.getTitle())
          && elementsEqual(strategyRegistration.getParameters().entrySet(),
              registration.getParameters().entrySet())
          && elementsEqual(strategyRegistration.getInstrumentsMapping(),
              registration.getInstrumentsMapping())) {
        return true;
      }
    }
    return false;
  }

  private void initStrategies() {
    for (StrategyRegistration strategy: strategyRegistrationDao.findAll()) {
      registerStrategy(strategy);
    }
  }

  private StrategyRegistrationDto createStrategyRegistrationIfValid(
      StrategyRegistrationRequestDto request) {
    checkNotNull(request);
    checkNotNullOrEmpty(request.getStrategyId(), "Missing dummy id.");
    checkNotNullOrEmpty(request.getTitle(), "Missing title.");
    checkNotNull(request.getParameters(), "Missing parameters.");
    checkNotNull(request.getInstrumentsMapping(), "Missing instrument mapping");
    return new StrategyRegistrationDto(
        UUID.randomUUID().toString(),
        request.getStrategyId(),
        request.getTitle(),
        ImmutableMap.copyOf(request.getParameters()),
        ImmutableList.copyOf(request.getInstrumentsMapping()));
  }

  private StrategyContext createStrategyContext(
      StrategyRegistration strategyRegistration, String instanceId) {
    return new StrategyContext(orderService, indicatorService, instrumentService,
        strategyEventService, strategyRegistration.getParameters(),
        strategyRegistration.getInstrumentsMapping(), instanceId, strategyRegistration.getTitle());
  }

  private String registerStrategy(StrategyRegistration registration) {
    Class<? extends Strategy> strategyClass =
        strategyManager.getStrategyById(registration.getStrategyId());
    checkNotNull(strategyClass, String.format(
        "Strategy [%s] is not registered.", registration.getStrategyId()));
    checkArgument(strategyClass.getConstructors().length == 1, String.format(
        "Strategy [%s] should implement only one constructor but [%d] was found.",
        registration.getStrategyId(), strategyClass.getConstructors().length));
    activeStrategiesLock.lock();
    try {
      if (activeStrategies.containsKey(registration.getInstanceId())) {
        throw new IllegalArgumentException(String.format(
            "Strategy instance [%s] already registered.", registration.getInstanceId()));
      }
    } finally {
      activeStrategiesLock.unlock();
    }
    Constructor strategyConstructor = strategyClass.getConstructors()[0];
    Strategy strategy = null;
    try {
      StrategyContext strategyContext = createStrategyContext(
          registration, registration.getInstanceId());
      strategy = (Strategy) strategyConstructor.newInstance(strategyContext);
      LOGGER.info(String.format("Strategy [%s] [%s] registered with id [%s].",
          strategy.getStrategyId(), strategyContext.getTitle(), strategyContext.getInstanceId()));
    } catch (Exception e) {
      if (strategy != null) {
        removeInstance(strategy.getStrategyId());
      }
      String error = e.getMessage();
      if (Strings.isNullOrEmpty(error) && e.getCause() != null) {
        error = e.getCause().getMessage();
      }
      throw new IllegalArgumentException(String.format(
          "Error while creating dummy [%s]: [%s].", registration.getStrategyId(), error));
    }
    activeStrategiesLock.lock();
    try {
      activeStrategies.put(strategy.getStrategyContext().getInstanceId(), strategy);
    } finally {
      activeStrategiesLock.unlock();
    }
    return strategy.getStrategyContext().getInstanceId();
  }

  private final class ProcessPriceTickThread extends Thread {
    private final Strategy strategy;
    private final PriceTick priceTick;

    ProcessPriceTickThread(final Strategy strategy, final PriceTick priceTick) {
      this.strategy = strategy;
      this.priceTick = priceTick;
    }

    @Override
    public void run() {
      try {
        strategy.onPriceTick(priceTick);
      } catch (Exception e) {
        strategy.getStrategyContext().logEventAsync(StrategyEventType.ERROR, priceTick,
            e.getMessage(), ExceptionUtils.stackTraceToString(e));
        LOGGER.error(String.format(
            "Strategy [%s] instance [%s]. Error while processing tick [%s]: [%s]",
            strategy.getStrategyId(), strategy.getStrategyContext().getInstanceId(), priceTick,
            e.getMessage()), e);
        removeStrategy(strategy.getStrategyContext().getInstanceId(), Optional.of(e));
      }
    }
  }
}
