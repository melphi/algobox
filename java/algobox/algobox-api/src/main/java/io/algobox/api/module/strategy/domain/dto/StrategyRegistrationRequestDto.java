package io.algobox.api.module.strategy.domain.dto;

import com.google.common.base.MoreObjects;
import io.algobox.strategy.InstrumentMapping;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public final class StrategyRegistrationRequestDto implements Serializable {
  private String strategyId;

  private String title;

  private Map<String, String> parameters;

  private Collection<InstrumentMapping> instrumentsMapping;

  public StrategyRegistrationRequestDto() {
    // Intentionally empty.
  }

  public StrategyRegistrationRequestDto(String strategyId, String title,
      Map<String, String> parameters, Collection<InstrumentMapping> instrumentsMapping) {
    this.strategyId = strategyId;
    this.title = title;
    this.parameters = parameters;
    this.instrumentsMapping = instrumentsMapping;
  }

  public String getStrategyId() {
    return strategyId;
  }

  public String getTitle() {
    return title;
  }

  public Map<String, String> getParameters() {
    return parameters;
  }

  public Collection<InstrumentMapping> getInstrumentsMapping() {
    return instrumentsMapping;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("strategyId", strategyId)
        .add("title", title)
        .add("parameters", parameters)
        .add("instrumentsMapping", instrumentsMapping)
        .toString();
  }
}
