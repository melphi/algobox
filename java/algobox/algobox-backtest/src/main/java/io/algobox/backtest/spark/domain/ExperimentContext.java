package io.algobox.backtest.spark.domain;

import io.algobox.strategy.InstrumentMapping;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public final class ExperimentContext implements Serializable {
  private Collection<InstrumentMapping> instrumentMappings;

  private String strategyClass;

  private Map<String, String> parameters;

  public ExperimentContext() {
    // Intentionally empty.
  }

  public ExperimentContext(Collection<InstrumentMapping> instrumentMappings,
      String strategyClass, Map<String, String> parameters) {
    this.instrumentMappings = instrumentMappings;
    this.strategyClass = strategyClass;
    this.parameters = parameters;
  }

  public Collection<InstrumentMapping> getInstrumentMappings() {
    return instrumentMappings;
  }

  public String getStrategyClass() {
    return strategyClass;
  }

  public Map<String, String> getParameters() {
    return parameters;
  }
}
