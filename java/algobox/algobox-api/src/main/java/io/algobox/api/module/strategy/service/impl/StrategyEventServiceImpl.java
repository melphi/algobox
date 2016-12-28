package io.algobox.api.module.strategy.service.impl;

import io.algobox.api.module.strategy.dao.impl.StrategyEventDao;
import io.algobox.price.PriceTick;
import io.algobox.strategy.StrategyEvent;
import io.algobox.strategy.StrategyEventDto;
import io.algobox.strategy.StrategyEventService;
import io.algobox.strategy.StrategyEventType;
import io.algobox.util.DateTimeUtils;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;
import static io.algobox.util.MorePreconditions.checkPagination;

@Service
public final class StrategyEventServiceImpl implements StrategyEventService {
  private static final Logger LOGGER = LoggerFactory.getLogger(StrategyEventServiceImpl.class);

  private final StrategyEventDao strategyEventDao;

  @Inject
  public StrategyEventServiceImpl(StrategyEventDao strategyEventDao) {
    this.strategyEventDao = strategyEventDao;
  }

  @Override
  public void logEventAsync(String strategyInstanceId, StrategyEventType strategyEventType,
      PriceTick priceTick, String message, String data) {
    checkNotNull(strategyEventType, "Empty instance id.");
    checkNotNull(strategyEventType, "Empty dummy event type.");
    long timestamp = DateTimeUtils.getCurrentUtcTimestampMilliseconds();
    StrategyEvent event = new StrategyEventDto(
        timestamp, strategyEventType, priceTick, message, data);
    new LogEventThread(strategyInstanceId, event).start();
  }

  @Override
  public Collection<StrategyEvent> getInstanceEventsLog(
      String instanceId, int pageNumber, int pageSize) {
    checkNotNullOrEmpty(instanceId);
    checkPagination(pageNumber, pageSize);
    return strategyEventDao.findEventsLog(instanceId, pageNumber, pageSize);
  }

  private final class LogEventThread extends Thread {
    private final StrategyEvent strategyEvent;
    private final String instanceId;

    LogEventThread(String instanceId, StrategyEvent strategyEvent) {
      this.strategyEvent = strategyEvent;
      this.instanceId = instanceId;
    }

    @Override
    public void run() {
      try {
        strategyEventDao.logEvent(instanceId, strategyEvent);
      } catch (Exception e) {
        LOGGER.error(String.format(
            "Error while logging event [%s]: [%s].", strategyEvent, e.getMessage()), e);
      }
    }
  }
}
