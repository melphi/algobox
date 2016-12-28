package io.algobox.microservice.container.context;

import java.util.Map;

public interface AppContext {
  String getValue(String key);

  Map<String, String> getAllValues();

  String getRequiredValue(String key);

  int getRequiredInt(String key);

  boolean getBoolean(String key);
}
