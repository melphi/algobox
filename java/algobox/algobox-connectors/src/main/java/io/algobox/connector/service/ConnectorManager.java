package io.algobox.connector.service;

import io.algobox.common.exception.ServiceException;
import io.algobox.connector.Connector;
import io.algobox.connector.ConnectorListener;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;

public interface ConnectorManager {
  Set<String> getRegisteredConnectors();

  Connector loadConnection(String connectionId, String connectorId,
      Map<String, String> connectionParameters, ConnectorListener connectorListener)
      throws ServiceException;

  Connector getConnectionIfPresent(String connectionId);

  void disposeConnection(String connectionId);

  boolean isMarketOpen();

  boolean isMarketOpen(ZonedDateTime dateTime);
}
