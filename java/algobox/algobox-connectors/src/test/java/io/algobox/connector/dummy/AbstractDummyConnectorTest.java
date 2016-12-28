package io.algobox.connector.dummy;

import io.algobox.connector.ConnectionStatus;
import io.algobox.connector.Connector;
import io.algobox.connector.ConnectorException;
import io.algobox.connector.ConnectorListener;
import org.junit.Before;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public abstract class AbstractDummyConnectorTest {
  protected static final long PRICES_POLLING_MILLISECONDS = 100L;

  protected Connector connector;
  protected ConnectorListener connectorListener;

  @Before
  public void init() throws ConnectorException {
    connectorListener = mock(ConnectorListener.class);
    connector = new DummyConnector(connectorListener, PRICES_POLLING_MILLISECONDS);
    assertEquals(
        ConnectionStatus.DISCONNECTED, connector.getConnectionInfo().getConnectionStatus());
    connector.connect();
    assertEquals(ConnectionStatus.CONNECTED, connector.getConnectionInfo().getConnectionStatus());
    completeInit();
  }

  protected abstract void completeInit() throws ConnectorException;
}
