package io.algobox.connector.fxcm;

import com.fxcm.external.api.transport.IGateway;
import com.fxcm.external.api.transport.listeners.IGenericMessageListener;
import com.fxcm.external.api.transport.listeners.IStatusMessageListener;
import com.fxcm.fix.FXCMTimingIntervalFactory;
import com.fxcm.fix.TradingSecurity;
import com.fxcm.fix.other.UserResponse;
import com.fxcm.fix.pretrade.MarketDataSnapshot;
import com.fxcm.fix.pretrade.TradingSessionStatus;
import com.fxcm.messaging.ISessionStatus;
import com.fxcm.messaging.ITransportable;
import com.fxcm.messaging.util.GenericSessionStatus;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import io.algobox.connector.ConnectorListener;
import io.algobox.connector.ConnectionInfo;
import io.algobox.connector.ConnectionStatus;
import io.algobox.util.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;

public final class FxcmSessionHelper {
  private static final Logger LOGGER = LoggerFactory.getLogger(FxcmSessionHelper.class);

  private final Map<String, Consumer<ITransportable>> callbacks = Maps.newHashMap();
  private final Lock callbacksLock = new ReentrantLock();
  private final Lock tradingSessionStatusLock = new ReentrantLock();

  private final IGateway fxcmGateway;
  private final ConnectorListener connectorListener;

  private volatile ConnectionStatus connectionStatus = ConnectionStatus.DISCONNECTED;
  private volatile long lastConnectionUpdateUtc = -1;

  private TradingSessionStatus tradingSessionStatus;

  public FxcmSessionHelper(IGateway fxcmGateway, ConnectorListener connectorListener) {
    this.fxcmGateway = checkNotNull(fxcmGateway);
    this.connectorListener = checkNotNull(connectorListener);
    this.fxcmGateway.registerStatusMessageListener(new StatusMessageListener());
    this.fxcmGateway.registerGenericMessageListener(new GenericMessageListener());
  }

  public ConnectionInfo getConnectionInfo() {
    if (!fxcmGateway.isConnected()) {
      switch (connectionStatus) {
        case CONNECTED:
          updateConnectionStatus(ConnectionStatus.DISCONNECTED);
        case CONNECTING:
        case DISCONNECTED:
        case DISCONNECTING:
          break;
        default:
          throw new IllegalArgumentException(
              String.format("Unsupported status [%s].", connectionStatus));
      }
    }
    return new ConnectionInfo(connectionStatus, lastConnectionUpdateUtc);
  }

  public boolean isConnected() {
    return ConnectionStatus.CONNECTED.equals(getConnectionInfo().getConnectionStatus());
  }

  public void sendMessage(ITransportable message, Consumer<ITransportable> callback)
      throws Exception {
    checkNotNull(message);
    checkNotNull(callback);
    bindRequest(fxcmGateway.sendMessage(message), callback);
  }

  public TradingSecurity getSymbol(String instrumentId) {
    checkNotNullOrEmpty(instrumentId);
    tradingSessionStatusLock.lock();
    try {
      checkNotNull(
          tradingSessionStatus, "Trading session status is null, did you wait for onConnected?");
      return checkNotNull(tradingSessionStatus.getSecurity(instrumentId),
          String.format("Instrument [%s] not found.", instrumentId));
    } finally {
      tradingSessionStatusLock.unlock();
    }
  }

  public void updateTradingSessionStatus(TradingSessionStatus tradingSessionStatus) {
    tradingSessionStatusLock.lock();
    this.tradingSessionStatus = checkNotNull(tradingSessionStatus);
    tradingSessionStatusLock.unlock();
  }

  private void bindRequest(String requestId, Consumer<ITransportable> callback) {
    checkNotNullOrEmpty(requestId);
    callbacksLock.lock();
    try {
      callbacks.put(requestId, callback);
    } finally {
      callbacksLock.unlock();
    }
  }

  private void finaliseLogin() {
    Consumer<ITransportable> callback = message -> {
      if (message instanceof TradingSessionStatus) {
        updateTradingSessionStatus((TradingSessionStatus) message);
        updateConnectionStatus(ConnectionStatus.CONNECTED);
      } else {
        LOGGER.error(String.format(
            "Unexpected response from requestTradingSessionStatus: [%s]", message.toString()));
      }
    };
    bindRequest(fxcmGateway.requestTradingSessionStatus(), callback);
  }

  private boolean consumeMessage(String requestId, ITransportable iTransportable) {
    if (Strings.isNullOrEmpty(requestId)) {
      return false;
    }
    checkNotNull(iTransportable);
    callbacksLock.lock();
    try {
      final Consumer<ITransportable> callback = callbacks.remove(requestId);
      if (callback != null) {
        new Thread(() -> {
          try {
            callback.accept(iTransportable);
          } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
          }
        }).start();
      }
      return callback != null;
    } finally {
      callbacksLock.unlock();
    }
  }

  private void clearCallbacks() {
    callbacksLock.lock();
    try {
      callbacks.clear();
    } finally {
      callbacksLock.unlock();
    }
  }

  private void updateConnectionStatus(ConnectionStatus newStatus) {
    if (checkNotNull(newStatus).equals(connectionStatus)) {
      return;
    }
    ConnectionStatus previousStatus = connectionStatus;
    connectionStatus = newStatus;
    lastConnectionUpdateUtc = DateTimeUtils.getCurrentUtcTimestampMilliseconds();
    if (ConnectionStatus.CONNECTED.equals(newStatus)
        && !ConnectionStatus.CONNECTED.equals(previousStatus)) {
      connectorListener.onConnected();
    } else if (ConnectionStatus.DISCONNECTED.equals(newStatus)
        && !ConnectionStatus.DISCONNECTED.equals(previousStatus)) {
      connectorListener.onDisconnected();
    }
  }

  private final class GenericMessageListener implements IGenericMessageListener {
    @Override
    public void messageArrived(ITransportable iTransportable) {
      if (consumeMessage(iTransportable.getRequestID(), iTransportable)) {
        return;
      } else if (iTransportable instanceof MarketDataSnapshot) {
        MarketDataSnapshot marketDataSnapshot = (MarketDataSnapshot) iTransportable;
        if (Strings.isNullOrEmpty(marketDataSnapshot.getRequestID())
            && FXCMTimingIntervalFactory.TICK.equals(marketDataSnapshot.getFXCMTimingInterval())) {
          connectorListener.onPriceTick(
              FxcmFactory.createPriceTick((MarketDataSnapshot) iTransportable));
        }
      } else if (iTransportable instanceof UserResponse) {
        // The requestTradingSessionStatus() can be called once UserResponse is received.
        finaliseLogin();
      } else if (iTransportable instanceof TradingSessionStatus) {
        updateTradingSessionStatus((TradingSessionStatus) iTransportable);
      }
    }
  }

  private final class StatusMessageListener implements IStatusMessageListener {
    @Override
    public void messageArrived(ISessionStatus iSessionStatus) {
      if (iSessionStatus instanceof GenericSessionStatus) {
        switch (iSessionStatus.getStatusCode()){
          case ISessionStatus.STATUSCODE_CONNECTING:
            updateConnectionStatus(ConnectionStatus.CONNECTING);
            break;
          case ISessionStatus.STATUSCODE_LOGGEDIN:
            // Call requestTradingSessionStatus to unlock TradingSessionStatus. This will return a
            // UserResponse, after that another requestTradingSessionStatus call is required.
            clearCallbacks();
            fxcmGateway.requestTradingSessionStatus();
            break;
          case ISessionStatus.STATUSCODE_DISCONNECTING:
            updateConnectionStatus(ConnectionStatus.DISCONNECTING);
            break;
          case ISessionStatus.STATUSCODE_DISCONNECTED:
            clearCallbacks();
            updateConnectionStatus(ConnectionStatus.DISCONNECTED);
            break;
          case ISessionStatus.STATUSCODE_CRITICAL_ERROR:
          case ISessionStatus.STATUSCODE_ERROR:
            try {
              connectorListener.onGenericError(new Exception(iSessionStatus.toString()));
            } finally {
              if (!fxcmGateway.isConnected()) {
                updateConnectionStatus(ConnectionStatus.DISCONNECTED);
              }
            }
            break;
          default:
            // Intentionally empty.
        }
      }
    }
  }
}
