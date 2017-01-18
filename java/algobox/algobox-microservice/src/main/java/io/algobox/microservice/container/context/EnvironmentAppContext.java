package io.algobox.microservice.container.context;

import java.util.Map;

/**
 * A thread safe microservice context which gets its values from environment variables.
 */
public final class EnvironmentAppContext extends AbstractAppContext {
  @Override
  public String getValue(String key) {
    return System.getenv(key);
  }

  @Override
  public Map<String, String> getAllValues() {
    return System.getenv();
  }
}
