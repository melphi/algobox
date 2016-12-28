package io.algobox.connector.dummy;

import com.google.common.collect.Sets;
import io.algobox.connector.ConnectorException;
import io.algobox.connector.ConnectorPriceService;
import io.algobox.price.PriceTick;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

public class DummyPriceServiceTest extends AbstractDummyConnectorTest {
  private static final String DEFAULT_INSTRUMENT_1 = "instrument1";
  private static final String DEFAULT_INSTRUMENT_2 = "instrument2";
  private static final String DEFAULT_INSTRUMENT_3 = "instrument3";

  private ConnectorPriceService priceService;

  @Test
  public void shouldStopPriceStreamOnDisconnection()
      throws InterruptedException, ConnectorException {
    reset(connectorListener);
    connector.disconnect();
    Thread.sleep(PRICES_POLLING_MILLISECONDS + 1);
    verify(connectorListener, never()).onPriceTick(any());
  }

  @Test
  public void shouldSubscribeInstrument() throws Exception {
    ArgumentCaptor<PriceTick> priceTicks = ArgumentCaptor.forClass(PriceTick.class);
    Set<String> subscriptions = Sets.newHashSet(DEFAULT_INSTRUMENT_1, DEFAULT_INSTRUMENT_2);
    priceService.subscribeInstrument(DEFAULT_INSTRUMENT_1);
    priceService.subscribeInstrument(DEFAULT_INSTRUMENT_2);
    verify(connectorListener,
        timeout(PRICES_POLLING_MILLISECONDS + 10).times(2)).onPriceTick(priceTicks.capture());
    for (PriceTick priceTick: priceTicks.getAllValues()) {
      subscriptions.remove(priceTick.getInstrument());
    }
    assertTrue(subscriptions.isEmpty());
  }

  @Test
  public void shouldUnSubscribeInstrument() throws Exception {
    priceService.subscribeInstrument(DEFAULT_INSTRUMENT_1);
    priceService.subscribeInstrument(DEFAULT_INSTRUMENT_2);

    priceService.unSubscribeInstrument(DEFAULT_INSTRUMENT_1);
    ArgumentCaptor<PriceTick> priceTicks = ArgumentCaptor.forClass(PriceTick.class);
    verify(connectorListener,
        timeout(PRICES_POLLING_MILLISECONDS + 10).times(1)).onPriceTick(priceTicks.capture());
    assertEquals(DEFAULT_INSTRUMENT_2, priceTicks.getValue().getInstrument());

    reset(connectorListener);
    priceService.unSubscribeInstrument(DEFAULT_INSTRUMENT_2);
    Thread.sleep(PRICES_POLLING_MILLISECONDS + 1);
    verify(connectorListener, never()).onPriceTick(any());
  }

  @Test
  public void shouldShowSubscribedInstruments() throws Exception {
    assertTrue(priceService.getSubscribedInstruments().isEmpty());
    priceService.subscribeInstrument(DEFAULT_INSTRUMENT_1);
    priceService.subscribeInstrument(DEFAULT_INSTRUMENT_2);
    assertEquals(2, priceService.getSubscribedInstruments().size());
    assertTrue(priceService.getSubscribedInstruments().contains(DEFAULT_INSTRUMENT_1));
    assertTrue(priceService.getSubscribedInstruments().contains(DEFAULT_INSTRUMENT_2));
    assertTrue(priceService.isInstrumentSubscribed(DEFAULT_INSTRUMENT_1));
    assertTrue(priceService.isInstrumentSubscribed(DEFAULT_INSTRUMENT_2));
    assertFalse(priceService.isInstrumentSubscribed(DEFAULT_INSTRUMENT_3));
  }

  @Override
  protected void completeInit() throws ConnectorException {
    priceService = connector.getPriceService();
  }
}
