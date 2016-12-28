package io.algobox.microservice.container.context;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A thread safe microservice context which loads values from a local Consul client or
 * application.propertiesFile if no Consul client in running.
 */
public final class ConsulAppContext extends AbstractAppContext {
  private static final Logger LOGGER = LoggerFactory.getLogger(ConsulAppContext.class);
  private static final String QUERY_RECURSE = "?recurse";
  private static final TypeReference<List<ConsulKeyValue>> JSON_TYPE_REFERENCE =
      new TypeReference<List<ConsulKeyValue>>(){};
  private static final Base64.Decoder BASE_64_DECODER = Base64.getDecoder();

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final AppContext fileAppContext;
  private final String consulUrl;
  private final boolean hasConsul;
  private final String subPath;

  /**
   * @param consulHost: The consul client to connect to.
   * @param switchToDefaultIfConsulNotFound: If true and Consul was not found then switches to
   *    the default application.properties file for configuration values.
   * @param subPath: The Consul sub path to query the values, it usually corresponds to the
   *    microservice name so each microservice has its own collection of parameters.
   */
  public ConsulAppContext(
      String consulHost, boolean switchToDefaultIfConsulNotFound, String subPath) {
    checkArgument(!Strings.isNullOrEmpty(consulHost), "Missing consulHost.");
    this.subPath = formatSubPath(subPath);
    this.consulUrl = String.format("http://%s:8500/v1/kv", consulHost);
    LOGGER.info(String.format("Trying to connect to Consul at [%s].", this.consulUrl));
    this.hasConsul = discoverConsul();
    if (!this.hasConsul) {
      checkArgument(switchToDefaultIfConsulNotFound, "Consul client not found.");
      LOGGER.warn("Consul service not found, using local file for application properties.");
      this.fileAppContext = new FileAppContext();
    } else {
      LOGGER.info("Consul client found, using it for application properties.");
      this.fileAppContext = null;
    }
  }

  @Override
  public String getValue(String key) {
    checkArgument(!Strings.isNullOrEmpty(key));
    if (hasConsul) {
      Collection<ConsulKeyValue> values = findElements(key);
      return values.isEmpty() ? null : Iterables.getOnlyElement(values).getValue();
    } else {
      return fileAppContext.getValue(key);
    }
  }

  @Override
  public Map<String, String> getAllValues() {
    if (hasConsul) {
      return getAllConsulValues();
    } else {
      return fileAppContext.getAllValues();
    }
  }

  private Map<String, String> getAllConsulValues() {
    ImmutableMap.Builder<String, String> result = ImmutableMap.builder();
    for (ConsulKeyValue item: findElements(QUERY_RECURSE)) {
      result.put(item.getKey(), item.getValue());
    }
    return result.build();
  }

  private Collection<ConsulKeyValue> findElements(String query) {
    String urlString = consulUrl+ subPath + query;
    HttpURLConnection connection = null;
    try {
      URL url = new URL(urlString);
      connection = (HttpURLConnection) url.openConnection();
      if (connection.getResponseCode() == HttpURLConnection.HTTP_OK
          && connection.getContentLength() > 0) {
         Collection<ConsulKeyValue> rawResult =
            objectMapper.readValue(connection.getInputStream(), JSON_TYPE_REFERENCE);
        return rawResult.stream()
            .map(this::parseKeyValue)
            .collect(Collectors.toList());
      } else {
        return ImmutableList.of();
      }
    } catch (IOException e) {
      throw new IllegalArgumentException(String.format(
          "Error while connecting or parsing [%s]: [%s]", urlString, e.getMessage()), e);
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }

  private ConsulKeyValue parseKeyValue(ConsulKeyValue consulKeyValue) {
    if (consulKeyValue == null) {
      return null;
    }
    String decodedKey = consulKeyValue.getKey().contains("/")
        ? consulKeyValue.getKey().substring(consulKeyValue.getKey().indexOf('/') + 1)
        : consulKeyValue.getKey();
    String decodedValue = new String(
        BASE_64_DECODER.decode(consulKeyValue.getValue()), StandardCharsets.UTF_8);
    return new ConsulKeyValue(decodedKey, decodedValue);
  }

  private String formatSubPath(String subPath) {
    if (Strings.isNullOrEmpty(subPath)) {
      return "/";
    }
    subPath = subPath.endsWith("/") ? subPath : subPath + "/";
    return subPath.startsWith("/") ? subPath : "/" + subPath;
  }

  private boolean discoverConsul() {
    try {
      getAllConsulValues();
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
