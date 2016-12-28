import io.algobox.connector.Connector;
import io.algobox.connector.ConnectorException;
import io.algobox.connector.ConnectorListener;
import io.algobox.connector.ConnectorOrderRequest;
import io.algobox.connector.oanada.OandaConnector;
import io.algobox.order.OrderResponse;
import io.algobox.price.PriceTick;
import org.junit.After;
import org.junit.Before;

import java.util.concurrent.CompletableFuture;

public class AbstractOandaConnectorIT {
  protected Connector connector;
  protected CompletableFuture<String> connectionFuture;
  protected CompletableFuture<PriceTick> priceTickFuture;
  protected CompletableFuture<OrderResponse> orderFuture;

  @Before
  public void init() throws ConnectorException {
    connectionFuture = new CompletableFuture<>();
    orderFuture = new CompletableFuture<>();
    priceTickFuture = new CompletableFuture<>();
    ConnectorListener connectorListener = new TestingConnectorListener();
    connector = new OandaConnector(
        IntegrationTestingConstants.DEFAULT_OANDA_API_KEY,
        IntegrationTestingConstants.DEFAULT_OANDA_ACCOUNT_NUMBER,
        false, connectorListener);
    connector.connect();
  }

  @After
  public void dispose() throws ConnectorException {
    connector.disconnect();
  }

  private class TestingConnectorListener implements ConnectorListener {
    @Override
    public void onConnected() {
      connectionFuture.complete("connected");
    }

    @Override
    public void onDisconnected() {
      connectionFuture.complete("disconneted");
    }

    @Override
    public void onGenericError(Throwable throwable) {
      connectionFuture.completeExceptionally(throwable);
    }

    @Override
    public void onPriceTick(PriceTick priceTick) {
      priceTickFuture.complete(priceTick);
    }

    @Override
    public void onOrderOpen(OrderResponse orderResponse) {
      orderFuture.complete(orderResponse);
    }

    @Override
    public void onOrderError(ConnectorOrderRequest connectorOrderRequest, Throwable throwable) {
      orderFuture.completeExceptionally(throwable);
    }
  }
}
