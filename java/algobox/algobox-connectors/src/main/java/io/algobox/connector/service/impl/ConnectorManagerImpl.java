package io.algobox.connector.service.impl;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import io.algobox.common.exception.ServiceException;
import io.algobox.connector.Connector;
import io.algobox.connector.ConnectorException;
import io.algobox.connector.ConnectorListener;
import io.algobox.connector.dummy.DummyConnector;
import io.algobox.connector.fxcm.FxcmConnector;
import io.algobox.connector.oanada.OandaConnector;
import io.algobox.connector.service.ConnectorManager;
import io.algobox.util.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;

public final class ConnectorManagerImpl implements ConnectorManager {
  private static final Logger LOGGER = LoggerFactory.getLogger(ConnectorManagerImpl.class);
  private static final String CONNECTOR_ID_DUMMY = "dummy";
  private static final String CONNECTOR_ID_FXCM = "fxcm";
  private static final String CONNECTOR_ID_OANDA = "oanda";

  private static final Set<String> REGISTERED_CONNECTORS =
      ImmutableSet.of(CONNECTOR_ID_DUMMY, CONNECTOR_ID_FXCM, CONNECTOR_ID_OANDA);

  private final int marketOpenHour;
  private final int marketOpenMinute;
  private final int marketCloseHour;
  private final int marketCloseMinute;

  private Map<String, Connector> loadedConnections = Maps.newConcurrentMap();

  public ConnectorManagerImpl(Map<String, String> parameters) {
    try {
      String[] marketOpen = getRequiredValue(
          parameters, ConnectorParameters.PARAMETER_MARKET_OPEN_TIME).split(":");
      String[] marketClose = getRequiredValue(
          parameters, ConnectorParameters.PARAMETER_MARKET_CLOSE_TIME).split(":");
      marketOpenHour = Integer.valueOf(marketOpen[0]);
      marketOpenMinute = Integer.valueOf(marketOpen[1]);
      marketCloseHour = Integer.valueOf(marketClose[0]);
      marketCloseMinute = Integer.valueOf(marketClose[1]);
      LOGGER.info(String.format("Setting market open [%02d:%02d], close [%02d:%02d].",
          marketOpenHour, marketOpenMinute, marketCloseHour, marketCloseMinute));
    } catch (Exception e) {
      throw new IllegalArgumentException(
          String.format("Invalid market open or close time: [%s].", e.getMessage()), e);
    }
  }

  @Override
  public Connector getConnectionIfPresent(String connectionId) {
    Connector connection = loadedConnections.get(connectionId);
    return checkNotNull(connection, String.format("Connection [%s] not found.", connectionId));
  }

  @Override
  public Set<String> getRegisteredConnectors() {
    return REGISTERED_CONNECTORS;
  }

  @Override
  public Connector loadConnection(String connectionId, String connectorId,
      Map<String, String> connectionParameters, ConnectorListener connectorListener)
      throws ServiceException {
    checkNotNull(connectorListener);
    checkNotNullOrEmpty(connectionId, "Connection id can not be empty.");
    checkNotNullOrEmpty(connectorId, "Connector id can not be empty.");
    checkNotNull(connectionParameters, "Connection parameters can not be null.");
    if (loadedConnections.containsKey(connectionId)) {
      throw new ServiceException(String.format("Connection id [%s] already loaded.", connectionId));
    }
    Connector connection;
    switch (connectorId) {
      case CONNECTOR_ID_DUMMY:
        connection = createDummyConnection(connectorListener);
        break;
      case CONNECTOR_ID_FXCM:
        connection = createFxcmConnection(connectionParameters, connectorListener);
        break;
      case CONNECTOR_ID_OANDA:
        connection = createOandaConnection(connectionParameters, connectorListener);
        break;
      default:
        throw new ServiceException(String.format("Connector [%s] not registered.", connectorId));
    }
    loadedConnections.put(connectionId, connection);
    return connection;
  }

  @Override
  public void disposeConnection(String connectionId) {
    checkNotNullOrEmpty(connectionId, "Connection id can not be empty.");
    Connector connector = loadedConnections.remove(connectionId);
    checkArgument(connector != null, String.format("Connection id [%s] not found.", connectionId));
    try {
      connector.disconnect();
    } catch (ConnectorException e) {
      LOGGER.error(String.format("Error while disconnecting connection [%s]: [%s].",
          connectionId, e.getMessage()), e);
    }
  }

  @Override
  public boolean isMarketOpen() {
    return isMarketOpen(DateTimeUtils.getCurrentUtcDateTime());
  }

  @Override
  public boolean isMarketOpen(ZonedDateTime dateTime) {
    checkNotNull(dateTime);
    switch (dateTime.getDayOfWeek()) {
      case MONDAY:
      case TUESDAY:
      case WEDNESDAY:
      case THURSDAY:
        return true;
      case FRIDAY:
        return dateTime.getHour() < marketCloseHour
            || (dateTime.getHour() == marketCloseHour && dateTime.getMinute() < marketCloseMinute);
      case SATURDAY:
        return false;
      case SUNDAY:
        return dateTime.getHour() > marketOpenHour
            || dateTime.getHour() == marketOpenHour && dateTime.getMinute() >= marketOpenMinute;
      default:
        throw new IllegalArgumentException(
            String.format("Invalid day of week [%s].", dateTime.getDayOfWeek()));
    }
  }

  private Connector createDummyConnection(ConnectorListener connectorListener) {
    return new DummyConnector(connectorListener);
  }

  private Connector createFxcmConnection(
      Map<String, String> parameters, ConnectorListener connectorListener) {
    String username = getRequiredValue(parameters, "username");
    String password = getRequiredValue(parameters, "password");
    String terminal = getRequiredValue(parameters, "terminal");
    String server = getRequiredValue(parameters, "server");
    return new FxcmConnector(username, password, terminal, server, connectorListener);
  }

  private Connector createOandaConnection(
      Map<String, String> parameters, ConnectorListener connectorListener) {
    String apiKey = getRequiredValue(parameters, "apiKey");
    String accountNumber = getRequiredValue(parameters, "accountNumber");
    Boolean isLiveAccount = Boolean.valueOf(getRequiredValue(parameters, "isLiveAccount"));
    return new OandaConnector(apiKey, accountNumber, isLiveAccount, connectorListener);
  }

  private String getRequiredValue(Map<String, String> parameters, String key) {
    return checkNotNullOrEmpty(parameters.get(key),
        String.format("Required parameter [%s] not found.", key));
  }
}
