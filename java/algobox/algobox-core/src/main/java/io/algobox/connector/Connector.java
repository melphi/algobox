package io.algobox.connector;

public interface Connector {
  /**
   * Connects to the service. Connection will probably be non-blocking, depending on the
   * implementation. A service is connected when it is ready to execute commands.
   * @throws ConnectorException Error while sending the connection command.
   */
  void connect() throws ConnectorException;

  /**
   * Disconnects from the service.
   * @throws ConnectorException Error while sending the disconnection command.
   */
  void disconnect() throws ConnectorException;

  /**
   * Returns the connection info. The operation can be expensive and should be used accordingly.
   */
  ConnectionInfo getConnectionInfo();

  ConnectorPriceService getPriceService() throws ConnectorException;

  ConnectorInstrumentService getInstrumentService() throws ConnectorException;

  ConnectorOrderService getOrderService() throws ConnectorException;
}
