package io.algobox.connector;

import java.io.Serializable;

public enum ConnectionStatus implements Serializable {
  CONNECTED("CONNECTED"),
  CONNECTING("CONNECTING"),
  DISCONNECTING("DISCONNECTING"),
  DISCONNECTED("DISCONNECTED");

  private final String value;

  ConnectionStatus(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
