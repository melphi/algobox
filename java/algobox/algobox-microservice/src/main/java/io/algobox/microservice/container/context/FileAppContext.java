package io.algobox.microservice.container.context;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

/**
 * A thread safe microservice context which loads values from an application.properties file.
 */
public final class FileAppContext extends AbstractAppContext {
  private static final String DEFAULT_PROPERTIES_FILE = "application.properties";
  private static final Logger LOGGER = LoggerFactory.getLogger(FileAppContext.class);

  private final Properties properties;

  public FileAppContext() {
    try {
      properties = loadApplicationProperties();
      checkArgument(!properties.isEmpty(), "No properties found.");
    } catch (Exception e) {
      LOGGER.error("Please create an application.properties file in resources folder.", e);
      throw new IllegalArgumentException(e);
    }
  }

  @Override
  public String getValue(String key) {
    return (String) properties.get(key);
  }

  @Override
  public Map<String, String> getAllValues() {
    ImmutableMap.Builder<String, String> result = ImmutableMap.builder();
    for (Map.Entry<Object, Object> entry: properties.entrySet()) {
      result.put((String) entry.getKey(), (String) entry.getValue());
    }
    return result.build();
  }

  private Properties loadApplicationProperties() throws Exception {
    InputStream fileStream = checkNotNull(
        ClassLoader.getSystemResourceAsStream(DEFAULT_PROPERTIES_FILE),
        String.format("Properties [%s] not found.", DEFAULT_PROPERTIES_FILE));
    LOGGER.info(format("Loading property file [%s]", DEFAULT_PROPERTIES_FILE));
    Properties properties = new Properties();
    properties.load(fileStream);
    fileStream.close();
    return properties;
  }
}
