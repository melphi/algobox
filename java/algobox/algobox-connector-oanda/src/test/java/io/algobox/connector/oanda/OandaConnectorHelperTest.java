package io.algobox.connector.oanda;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import io.algobox.connector.oanada.OandaConnectorHelper;
import io.algobox.price.PriceTick;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OandaConnectorHelperTest {
  private static final String SAMPLE_PRICE_TICKS = "samplePriceTicks.txt";

  @Test
  public void testParsePriceLine() throws IOException {
    Set<PriceTick> priceTicks = Sets.newHashSet();
    InputStream inputStream =
        OandaConnectorHelperTest.class.getClassLoader().getResourceAsStream(SAMPLE_PRICE_TICKS);
    checkNotNull(inputStream, "File not found.");
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    String line;
    while ((line = bufferedReader.readLine()) != null) {
      PriceTick priceTick = OandaConnectorHelper.parsePriceLine(line);
      if (priceTick != null) {
        assertFalse(Strings.isNullOrEmpty(priceTick.getInstrument()));
        assertTrue(priceTick.getAsk() > 0);
        assertTrue(priceTick.getBid() > 0);
        assertTrue(priceTick.getTime() > 0);
        priceTicks.add(priceTick);
      }
    }
    assertEquals(4, priceTicks.size());
  }
}
