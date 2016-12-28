package io.algobox.microservice.container.context;

import com.google.common.base.Strings;

import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;

public abstract class AbstractAppContext implements AppContext {
  private static final String VALUE_TRUE = "true";

  @Override
  public abstract String getValue(String key);

  public abstract Map<String, String> getAllValues();

  @Override
  public String getRequiredValue(String key) {
    String value = getValue(key);
    checkArgument(!Strings.isNullOrEmpty(value), format("Property [%s] is empty or null.", key));
    return value;
  }

  @Override
  public int getRequiredInt(String key) {
    String value = getRequiredValue(key);
    return Integer.parseInt(value);
  }

  @Override
  public boolean getBoolean(String key) {
    return VALUE_TRUE.equals(getValue(key));
  }
}
