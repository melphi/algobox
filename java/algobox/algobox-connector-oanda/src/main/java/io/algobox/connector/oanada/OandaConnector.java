package io.algobox.connector.oanada;

import io.algobox.connector.ConnectionInfo;
import io.algobox.connector.ConnectionStatus;
import io.algobox.connector.Connector;
import io.algobox.connector.ConnectorException;
import io.algobox.connector.ConnectorInstrumentService;
import io.algobox.connector.ConnectorListener;
import io.algobox.connector.ConnectorOrderService;
import io.algobox.connector.ConnectorPriceService;
import io.algobox.connector.oanada.domain.OandaAccountResponse;
import io.algobox.util.DateTimeUtils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;

/**
 * OAnda client following REST-V20 API implementation
 * {@see http://developer.oanda.com/rest-live-v20/introduction}.
 */
public final class OandaConnector implements Connector {
  private static final String PATH_ACCOUNTS = "/v3/accounts";
  private static final String DEMO_API_URL = "https://api-fxpractice.oanda.com";
  private static final String DEMO_STREAMING_API_URL = "https://stream-fxpractice.oanda.com";
  private static final String LIVE_API_URL = "https://api-fxtrade.oanda.com";
  private static final String LIVE_STREAMING_API_URL = "https://stream-fxtrade.oanda.com";

  private final String accountNumber;
  private final ConnectorListener connectorListener;
  private final OandaConnectorHelper connectorHelper;
  private final ConnectorOrderService orderService;
  private final ConnectorPriceService priceService;

  private volatile ConnectionStatus connectionStatus = ConnectionStatus.DISCONNECTED;
  private volatile long connectionDateUtc = -1;

  public OandaConnector(String apiKey, String accountNumber, Boolean isLiveAccount,
      ConnectorListener connectorListener) {
    this.accountNumber = checkNotNullOrEmpty(accountNumber, "Missing connector number.");
    this.connectorListener = connectorListener;
    this.connectorHelper = new OandaConnectorHelper(accountNumber, apiKey, getApiUrl(isLiveAccount),
        getStreamingApiUrl(isLiveAccount));
    this.orderService = new OandaOrderService(this.connectorHelper, this.connectorListener);
    this.priceService = new OandaPriceService(this.connectorHelper, this.connectorListener);
  }

  @Override
  public void connect() throws ConnectorException {
    String path = PATH_ACCOUNTS + "/" + accountNumber;
    try {
      OandaAccountResponse oandaAccount = connectorHelper.doGet(path, OandaAccountResponse.class);
      checkArgument(oandaAccount.getAccount().getId().equals(accountNumber),
          "Error while retrieving connector by id.");
    } catch (Exception e) {
      throw new ConnectorException(
          String.format("Unable to connect connector [%s]: [%s]", accountNumber, e.getMessage()), e);
    }
    updateConnection(true);
  }

  @Override
  public void disconnect() throws ConnectorException {
    updateConnection(false);
  }

  @Override
  public ConnectionInfo getConnectionInfo() {
    return new ConnectionInfo(connectionStatus, connectionDateUtc);
  }

  @Override
  public ConnectorPriceService getPriceService() throws ConnectorException {
    return priceService;
  }

  @Override
  public ConnectorInstrumentService getInstrumentService() throws ConnectorException {
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public ConnectorOrderService getOrderService() throws ConnectorException {
    return orderService;
  }

  private void updateConnection(boolean connected) {
    ConnectionStatus oldStatus = connectionStatus;
    connectionStatus = connected ? ConnectionStatus.CONNECTED : ConnectionStatus.DISCONNECTED;
    if (!oldStatus.equals(connectionStatus)) {
      this.connectionDateUtc = DateTimeUtils.getCurrentUtcTimestampMilliseconds();
      if (ConnectionStatus.CONNECTED.equals(connectionStatus)) {
        connectorListener.onConnected();
      } else if(ConnectionStatus.DISCONNECTED.equals(connectionStatus)) {
        connectorListener.onDisconnected();
      }
    }
  }

  private String getApiUrl(Boolean isLiveAccount) {
    checkNotNull(isLiveAccount);
    return isLiveAccount ? LIVE_API_URL : DEMO_API_URL;
  }

  private String getStreamingApiUrl(Boolean isLiveAccount) {
    checkNotNull(isLiveAccount);
    return isLiveAccount ? LIVE_STREAMING_API_URL : DEMO_STREAMING_API_URL;
  }
}
