package io.algobox.connector.domain;

import java.io.Serializable;
import java.util.Map;

public interface ConnectionRegistration extends Serializable {
  String getConnectionId();

  String getConnectorId();

  Map<String, String> getParameters();

  Boolean getKeepAlive();
}
