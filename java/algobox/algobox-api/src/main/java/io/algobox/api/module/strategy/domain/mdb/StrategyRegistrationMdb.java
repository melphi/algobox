package io.algobox.api.module.strategy.domain.mdb;

import com.google.common.base.Objects;
import io.algobox.api.module.strategy.domain.StrategyRegistration;
import io.algobox.strategy.InstrumentMapping;

import java.util.Collection;
import java.util.Map;

public final class StrategyRegistrationMdb implements StrategyRegistration {
  public static final String COLLECTION_STRATEGY_REGISTRATIONS = "strategyRegistrations";
  public static final String FIELD_INSTANCE_ID = "instanceId";
  public static final String FIELD_TITLE = "title";
  public static final String FIELD_STRATEGY_ID = "strategyId";

  private String instanceId;

  private String strategyId;

  private String title;

  private Map<String, String> parameters;

  private Collection<InstrumentMapping> instrumentsMapping;

  public StrategyRegistrationMdb() {
    // Intentionally empty.
  }

  public StrategyRegistrationMdb(StrategyRegistration strategyRegistration) {
    this(strategyRegistration.getInstanceId(), strategyRegistration.getStrategyId(),
        strategyRegistration.getTitle(), strategyRegistration.getParameters(),
        strategyRegistration.getInstrumentsMapping());
  }

  public StrategyRegistrationMdb(String instanceId, String strategyId, String tile,
      Map<String, String> parameters, Collection<InstrumentMapping> instrumentsMapping) {
    this.instanceId = instanceId;
    this.strategyId = strategyId;
    this.title = tile;
    this.parameters = parameters;
    this.instrumentsMapping = instrumentsMapping;
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
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StrategyRegistrationMdb that = (StrategyRegistrationMdb) o;
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
