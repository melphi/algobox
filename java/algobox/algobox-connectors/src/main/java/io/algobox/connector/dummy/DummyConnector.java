package io.algobox.connector.dummy;

import io.algobox.connector.ConnectionInfo;
import io.algobox.connector.ConnectionStatus;
import io.algobox.connector.Connector;
import io.algobox.connector.ConnectorException;
import io.algobox.connector.ConnectorInstrumentService;
import io.algobox.connector.ConnectorListener;
import io.algobox.connector.ConnectorOrderService;
import io.algobox.connector.ConnectorPriceService;
import io.algobox.util.DateTimeUtils;

import static com.google.common.base.Preconditions.checkNotNull;

public class DummyConnector implements Connector {
  private final ConnectorOrderService orderService;
  private final DummyPriceService priceService;
  private final ConnectorListener connectorListener;

  private ConnectionStatus connectionStatus;
  private long connectionTimestamp;

  public DummyConnector(ConnectorListener connectorListener) {
    this(connectorListener, DummyPriceService.DEFAULT_POLLING_MILLISECONDS);
  }

  public DummyConnector(ConnectorListener connectorListener, long pricesPollingMilliseconds) {
    this.connectorListener = checkNotNull(connectorListener);
    this.priceService = new DummyPriceService(connectorListener, pricesPollingMilliseconds);
    this.orderService = new DummyOrderService();
    connectionStatus = ConnectionStatus.DISCONNECTED;
  }

  @Override
  public void connect() throws ConnectorException {
    connectionStatus = ConnectionStatus.CONNECTED;
    connectionTimestamp = DateTimeUtils.getCurrentUtcTimestampMilliseconds();
    connectorListener.onConnected();
    priceService.start();
  }

  @Override
  public void disconnect() throws ConnectorException {
    connectionStatus = ConnectionStatus.DISCONNECTED;
    connectionTimestamp = DateTimeUtils.getCurrentUtcTimestampMilliseconds();
    connectorListener.onDisconnected();
    priceService.stop();
  }

  @Override
  public ConnectionInfo getConnectionInfo() {
    return new ConnectionInfo(connectionStatus, connectionTimestamp);
  }

  @Override
  public ConnectorPriceService getPriceService() throws ConnectorException {
    return priceService;
  }

  @Override
  public ConnectorInstrumentService getInstrumentService() throws ConnectorException {
    throw new IllegalAccessError("Not yet implemented.");
  }

  @Override
  public ConnectorOrderService getOrderService() throws ConnectorException {
    return orderService;
  }
}
