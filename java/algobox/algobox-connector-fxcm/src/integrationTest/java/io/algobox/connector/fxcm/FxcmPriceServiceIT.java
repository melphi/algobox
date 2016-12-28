package io.algobox.connector.fxcm;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import io.algobox.connector.ConnectorException;
import io.algobox.connector.ConnectorListener;
import io.algobox.connector.ConnectorOrderRequest;
import io.algobox.order.OrderResponse;
import io.algobox.price.PriceTick;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FxcmPriceServiceIT {
  private FxcmConnector fxcmConnector;
  private CompletableFuture<String> connectionFuture = new CompletableFuture<>();
  private CompletableFuture<PriceTick> priceTickFuture = new CompletableFuture<>();
  private FxcmConnectorListener connectorListener = new FxcmConnectorListener(
      priceTickFuture, connectionFuture);

  @Before
  public void init()
      throws ExecutionException, InterruptedException, TimeoutException, ConnectorException {
    CompletableFuture<String> completableFuture = new CompletableFuture<>();
    this.fxcmConnector = new FxcmConnector(
        IntegrationTestConstants.FXCM_USERNAME,
        IntegrationTestConstants.FXCM_PASSWORD,
        IntegrationTestConstants.FXCM_TERMINAL,
        IntegrationTestConstants.FXCM_SERVER,
        connectorListener);
    fxcmConnector.connect();
    assertEquals("connected", completableFuture.get(2, TimeUnit.MINUTES));
  }

  @After
  public void tearDown() throws ConnectorException {
    fxcmConnector.disconnect();
  }

  @Test
  public void test_shouldReceivePriceTicks()
      throws ConnectorException, ExecutionException, TimeoutException, InterruptedException {
    fxcmConnector.getPriceService().subscribeInstrument(
        IntegrationTestConstants.INSTRUMENT_ID_EURUSD);
    assertPriceTick(priceTickFuture.get(10, TimeUnit.SECONDS));
  }

  @Test
  public void test_shouldUnSubscribePriceTicks()
      throws ConnectorException, InterruptedException, ExecutionException, TimeoutException {
    assertFalse(fxcmConnector.getPriceService().isInstrumentSubscribed(
        IntegrationTestConstants.INSTRUMENT_ID_EURUSD));
    fxcmConnector.getPriceService().subscribeInstrument(
        IntegrationTestConstants.INSTRUMENT_ID_EURUSD);
    priceTickFuture.get(10, TimeUnit.SECONDS);
    assertTrue(fxcmConnector.getPriceService().isInstrumentSubscribed(
        IntegrationTestConstants.INSTRUMENT_ID_EURUSD));
    fxcmConnector.getPriceService().unSubscribeInstrument(
        IntegrationTestConstants.INSTRUMENT_ID_EURUSD);
    Thread.sleep(1000);
    assertFalse(fxcmConnector.getPriceService().isInstrumentSubscribed(
        IntegrationTestConstants.INSTRUMENT_ID_EURUSD));
  }

  @Test
  public void test_shouldUnSubscribeInstrumentsAfterLogout()
      throws ConnectorException, InterruptedException, ExecutionException, TimeoutException {
    fxcmConnector.getPriceService().subscribeInstrument(
        IntegrationTestConstants.INSTRUMENT_ID_EURUSD);
    assertPriceTick(priceTickFuture.get(10, TimeUnit.SECONDS));
    fxcmConnector.disconnect();
    Thread.sleep(1000);
    assertTrue(Iterables.isEmpty(fxcmConnector.getPriceService().getSubscribedInstruments()));
  }

  private void assertPriceTick(PriceTick priceTick) {
    assertNotNull(priceTick);
    assertTrue(!Strings.isNullOrEmpty(priceTick.getInstrument()));
    assertTrue(priceTick.getAsk() > 0.0);
    assertTrue(priceTick.getBid() > 0.0);
    assertTrue(priceTick.getTime() > 0);
  }

  private class FxcmConnectorListener implements ConnectorListener {
    private CompletableFuture<PriceTick> priceTickFuture;
    private CompletableFuture<String> connectionFuture;

    public FxcmConnectorListener(
        CompletableFuture<PriceTick> priceTickFuture, CompletableFuture<String> connectionFuture) {
      this.priceTickFuture = priceTickFuture;
      this.connectionFuture = connectionFuture;
    }

    @Override
    public void onConnected() {
      connectionFuture.complete("connected");
    }

    @Override
    public void onDisconnected() {
      connectionFuture.complete("disconnected");
    }

    @Override
    public void onGenericError(Throwable throwable) {
      connectionFuture.complete("error");
    }

    @Override
    public void onPriceTick(PriceTick priceTick) {
      priceTickFuture.complete(priceTick);
    }

    @Override
    public void onOrderOpen(OrderResponse orderResponse) {
      // Intentionally empty.
    }

    @Override
    public void onOrderError(ConnectorOrderRequest orderRequest, Throwable throwable) {
      // Intentionally empty.
    }
  }
}
