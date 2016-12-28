package io.algobox.microservice.container.context;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class ConsulKeyValue {
  private String key;

  private String value;

  public ConsulKeyValue() {
    // Intentionally empty.
  }

  public ConsulKeyValue(String key, String value) {
    this.key = key;
    this.value = value;
  }

  @JsonProperty(value = "Key", required = true)
  public String getKey() {
    return key;
  }

  @JsonProperty(value = "Value")
  public String getValue() {
    return value;
  }
}
