package io.algobox.instrument;

import java.io.Serializable;
import java.util.Objects;

public final class Instrument implements Serializable {
  private String instrumentId;

  private String instrumentType;

  private String description;

  public Instrument() {
    // Intentionally empty.
  }

  public Instrument(String instrumentId, String instrumentType, String description) {
    this.instrumentId = instrumentId;
    this.instrumentType = instrumentType;
    this.description = description;
  }

  public String getInstrumentId() {
    return instrumentId;
  }

  public String getInstrumentType() {
    return instrumentType;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public int hashCode() {
    return Objects.hash(instrumentId, instrumentType, description);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Instrument)) {
      return false;
    }
    Instrument other = (Instrument) obj;
    return Objects.equals(instrumentId, other.getInstrumentId())
        && Objects.equals(instrumentType, other.getInstrumentType())
        && Objects.equals(description, other.getDescription());
  }
}
