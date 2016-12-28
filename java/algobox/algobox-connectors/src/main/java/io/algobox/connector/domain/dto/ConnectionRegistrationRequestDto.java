package io.algobox.connector.domain.dto;

import java.io.Serializable;
import java.util.Map;

public final class ConnectionRegistrationRequestDto implements Serializable {
  private String connectionId;

  private String connectorId;

  private Map<String, String> parameters;

  private Boolean keepAlive;

  public String getConnectorId() {
    return connectorId;
  }

  public Map<String, String> getParameters() {
    return parameters;
  }

  public String getConnectionId() {
    return connectionId;
  }

  public Boolean getKeepAlive() {
    return keepAlive;
  }
}
