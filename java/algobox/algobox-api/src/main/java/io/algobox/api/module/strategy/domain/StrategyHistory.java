package io.algobox.api.module.strategy.domain;

import io.algobox.strategy.InstrumentMapping;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public interface StrategyHistory extends Serializable {
  String getInstanceId();

  String getStrategyId();

  String getTitle();

  Map<String, String> getParameters();

  Collection<InstrumentMapping> getInstrumentsMapping();

  long getTimestampUtc();

  String getExceptionMessage();

  String getExceptionStack();

  long getReceivedTicks();
}
