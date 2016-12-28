package io.algobox.connector;

/**
 * A connector listener with the source.
 */
public interface SourcedConnectorListener {
  void onConnected(String source);

  void onDisconnected(String source);

  void onError(String source, Exception exception);
}
