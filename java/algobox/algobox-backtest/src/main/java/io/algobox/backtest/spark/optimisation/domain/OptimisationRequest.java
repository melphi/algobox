package io.algobox.backtest.spark.optimisation.domain;

import io.algobox.strategy.InstrumentMapping;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public final class OptimisationRequest implements Serializable {
  private Collection<InstrumentMapping> instrumentMappings;

  private Long fromTimestamp;

  private Long toTimestamp;

  private String strategyClass;

  private Map<String, ParameterValues> parameters;

  private String outputPath;

  public OptimisationRequest() {
    // Intentionally empty.
  }

  public OptimisationRequest(Collection<InstrumentMapping> instrumentMappings, Long fromTimestamp,
      Long toTimestamp, String strategyClass, Map<String, ParameterValues> parameters,
      String outputPath) {
    this.instrumentMappings = instrumentMappings;
    this.fromTimestamp = fromTimestamp;
    this.toTimestamp = toTimestamp;
    this.strategyClass = strategyClass;
    this.parameters = parameters;
    this.outputPath = outputPath;
  }

  public String getStrategyClass() {
    return strategyClass;
  }

  public Map<String, ParameterValues> getParameters() {
    return parameters;
  }

  public Long getFromTimestamp() {
    return fromTimestamp;
  }

  public Long getToTimestamp() {
    return toTimestamp;
  }

  public Collection<InstrumentMapping> getInstrumentMappings() {
    return instrumentMappings;
  }

  public String getOutputPath() {
    return outputPath;
  }
}
