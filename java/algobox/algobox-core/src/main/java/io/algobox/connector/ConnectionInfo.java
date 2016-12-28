package io.algobox.connector;

import java.io.Serializable;

public final class ConnectionInfo implements Serializable {
  private ConnectionStatus connectionStatus;

  private long connectionDateUtc;

  public ConnectionInfo(ConnectionStatus connectionStatus, long connectionDateUtc) {
    this.connectionStatus = connectionStatus;
    this.connectionDateUtc = connectionDateUtc;
  }

  public ConnectionStatus getConnectionStatus() {
    return connectionStatus;
  }

  public long getConnectionDateUtc() {
    return connectionDateUtc;
  }

  @Override
  public String toString() {
    return "ConnectionInfo{"
        + "connectionStatus=" + connectionStatus
        + ", connectionDateUtc=" + connectionDateUtc
        + '}';
  }
}
