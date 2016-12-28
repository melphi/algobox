package io.algobox.connector.fxcm;

import io.algobox.connector.ConnectorException;
import io.algobox.connector.ConnectorListener;
import io.algobox.connector.ConnectorOrderRequest;
import io.algobox.order.OrderResponse;
import io.algobox.price.PriceTick;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;

public abstract class AbstractFxcmConnectorServiceIT {
  protected static FxcmConnector fxcmConnector;

  @BeforeClass
  public static void initClass()
      throws ExecutionException, InterruptedException, TimeoutException, ConnectorException {
    CompletableFuture<String> completableFuture = new CompletableFuture<>();
    FxcmConnectorListener connectorListener = new FxcmConnectorListener(completableFuture);
    fxcmConnector = new FxcmConnector(
        IntegrationTestConstants.FXCM_USERNAME,
        IntegrationTestConstants.FXCM_PASSWORD,
        IntegrationTestConstants.FXCM_TERMINAL,
        IntegrationTestConstants.FXCM_SERVER,
        connectorListener);
    fxcmConnector.connect();
    assertEquals("connected", completableFuture.get(2, TimeUnit.MINUTES));
  }

  @AfterClass
  public static void tearDownClass() throws ConnectorException {
    fxcmConnector.disconnect();
  }

  private static class FxcmConnectorListener implements ConnectorListener {
    private CompletableFuture<String> completableFuture;

    public FxcmConnectorListener(CompletableFuture<String> completableFuture) {
      this.completableFuture = completableFuture;
    }

    @Override
    public void onConnected() {
      completableFuture.complete("connected");
    }

    @Override
    public void onDisconnected() {
      completableFuture.complete("disconnected");
    }

    @Override
    public void onGenericError(Throwable throwable) {
      completableFuture.complete("error");
    }

    @Override
    public void onPriceTick(PriceTick priceTick) {
      // Intentionally empty.
    }

    @Override
    public void onOrderOpen(OrderResponse orderResponse) {
      // Intentionally empty.
    }

    @Override
    public void onOrderError(ConnectorOrderRequest orderRequest, Throwable cause) {
      // Intentionally empty.
    }
  }
}
