import io.algobox.connector.ConnectionStatus;
import io.algobox.connector.ConnectorException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OandaConnectorIT extends AbstractOandaConnectorIT {
  @Test
  public void testConnection() throws ConnectorException {
    assertEquals(ConnectionStatus.CONNECTED, connector.getConnectionInfo().getConnectionStatus());
    connector.disconnect();
    assertEquals(
        ConnectionStatus.DISCONNECTED, connector.getConnectionInfo().getConnectionStatus());
  }
}
