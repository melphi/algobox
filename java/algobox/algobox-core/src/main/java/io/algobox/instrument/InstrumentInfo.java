package io.algobox.instrument;

import java.io.Serializable;
import java.util.Objects;

public final class InstrumentInfo implements Serializable {
  private String instrumentId;

  private String instrumentType;

  private String name;

  public InstrumentInfo() {
    // Intentionally empty.
  }

  public InstrumentInfo(String instrumentId, String instrumentType, String name) {
    this.instrumentId = instrumentId;
    this.instrumentType = instrumentType;
    this.name = name;
  }

  public String getInstrumentId() {
    return instrumentId;
  }

  public String getInstrumentType() {
    return instrumentType;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "Instrument{" +
        "instrumentId='" + instrumentId + '\'' +
        ", instrumentType='" + instrumentType + '\'' +
        ", name='" + name + '\'' +
        '}';
  }

  @Override
  public int hashCode() {
    return Objects.hash(instrumentId, instrumentType, name);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof InstrumentInfo)) {
      return false;
    }
    InstrumentInfo other = (InstrumentInfo) obj;
    return Objects.equals(instrumentId, other.getInstrumentId())
        && Objects.equals(instrumentType, other.getInstrumentType())
        && Objects.equals(name, other.getName());
  }
}
