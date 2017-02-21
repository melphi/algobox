package io.algobox.backtest.spark.datacollection;

import io.algobox.backtest.spark.common.client.impl.TestingPriceService;
import io.algobox.testing.TestingInstrumentService;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;

public class SparkDataCollectionTaskTest {
  private static final long FROM_DATE = ZonedDateTime.of(2016, 2, 22, 7, 0, 0, 0, ZoneId.of("UTC"))
      .toInstant()
      .toEpochMilli();
  private static final long TO_DATE = ZonedDateTime.of(2016, 2, 23, 17, 0, 0, 0, ZoneId.of("UTC"))
      .toInstant()
      .toEpochMilli();

  @Test
  public void shouldCollectAndSavePrices() throws IOException {
    File pricesFolder = createPricesFolder();
    SparkDataCollectionTask task = new SparkDataCollectionTask();
    task.run(new TestingPriceService(true), new TestingInstrumentService(), pricesFolder.getPath(),
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
