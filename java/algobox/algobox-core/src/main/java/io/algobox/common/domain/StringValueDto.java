package io.algobox.common.domain;

import java.io.Serializable;

public final class StringValueDto implements Serializable {
  private String value;

  public StringValueDto() {
    // Intentionally empty.
  }

  public StringValueDto(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
