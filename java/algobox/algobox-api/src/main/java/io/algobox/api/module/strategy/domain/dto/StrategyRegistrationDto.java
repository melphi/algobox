package io.algobox.api.module.strategy.domain.dto;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import io.algobox.api.module.strategy.domain.StrategyRegistration;
import io.algobox.strategy.InstrumentMapping;

import java.util.Collection;
import java.util.Map;

public final class StrategyRegistrationDto implements StrategyRegistration {
  private String instanceId;

  private String strategyId;

  private String title;

  private Map<String, String> parameters;

  private Collection<InstrumentMapping> instrumentsMapping;

  public StrategyRegistrationDto() {
    // Intentionally empty.
  }

  public StrategyRegistrationDto(StrategyRegistration strategyRegistration) {
    this(strategyRegistration.getInstanceId(), strategyRegistration.getStrategyId(),
        strategyRegistration.getTitle(), strategyRegistration.getParameters(),
        strategyRegistration.getInstrumentsMapping());
  }

  public StrategyRegistrationDto(String instanceId, String strategyId, String title,
      Map<String, String> parameters, Collection<InstrumentMapping> instrumentsMapping) {
    this.instanceId = instanceId;
    this.strategyId = strategyId;
    this.title = title;
    this.parameters = parameters;
    this.instrumentsMapping = instrumentsMapping;
  }

  @Override
  public String getInstanceId() {
    return instanceId;
  }

  @Override
  public String getStrategyId() {
    return strategyId;
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public Map<String, String> getParameters() {
    return parameters;
  }

  @Override
  public Collection<InstrumentMapping> getInstrumentsMapping() {
    return instrumentsMapping;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("instanceId", instanceId)
        .add("strategyId", strategyId)
        .add("title", title)
        .add("parameters", parameters)
        .add("instrumentsMapping", instrumentsMapping)
        .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StrategyRegistrationDto that = (StrategyRegistrationDto) o;
    return Objects.equal(instanceId, that.instanceId) &&
        Objects.equal(strategyId, that.strategyId) &&
        Objects.equal(title, that.title) &&
        Objects.equal(parameters, that.parameters) &&
        Objects.equal(instrumentsMapping, that.instrumentsMapping);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(instanceId, strategyId, title, parameters, instrumentsMapping);
  }
}
