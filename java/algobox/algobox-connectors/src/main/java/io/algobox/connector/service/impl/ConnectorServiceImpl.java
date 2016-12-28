package io.algobox.connector.service.impl;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.algobox.common.domain.StringValueDto;
import io.algobox.common.exception.ServiceException;
import io.algobox.connector.ConnectionInfo;
import io.algobox.connector.ConnectionStatus;
import io.algobox.connector.Connector;
import io.algobox.connector.ConnectorException;
import io.algobox.connector.ConnectorListener;
import io.algobox.connector.ConnectorOrderRequest;
import io.algobox.connector.ConnectorPriceService;
import io.algobox.connector.SourcedConnectorListener;
import io.algobox.connector.dao.ConnectionInstrumentSubscriptionDao;
import io.algobox.connector.dao.ConnectionRegistrationDao;
import io.algobox.connector.domain.ConnectionRegistration;
import io.algobox.connector.domain.dto.ConnectionRegistrationDto;
import io.algobox.connector.domain.dto.ConnectionRegistrationRequestDto;
import io.algobox.connector.service.ConnectorManager;
import io.algobox.connector.service.ConnectorService;
import io.algobox.instrument.InstrumentInfo;
import io.algobox.order.OrderResponse;
import io.algobox.price.PriceTick;
import io.algobox.price.SourcedPriceTickListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;

public final class ConnectorServiceImpl implements ConnectorService {
  private static final Logger LOGGER = LoggerFactory.getLogger(ConnectorServiceImpl.class);
  private static final long TRY_CONNECTION_POLLING_MILLISECONDS = 1_500L;
  private static final long MARKET_HOURS_POLLING_MILLISECONDS = 60_000L;

  private final Lock semaphoresLock = new ReentrantLock();
  private final Map<String, Semaphore> semaphores = Maps.newHashMap();
  private final ConnectionRegistrationDao connectionRegistrationDao;
  private final ConnectionInstrumentSubscriptionDao connectionInstrumentSubscriptionDao;
  private final ConnectorManager connectorManager;
  private final Optional<SourcedConnectorListener> sourcedConnectorListener;

  private Set<String> keepAliveConnections = Sets.newConcurrentHashSet();
  private Map<String, Set<String>> subscribedInstrumentsByConnection = Maps.newConcurrentMap();
  private SourcedPriceTickListener priceTickListener;

  public ConnectorServiceImpl(ConnectorManager connectorManager,
      ConnectionRegistrationDao connectorKeepAliveDao,
      ConnectionInstrumentSubscriptionDao connectorInstrumentSubscriptionDao)
      throws ExecutionException, InterruptedException {
    this(connectorManager, null, connectorKeepAliveDao, connectorInstrumentSubscriptionDao);
  }

  public ConnectorServiceImpl(ConnectorManager connectorManager,
      SourcedConnectorListener sourcedConnectorListener,
      ConnectionRegistrationDao connectorKeepAliveDao,
      ConnectionInstrumentSubscriptionDao connectorInstrumentSubscriptionDao)
      throws ExecutionException, InterruptedException {
    this.connectorManager = connectorManager;
    this.connectionRegistrationDao = connectorKeepAliveDao;
    this.connectionInstrumentSubscriptionDao = connectorInstrumentSubscriptionDao;
    this.sourcedConnectorListener = Optional.ofNullable(sourcedConnectorListener);
    initConnections();
    new CheckMarketHoursThread().start();
  }

  @Override
  public void createConnection(ConnectionRegistrationRequestDto connectionRegistrationRequest)
      throws ConnectorException, ServiceException  {
    Connector connector = loadConnector(connectionRegistrationRequest.getConnectionId(),
        connectionRegistrationRequest.getConnectorId(),
        connectionRegistrationRequest.getParameters());
    checkNotNull(connector, "No connection was returned.");
    try {
      connectionRegistrationDao.save(connectionRegistrationRequest);
      if (Boolean.TRUE.equals(connectionRegistrationRequest.getKeepAlive())) {
        connect(connectionRegistrationRequest.getConnectionId()).get(15, TimeUnit.SECONDS);
      }
    } catch (Exception e) {
      removeConnection(connectionRegistrationRequest.getConnectionId());
    }
  }

  @Override
  public Collection<ConnectionRegistrationDto> findAllConnections() {
    return connectionRegistrationDao.findAll()
        .stream()
        .map(ConnectionRegistrationDto::new)
        .collect(Collectors.toSet());
  }

  @Override
  public void removeConnection(String connectionId) throws ConnectorException, ServiceException {
    try {
      disconnect(connectionId);
    } finally {
      connectionRegistrationDao.deleteById(connectionId);
      connectorManager.disposeConnection(connectionId);
    }
  }

  @Override
  public Collection<StringValueDto> getSubscribedInstruments(String connectionId) {
    checkNotNullOrEmpty(connectionId);
    return internalGetSubscribedInstruments(connectionId).stream()
        .map(StringValueDto::new)
        .collect(Collectors.toList());
  }

  @Override
  public ConnectionInfo getConnectionInfo(String connectionId) throws ServiceException {
    return connectorManager.getConnectionIfPresent(connectionId).getConnectionInfo();
  }

  @Override
  public void disconnect(String connectionId) throws ServiceException, ConnectorException {
    checkNotNullOrEmpty(connectionId);
    subscribedInstrumentsByConnection.remove(connectionId);
    setKeepAlive(connectionId, false);
    internalDisconnectBlocking(connectionId);
  }

  /**
   * Asynchronously attempts to connect to the connector unlimited times until disconnect is
   * called. Returns a successful future in case of established connection.
   */
  @Override
  public Future<Boolean> connect(String connectionId) {
    checkNotNullOrEmpty(connectionId);
    setKeepAlive(connectionId, true);
    return internalConnectAsync(connectionId);
  }

  @Override
  public void subscribeInstrument(String connectionId, String instrumentId)
      throws ServiceException {
    checkNotNullOrEmpty(connectionId);
    checkNotNullOrEmpty(instrumentId);
    Set<String> subscriptions = ImmutableSet.<String>builder()
        .addAll(internalGetSubscribedInstruments(connectionId))
        .add(instrumentId)
        .build();
    try {
      connectorManager.getConnectionIfPresent(connectionId)
          .getPriceService()
          .subscribeInstrument(instrumentId);
      subscribedInstrumentsByConnection.put(connectionId, subscriptions);
      connectionInstrumentSubscriptionDao.subscribeInstrument(connectionId, instrumentId);
      LOGGER.info(String.format(
          "Subscribed instrument [%s] for connector [%s].", instrumentId, connectionId));
    } catch (Exception e) {
      LOGGER.error(String.format(
          "Error while subscribing instrument [%s] for connector [%s]: [%s]", instrumentId,
          connectionId, e.getMessage()), e);
      throw new ServiceException(String.format(
          "Error while subscribing instrument [%s] to [%s]: [%s].",
          instrumentId, connectionId, e.getMessage()));
    }
  }

  @Override
  public void unSubscribeInstrument(String connectionId, String instrumentId) {
    checkNotNullOrEmpty(connectionId);
    checkNotNullOrEmpty(instrumentId);
    Set<String> subscriptions = internalGetSubscribedInstruments(connectionId).stream()
        .filter(instrument -> !instrumentId.equals(instrument))
        .collect(Collectors.toSet());
    subscribedInstrumentsByConnection.put(connectionId, ImmutableSet.copyOf(subscriptions));
    connectionInstrumentSubscriptionDao.unSubscribeInstrument(connectionId, instrumentId);
    try {
      connectorManager.getConnectionIfPresent(connectionId)
          .getPriceService()
          .unSubscribeInstrument(instrumentId);
      LOGGER.info(String.format(
          "Un-subscribed instrument [%s] for connector [%s].", instrumentId, connectionId));
    } catch (Exception e) {
      LOGGER.error(String.format(
          "Error while un-subscribing instrument [%s] for connector [%s]: [%s]", instrumentId,
          connectionId, e.getMessage()), e);
    }
  }

  @Override
  public Collection<InstrumentInfo> searchInstruments(String connectionId, String searchTerm)
      throws ServiceException, ConnectorException {
    checkNotNullOrEmpty(connectionId);
    checkNotNullOrEmpty(searchTerm);
    return connectorManager.getConnectionIfPresent(connectionId)
        .getInstrumentService()
        .findInstrumentsBySearchTerm(searchTerm);
  }

  @Override
  public Connector getConnectionById(String connectionId) {
    return connectorManager.getConnectionIfPresent(connectionId);
  }

  @Override
  public Collection<StringValueDto> findAllConnectors() {
    return connectorManager.getRegisteredConnectors().stream()
        .map(StringValueDto::new)
        .collect(Collectors.toSet());
  }

  @Override
  public void setPriceTickListener(SourcedPriceTickListener priceTickListener) {
    checkArgument(this.priceTickListener == null, "Price tick listener can be set only once.");
    this.priceTickListener = checkNotNull(priceTickListener);
  }

  private void setKeepAlive(String connectionId, boolean keepAlive) {
    if(keepAlive) {
      keepAliveConnections.add(connectionId);
    } else {
      keepAliveConnections.remove(connectionId);
    }
    connectionRegistrationDao.setKeepAlive(connectionId, keepAlive);
  }

  private void internalDisconnectBlocking(final String connectionId)
      throws ServiceException, ConnectorException {
    connectorManager.getConnectionIfPresent(connectionId).disconnect();
  }

  private CompletableFuture<Boolean> internalConnectAsync(final String connectionId) {
    CompletableFuture<Boolean> callback = new CompletableFuture<>();
    startOnlyOneConnectionThread(connectionId, callback);
    return callback;
  }

  private Set<String> internalGetSubscribedInstruments(String connectionId) {
    Set<String> instruments = subscribedInstrumentsByConnection.get(connectionId);
    return (instruments != null) ? ImmutableSet.copyOf(instruments) : ImmutableSet.of();
  }

  private void initConnections() {
    LOGGER.info("Loading connections.");
    subscribedInstrumentsByConnection.clear();
    subscribedInstrumentsByConnection.putAll(
        connectionInstrumentSubscriptionDao.findAllSubscriptionsByConnection());
    Collection<ConnectionRegistration> connections = connectionRegistrationDao.findAll();
    for (ConnectionRegistration connection: connections) {
      LOGGER.info(String.format("Loading connection [%s].", connection.getConnectionId()));
      try {
        loadConnector(connection.getConnectionId(),
            connection.getConnectorId(), connection.getParameters());
        if (Boolean.TRUE.equals(connection.getKeepAlive())) {
          connect(connection.getConnectionId());
        }
      } catch (Exception e) {
        LOGGER.error(String.format("Error while loading connection [%s] for connector [%s]: [%s].",
            connection.getConnectionId(), connection.getConnectorId(), e.getMessage()),
            e);
      }
    }
  }

  private Connector loadConnector(String connectionId, String connectorId,
      Map<String, String> parameters) throws ServiceException {
    return connectorManager.loadConnection(connectionId, connectorId, parameters,
        new ConnectionListenerImpl(connectionId));
  }

  private boolean isConnectedOrConnecting(String connectorId) {
    checkNotNullOrEmpty(connectorId);
    try {
      ConnectionStatus connectionStatus = getConnectionInfo(connectorId).getConnectionStatus();
      switch (connectionStatus) {
        case CONNECTED:
        case CONNECTING:
          return true;
        case DISCONNECTED:
        case DISCONNECTING:
          return false;
        default:
          throw new IllegalArgumentException(
              String.format("Unsupported status [%s].", connectionStatus));
      }
    } catch (ServiceException e) {
      return false;
    }
  }

  /**
   * Starts a new connection thread if and only if no other connection thread is active for the
   * given connection id.
   */
  private void startOnlyOneConnectionThread(
      String connectionId, CompletableFuture<Boolean> callback) {
    checkNotNullOrEmpty(connectionId);
    semaphoresLock.lock();
    try {
      Semaphore semaphore = semaphores.get(connectionId);
      if (semaphore == null) {
        semaphore = new Semaphore(1);
        semaphores.put(connectionId, semaphore);
      }
      if(semaphore.tryAcquire()) {
        new StartConnectionThread(connectionId, callback, semaphore).start();
      }
    } finally {
      semaphoresLock.unlock();
    }
  }

  private final class ConnectionListenerImpl implements ConnectorListener {
    private final String connectionId;

    ConnectionListenerImpl(String connectionId) {
      this.connectionId = checkNotNullOrEmpty(connectionId);
    }

    @Override
    public void onPriceTick(PriceTick priceTick) {
      try {
        if (priceTickListener != null) {
          priceTickListener.onPriceTick(connectionId, priceTick);
        }
      } catch (Exception e) {
        // Intentionally empty.
      }
    }

    @Override
    public void onOrderOpen(OrderResponse orderResponse) {
      // Intentionally empty.
    }

    @Override
    public void onOrderError(ConnectorOrderRequest orderRequest, Throwable throwable) {
      LOGGER.info(String.format(
          "Failed order request [%s]: [%s].", orderRequest, throwable.getMessage()));
    }

    @Override
    public void onConnected() {
      LOGGER.info(String.format("Connector [%s] connected.", connectionId));
      try {
        ConnectorPriceService priceService =
            connectorManager.getConnectionIfPresent(connectionId).getPriceService();
        for (String instrumentId: internalGetSubscribedInstruments(connectionId)) {
          try {
            if (!priceService.isInstrumentSubscribed(instrumentId)) {
              priceService.subscribeInstrument(instrumentId);
            }
          } catch (Exception e) {
            LOGGER.error(String.format(
                "Error while (re)subscribing instrument [%s] for connector [%s]: [%s]",
                instrumentId, connectionId, e.getMessage()), e);
          }
        }
      } catch (Exception e) {
        LOGGER.error(String.format(
            "Error while (re)subscribing instrument for connector [%s]: [%s]", connectionId,
            e.getMessage()), e);
      } finally {
        if (sourcedConnectorListener.isPresent()) {
          try {
            sourcedConnectorListener.get().onConnected(connectionId);
          } catch (Exception  e) {
            LOGGER.warn(String.format(
                "Error while notifying the connector listener: [%s]", e.getMessage()), e);
          }
        }
      }
    }

    @Override
    public void onDisconnected() {
      try {
        ConnectorPriceService priceService =
            connectorManager.getConnectionIfPresent(connectionId).getPriceService();
        for (String instrumentId: priceService.getSubscribedInstruments()) {
          try {
            priceService.unSubscribeInstrument(instrumentId);
          } catch (ConnectorException e) {
            LOGGER.error("Error while un-subscribing instrument [%s] for connector [%s]: [%s].",
                instrumentId, connectionId, e.getMessage(), e);
          }
        }
      } catch (Exception e) {
        LOGGER.error("Error while processing disconnection for connector [%s]: [%s].",
            connectionId, e.getMessage(), e);
      }

      if (keepAliveConnections.contains(connectionId)) {
        LOGGER.warn(String.format(
            "Connector [%s] disconnected (unplanned), trying to reconnect.", connectionId));
        startOnlyOneConnectionThread(connectionId, null);
      } else {
        LOGGER.info(String.format("Connector [%s] disconnected (planned).", connectionId));
      }

      if (sourcedConnectorListener.isPresent()) {
        try {
          sourcedConnectorListener.get().onConnected(connectionId);
        } catch (Exception  e) {
          LOGGER.warn(String.format(
              "Error while notifying the connector listener: [%s]", e.getMessage()), e);
        }
      }
    }

    @Override
    public void onGenericError(Throwable throwable) {
      LOGGER.error(String.format(
          "Connector [%s] error [%s].", connectionId, throwable.getMessage()), throwable);
      if (sourcedConnectorListener.isPresent()) {
        try {
          sourcedConnectorListener.get().onConnected(connectionId);
        } catch (Exception  e) {
          LOGGER.warn(String.format(
              "Error while notifying the connector listener: [%s]", e.getMessage()), e);
        }
      }
    }
  }

  /**
   * Attempts to reconnect to the connector unlimited times, unless the disconnection was planned.
   * Warning: Avoid to have multiple StartConnectionThread active by calling start
   * startOnlyOneConnectionThread().
   */
  private final class StartConnectionThread extends Thread {
    private final Semaphore semaphore;
    private final String connectionId;
    private final Optional<CompletableFuture<Boolean>> callback;

    /**
     * @param connectionId The connector id.
     * @param callback The callback, can be null.
     * @param semaphore The semaphore to be released.
     */
    StartConnectionThread(
        String connectionId, CompletableFuture<Boolean> callback, Semaphore semaphore) {
      this.semaphore = semaphore;
      this.connectionId = checkNotNullOrEmpty(connectionId);
      this.callback = callback == null ? Optional.empty() : Optional.of(callback);
    }

    @Override
    public void run() {
      try {
        while (!tryEstablishConnection()) {
          try {
            Thread.sleep(TRY_CONNECTION_POLLING_MILLISECONDS);
          } catch (InterruptedException e) {
            // Intentionally empty.
          }
        }
      } finally {
        semaphore.release();
      }
    }

    private boolean tryEstablishConnection() {
      if (!connectorManager.isMarketOpen()) {
        LOGGER.info(String.format("Market is not open, connection to [%s] skipped.", connectionId));
        callback.ifPresent(value -> value.complete(true));
        return true;
      }
      if (!keepAliveConnections.contains(connectionId)) {
        return true;
      }
      ConnectionStatus connectionStatus = null;
      try {
        connectionStatus = connectorManager.getConnectionIfPresent(connectionId)
            .getConnectionInfo()
            .getConnectionStatus();
      } catch (Exception e) {
        LOGGER.error(e.getMessage(), e);
        return false;
      }
      switch (connectionStatus) {
        case CONNECTED:
          callback.ifPresent(value -> value.complete(true));
          return true;
        case CONNECTING:
        case DISCONNECTING:
          return false;
        case DISCONNECTED:
          try {
            LOGGER.info(String.format("Connecting to [%s].", connectionId));
            connectorManager.getConnectionIfPresent(connectionId).connect();
          } catch (Exception e) {
            LOGGER.warn(String.format("Failed (re)connection attempt connector [%s]: [%s].",
                connectionId, e.getMessage()), e);
          }
          return false;
        default:
          throw new IllegalArgumentException(
              String.format("Unsupported status [%s].", connectionStatus));
      }
    }
  }

  private final class CheckMarketHoursThread extends Thread {
    @Override
    public void run() {
      // TODO(robertom): Implement service shutdown hook.
      while (true) {
        ImmutableSet<String> connectorsId = ImmutableSet.copyOf(keepAliveConnections);
        for (String connectionId : connectorsId) {
          boolean isConnectedOrConnecting = isConnectedOrConnecting(connectionId);
          boolean isMarketOpen = connectorManager.isMarketOpen();
          if (isMarketOpen && !isConnectedOrConnecting) {
            internalConnectAsync(connectionId);
          } else if (!isMarketOpen && isConnectedOrConnecting) {
            try {
              internalDisconnectBlocking(connectionId);
            } catch (Exception e) {
              LOGGER.error(e.getMessage(), e);
            }
          }
        }
        try {
          Thread.sleep(MARKET_HOURS_POLLING_MILLISECONDS);
        } catch (InterruptedException e) {
          LOGGER.error(e.getMessage(), e);
          return;
        }
      }
    }
  }
}
