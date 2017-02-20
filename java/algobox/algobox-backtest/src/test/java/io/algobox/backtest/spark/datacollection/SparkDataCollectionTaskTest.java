package io.algobox.backtest.spark.datacollection;

import io.algobox.backtest.spark.common.client.impl.TestingPriceService;
import io.algobox.testing.TestingInstrumentService;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;

public class SparkDataCollectionTaskTest {
  private static final long FROM_DATE = 1L;
  private static final long TO_DATE = 1L;

  @Test
  public void shouldCollectAndSavePrices() throws IOException {
    File pricesFolder = createPricesFolder();
    SparkDataCollectionTask task = new SparkDataCollectionTask();
    task.run(new TestingPriceService(), new TestingInstrumentService(), pricesFolder.getName(),
        FROM_DATE, TO_DATE, TestingInstrumentService.INSTRUMENT_DAX);
    checkArgument(pricesFolder.delete());
  }

  private File createPricesFolder() {
    File pricesFolder = new File("/tmp/saved_prices/" + UUID.randomUUID().toString());
    checkArgument(pricesFolder.mkdirs());
    pricesFolder.deleteOnExit();
    return pricesFolder;
  }
}
