import com.google.common.collect.Iterables;
import io.algobox.connector.ConnectorException;
import io.algobox.price.PriceTick;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OandaConnectorPriceServiceIT extends AbstractOandaConnectorIT {
  private static final String INSTRUMENT_EUR_USD = "EUR_USD";
  private static final String INSTRUMENT_USD_CAD = "USD_CAD";

  @Test
  public void testSubscribeOneInstrument()
      throws ConnectorException, InterruptedException, ExecutionException, TimeoutException {
    connector.getPriceService().subscribeInstrument(INSTRUMENT_EUR_USD);
    assertEquals(INSTRUMENT_EUR_USD,
        Iterables.getOnlyElement(connector.getPriceService().getSubscribedInstruments()));
    PriceTick priceTick = priceTickFuture.get(10, TimeUnit.SECONDS);
    assertEquals(INSTRUMENT_EUR_USD, priceTick.getInstrument());
    assertTrue(priceTick.getTime() > 0);
    assertTrue(priceTick.getAsk() > 0);
    assertTrue(priceTick.getBid() > 0);
  }

  @Test
  public void testSubscribeTwoInstrument()
      throws ConnectorException, InterruptedException, ExecutionException, TimeoutException {
    connector.getPriceService().subscribeInstrument(INSTRUMENT_EUR_USD);
    connector.getPriceService().subscribeInstrument(INSTRUMENT_USD_CAD);
    assertEquals(2, connector.getPriceService().getSubscribedInstruments().size());
    PriceTick priceTick = priceTickFuture.get(10, TimeUnit.SECONDS);
    assertTrue(INSTRUMENT_EUR_USD.equals(priceTick.getInstrument())
        || INSTRUMENT_USD_CAD.equals(priceTick.getInstrument()));
    assertTrue(priceTick.getTime() > 0);
    assertTrue(priceTick.getAsk() > 0);
    assertTrue(priceTick.getBid() > 0);
  }

  @Test
  public void testUnSubscribeOneInstrument() throws ConnectorException, InterruptedException {
    connector.getPriceService().subscribeInstrument(INSTRUMENT_EUR_USD);
    connector.getPriceService().subscribeInstrument(INSTRUMENT_USD_CAD);
    connector.getPriceService().unSubscribeInstrument(INSTRUMENT_EUR_USD);
    connector.getPriceService().unSubscribeInstrument(INSTRUMENT_USD_CAD);
    assertTrue(connector.getPriceService().getSubscribedInstruments().isEmpty());
  }
}
