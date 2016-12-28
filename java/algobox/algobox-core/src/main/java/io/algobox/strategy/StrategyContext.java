package io.algobox.strategy;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import io.algobox.indicator.IndicatorService;
import io.algobox.instrument.InstrumentService;
import io.algobox.order.OrderRequest;
import io.algobox.order.OrderService;
import io.algobox.price.PriceTick;
import io.algobox.util.JsonUtils;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;

public final class StrategyContext {
  private final OrderService orderService;
  private final IndicatorService indicatorService;
  private final InstrumentService instrumentService;
  private final StrategyEventService strategyEventService;
  private final Map<String, String> parameters;
  private final String instanceId;
  private final String title;
  private final String onlyPriceConnectorId;
  private final String onlyPriceInstrumentId;
  private final String onlyOrderConnectorId;
  private final String onlyOrderInstrumentId;
  private final AtomicLong receivedTicks = new AtomicLong();

  private volatile StrategyStatus status = StrategyStatus.UNDEFINED;

  public StrategyContext(OrderService orderService, IndicatorService indicatorService,
      InstrumentService instrumentService, StrategyEventService strategyEventService,
      Map<String, String> parameters, Collection<InstrumentMapping> instrumentMappings,
      String instanceId, String title) {
    this.instanceId = instanceId;
    this.title = title;
    this.orderService = checkNotNull(orderService);
    this.indicatorService = checkNotNull(indicatorService);
    this.instrumentService = checkNotNull(instrumentService);
    this.strategyEventService = checkNotNull(strategyEventService);
    this.parameters = ImmutableMap.copyOf(parameters);
    InstrumentMapping instrumentMapping = Iterables.getOnlyElement(instrumentMappings);
    this.onlyPriceConnectorId  = checkNotNullOrEmpty(instrumentMapping.getPriceConnectionId());
    this.onlyPriceInstrumentId = checkNotNullOrEmpty(instrumentMapping.getPriceInstrumentId());
    this.onlyOrderConnectorId = checkNotNullOrEmpty(instrumentMapping.getOrderConnectionId());
    this.onlyOrderInstrumentId = checkNotNullOrEmpty(instrumentMapping.getOrderInstrumentId());
  }

  public IndicatorService getIndicatorService() {
    return indicatorService;
  }

  public InstrumentService getInstrumentService() {
    return instrumentService;
  }

  public Map<String, Object> getParameters() {
    return ImmutableMap.copyOf(parameters);
  }

  public String getRequiredString(String parameter) {
    return getRequiredValue(parameter);
  }

  public Double getRequiredDouble(String parameter) {
    return Double.valueOf(getRequiredValue(parameter));
  }

  public Integer getRequiredInteger(String parameter) {
    return Integer.valueOf(getRequiredValue(parameter));
  }

  public String getInstanceId() {
    return instanceId;
  }

  public String getTitle() {
    return title;
  }

  public String getOnlyPriceConnectorId() {
    return onlyPriceConnectorId;
  }

  public String getOnlyPriceInstrumentId() {
    return onlyPriceInstrumentId;
  }

  public String getOnlyOrderConnectorId() {
    return onlyOrderConnectorId;
  }

  public String getOnlyOrderInstrumentId() {
    return onlyOrderInstrumentId;
  }

  public StrategyStatus getStatus() {
    return status;
  }

  public void setStatus(final StrategyStatus status) {
    this.status = status;
  }

  public void logEventAsync(final StrategyEventType strategyEventType, final PriceTick priceTick,
      final String message, final Object data) {
    strategyEventService.logEventAsync(
        instanceId, strategyEventType, priceTick, message, JsonUtils.toJson(data));
  }

  public void sendOrderAsync(OrderRequest orderRequest) {
    try {
      orderService.sendOrderAsync(orderRequest);
    } finally {
      logEventAsync(StrategyEventType.ORDER_SENT, orderRequest.getPriceTick(), String.format(
          "Order sent, instrument [%s]:[%s], amount [%s], direction [%s].",
          orderRequest.getInstrumentId(), orderRequest.getConnectionId(), orderRequest.getAmount(),
          orderRequest.getOpenStrategy().getOrderDirection()), orderRequest);
    }
  }

  public void incrementReceivedTicks() {
    receivedTicks.incrementAndGet();
  }

  public long getReceivedTicks() {
    return receivedTicks.get();
  }

  private String getRequiredValue(String parameter) {
    String value = parameters.get(parameter);
    return checkNotNullOrEmpty(value, String.format("Parameter [%s] not found.", parameter));
  }
}
