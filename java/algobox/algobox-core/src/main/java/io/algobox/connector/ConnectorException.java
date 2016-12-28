package io.algobox.connector;

public class ConnectorException extends Exception {
  public ConnectorException(String message) {
    super(message);
  }

  public ConnectorException(String message, Throwable cause) {
    super(message, cause);
  }
}
