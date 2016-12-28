package io.algobox.microservice.container.context;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A thread safe microservice context which loads values from a function.
 */
public final class MapAppContext extends AbstractAppContext {
  private final Map<String, String> values;

  public MapAppContext(Map<String, String> values) {
    this.values = ImmutableMap.copyOf(values);
  }

  @Override
  public String getValue(String key) {
    checkArgument(!Strings.isNullOrEmpty(key), "Key can not be empty.");
    return String.valueOf(values.get(key));
  }
  @Override
  public Map<String, String> getAllValues() {
    return ImmutableMap.copyOf(values);
  }
}
