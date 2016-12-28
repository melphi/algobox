package io.algobox.connector;

import java.util.Collection;

public interface ConnectorPriceService {
  boolean isInstrumentSubscribed(String connectorInstrumentId);

  void subscribeInstrument(String connectorInstrumentId) throws ConnectorException;

  void unSubscribeInstrument(String connectorInstrumentId) throws ConnectorException;

  Collection<String> getSubscribedInstruments();
}
