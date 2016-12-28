package io.algobox.connector.dummy;

import io.algobox.connector.ConnectionStatus;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DummyConnectorTest extends AbstractDummyConnectorTest {
  @Test
  public void shouldDisconnect() throws Exception {
    connector.disconnect();
    assertEquals(
        ConnectionStatus.DISCONNECTED, connector.getConnectionInfo().getConnectionStatus());
    assertTrue(connector.getConnectionInfo().getConnectionDateUtc() > 0);
  }

  @Override
  protected void completeInit() {
    // Intentionally empty.
  }
}
