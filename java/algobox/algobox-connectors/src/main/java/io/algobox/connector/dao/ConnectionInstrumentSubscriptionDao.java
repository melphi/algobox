package io.algobox.connector.dao;

import java.util.Map;
import java.util.Set;

public interface ConnectionInstrumentSubscriptionDao {
  void subscribeInstrument(String connectionId, String instrumentId);

  void unSubscribeInstrument(String connectionId, String instrumentId);

  Map<String, Set<String>> findAllSubscriptionsByConnection();
}
