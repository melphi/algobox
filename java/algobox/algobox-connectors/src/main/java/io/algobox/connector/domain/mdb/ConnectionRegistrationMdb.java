package io.algobox.connector.domain.mdb;

import io.algobox.connector.domain.ConnectionRegistration;

import java.util.Map;

public final class ConnectionRegistrationMdb implements ConnectionRegistration {
  public static final String COLLECTION_CONNECTION_REGISTRATIONS = "connectionRegistrations";
  public static final String FIELD_CONNECTOR_ID = "connectorId";
  public static final String FIELD_CONNECTION_ID = "connectionId";
  public static final String FIELD_KEEP_ALIVE = "keepAlive";
  public static final String FIELD_PARAMETERS = "parameters";

  private String connectionId;

  private String connectorId;

  private Map<String, String> parameters;

  private Boolean keepAlive;

  public ConnectionRegistrationMdb(
      String connectionId, String connectorId, Map<String, String> parameters, Boolean keepAlive) {
    this.connectionId = connectionId;
    this.connectorId = connectorId;
    this.parameters = parameters;
    this.keepAlive = keepAlive;
  }

  public String getConnectionId() {
    return connectionId;
  }

  public void setConnectionId(String connectionId) {
    this.connectionId = connectionId;
  }

  public String getConnectorId() {
    return connectorId;
  }

  public void setConnectorId(String connectorId) {
    this.connectorId = connectorId;
  }

  public Map<String, String> getParameters() {
    return parameters;
  }

  public void setParameters(Map<String, String> parameters) {
    this.parameters = parameters;
  }

  public Boolean getKeepAlive() {
    return keepAlive;
  }

  public void setKeepAlive(Boolean keepAlive) {
    this.keepAlive = keepAlive;
  }
}
