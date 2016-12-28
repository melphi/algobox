package io.algobox.microservice.container.domain;

import java.io.Serializable;

public final class HealthStatus implements Serializable {
  private String message;

  public HealthStatus() {
    // Intentionally empty.
  }

  public HealthStatus(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
