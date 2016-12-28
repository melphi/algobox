package io.algobox.connector.fxcm;

import com.fxcm.external.api.transport.FXCMLoginProperties;
import com.fxcm.external.api.transport.GatewayFactory;
import com.fxcm.external.api.transport.IGateway;
import io.algobox.connector.ConnectionInfo;
import io.algobox.connector.ConnectionStatus;
import io.algobox.connector.Connector;
import io.algobox.connector.ConnectorException;
import io.algobox.connector.ConnectorInstrumentService;
import io.algobox.connector.ConnectorListener;
import io.algobox.connector.ConnectorOrderRequest;
import io.algobox.connector.ConnectorOrderService;
import io.algobox.connector.ConnectorPriceService;
import io.algobox.order.OrderResponse;
import io.algobox.price.PriceTick;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

public final class FxcmConnector implements Connector {
  private static final Logger LOGGER = LoggerFactory.getLogger(FxcmConnector.class);

  private final String username;
  private final String password;
  private final String terminal;
  private final String server;

  private final IGateway fxcmGateway;
  private final FxcmPriceService priceService;
  private final FxcmSessionHelper fxcmSessionHelper;

  public FxcmConnector(String username, String password, String terminal, String server,
      ConnectorListener connectorListener) {
    this.username = username;
    this.password = password;
    this.terminal = terminal;
    this.server = server;
    this.fxcmGateway = GatewayFactory.createGateway();
    this.fxcmSessionHelper = new FxcmSessionHelper(
        fxcmGateway, new ConnectorListenerWrapper(connectorListener));
    this.priceService = new FxcmPriceService(fxcmSessionHelper);
  }

  @Override
  public void connect() throws ConnectorException {
    LOGGER.info("Connecting to FXCM.");
    ConnectionStatus connectionStatus = getConnectionInfo().getConnectionStatus();
    switch (connectionStatus) {
      case CONNECTED:
      case CONNECTING:
        LOGGER.info("Connection attempt skipped because service is connected or connecting.");
        return;
      case DISCONNECTED:
      case DISCONNECTING:
        break;
      default:
        throw new IllegalArgumentException(
            String.format("Unsupported status [%s].", connectionStatus));
    }
    try {
      openConnection();
    } catch (Exception e) {
      String message = String.format("Error while connecting to FXCM: [%s].", e.getMessage());
      LOGGER.error(message, e);
      throw new ConnectorException(message, e);
    }
  }

  @Override
  public void disconnect() throws ConnectorException {
    if (!fxcmGateway.isConnected()) {
      LOGGER.info("Disconnection attempt skipped because service was already disconnected.");
      return;
    }
    fxcmGateway.logout();
  }

  @Override
  public ConnectionInfo getConnectionInfo() {
    return fxcmSessionHelper.getConnectionInfo();
  }

  @Override
  public ConnectorPriceService getPriceService() throws ConnectorException {
    return priceService;
  }

  @Override
  public ConnectorOrderService getOrderService() throws ConnectorException {
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  @Override
  public ConnectorInstrumentService getInstrumentService() throws ConnectorException {
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  private void openConnection() throws Exception {
    FXCMLoginProperties properties = new FXCMLoginProperties(username, password, terminal, server);
    fxcmGateway.login(properties);
  }

  private final class ConnectorListenerWrapper implements ConnectorListener {
    private final ConnectorListener connectorListener;

    public ConnectorListenerWrapper(ConnectorListener connectorListener) {
      this.connectorListener = checkNotNull(connectorListener);
    }

    @Override
    public void onConnected() {
      connectorListener.onConnected();
    }

    @Override
    public void onDisconnected() {
      priceService.onConnectorDisconnected();
      connectorListener.onDisconnected();
    }

    @Override
    public void onPriceTick(PriceTick priceTick) {
      connectorListener.onPriceTick(priceTick);
    }

    @Override
    public void onOrderOpen(OrderResponse orderResponse) {
      connectorListener.onOrderOpen(orderResponse);
    }

    @Override
    public void onOrderError(ConnectorOrderRequest orderRequest, Throwable throwable) {
      connectorListener.onOrderError(orderRequest, throwable);
    }

    @Override
    public void onGenericError(Throwable throwable) {
      connectorListener.onGenericError(throwable);
    }
  }
}
