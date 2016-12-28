package io.algobox.connector.domain.dto;

import io.algobox.connector.domain.ConnectionRegistration;

import java.util.Map;

public final class ConnectionRegistrationDto implements ConnectionRegistration {
  private String connectionId;

  private String connectorId;

  private Map<String, String> parameters;

  private Boolean keepAlive;

  public ConnectionRegistrationDto() {
    // Intentionally empty.
  }

  public ConnectionRegistrationDto(ConnectionRegistration connectionRegistration) {
    this(connectionRegistration.getConnectionId(),
        connectionRegistration.getConnectorId(),
        connectionRegistration.getParameters(),
        connectionRegistration.getKeepAlive());
  }

  public ConnectionRegistrationDto(
      String connectionId, String connectorId, Map<String, String> parameters, Boolean keepAlive) {
    this.connectionId = connectionId;
    this.connectorId = connectorId;
    this.parameters = parameters;
    this.keepAlive = keepAlive;
  }

  public String getConnectionId() {
    return connectionId;
  }

  public String getConnectorId() {
    return connectorId;
  }

  public Map<String, String> getParameters() {
    return parameters;
  }

  public Boolean getKeepAlive() {
    return keepAlive;
  }
}
