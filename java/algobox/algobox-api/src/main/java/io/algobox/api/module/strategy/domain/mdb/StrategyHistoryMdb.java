package io.algobox.api.module.strategy.domain.mdb;

import io.algobox.api.module.strategy.domain.StrategyHistory;
import io.algobox.api.module.strategy.domain.StrategyRegistration;
import io.algobox.strategy.InstrumentMapping;
import io.algobox.util.ExceptionUtils;

import java.util.Collection;
import java.util.Map;

public final class StrategyHistoryMdb implements StrategyHistory {
  public static final String COLLECTION_STRATEGY_HISTORIES = "strategyHistories";
  public static final String FIELD_INSTANCE_ID = "instanceId";
  public static final String FIELD_TIMESTAMP = "timestampUtc";

  private String instanceId;

  private String strategyId;

  private String title;

  private Map<String, String> parameters;

  private Collection<InstrumentMapping> instrumentsMapping;

  private long timestampUtc;

  private String exceptionMessage;

  private String exceptionStack;

  private long receivedTicks;

  public StrategyHistoryMdb() {
    // Intentionally empty;
  }

  public StrategyHistoryMdb(String instanceId, String strategyId, String title,
      Map<String, String> parameters, Collection<InstrumentMapping> instrumentMappings,
      long timestampUtc, String exceptionMessage, String exceptionStack, long receivedTicks) {
    this.instanceId = instanceId;
    this.strategyId = strategyId;
    this.title = title;
    this.parameters  = parameters;
    this.instrumentsMapping = instrumentMappings;
    this.timestampUtc = timestampUtc;
    this.exceptionMessage = exceptionMessage;
    this.exceptionStack = exceptionStack;
    this.receivedTicks = receivedTicks;
  }

  public StrategyHistoryMdb(StrategyRegistration strategyRegistration, long timestampUtc,
      Throwable exception, long receivedTicks) {
    this(strategyRegistration.getInstanceId(), strategyRegistration.getStrategyId(),
        strategyRegistration.getTitle(), strategyRegistration.getParameters(),
        strategyRegistration.getInstrumentsMapping(), timestampUtc,
        exception != null ? exception.getMessage() : null,
        exception != null ? ExceptionUtils.stackTraceToString(exception) : null,
        receivedTicks);
  }

  @Override
  public String getInstanceId() {
    return instanceId;
  }

  public void setInstanceId(String instanceId) {
    this.instanceId = instanceId;
  }

  @Override
  public String getStrategyId() {
    return strategyId;
  }

  public void setStrategyId(String strategyId) {
    this.strategyId = strategyId;
  }

  @Override
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public Map<String, String> getParameters() {
    return parameters;
  }

  public void setParameters(Map<String, String> parameters) {
    this.parameters = parameters;
  }

  @Override
  public Collection<InstrumentMapping> getInstrumentsMapping() {
    return instrumentsMapping;
  }

  public void setInstrumentsMapping(Collection<InstrumentMapping> instrumentsMapping) {
    this.instrumentsMapping = instrumentsMapping;
  }

  @Override
  public long getTimestampUtc() {
    return timestampUtc;
  }

  public void setTimestampUtc(long timestampUtc) {
    this.timestampUtc = timestampUtc;
  }

  @Override
  public String getExceptionMessage() {
    return exceptionMessage;
  }

  public void setExceptionMessage(String exceptionMessage) {
    this.exceptionMessage = exceptionMessage;
  }

  @Override
  public String getExceptionStack() {
    return exceptionStack;
  }

  public void setExceptionStack(String exceptionStack) {
    this.exceptionStack = exceptionStack;
  }

  @Override
  public long getReceivedTicks() {
    return receivedTicks;
  }

  public void setReceivedTicks(long receivedTicks) {
    this.receivedTicks = receivedTicks;
  }
}
