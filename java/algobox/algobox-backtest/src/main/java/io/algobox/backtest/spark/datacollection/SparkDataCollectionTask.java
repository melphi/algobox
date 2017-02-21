package io.algobox.backtest.spark.datacollection;

import com.google.common.collect.ImmutableList;
import io.algobox.backtest.spark.common.AbstractSparkTask;
import io.algobox.backtest.spark.datacollection.function.FilterIsFilePresent;
import io.algobox.backtest.spark.datacollection.function.TimesToPricesMap;
import io.algobox.backtest.spark.optimisation.SparkOptimisationTask;
import io.algobox.instrument.InstrumentInfoDetailed;
import io.algobox.instrument.InstrumentService;
import io.algobox.price.PriceService;
import io.algobox.price.PriceTick;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;

public class SparkDataCollectionTask extends AbstractSparkTask {
  private static final String TASK_NAME = "Prices aggregation from [%d] to [%d].";
  private static final Logger LOGGER = LoggerFactory.getLogger(SparkOptimisationTask.class);
  private static final Collection<Class<?>> KRYO_CLASSES = ImmutableList.<Class<?>>builder()
      .add(ArrayList.class)
      .add(PriceTick.class)
      .build();

  public SparkDataCollectionTask() {
    super(KRYO_CLASSES);
  }

  public static void main(String[] args) {
    throw new IllegalArgumentException("Not yet implemented.");
  }

  public void run(PriceService priceService, InstrumentService instrumentService,
      String pricesFolder, long fromDate, long toDate, String instrumentId) {
    checkNotNullOrEmpty(pricesFolder, "Missing prices folder.");
    checkNotNullOrEmpty(instrumentId, "Missing instrument id.");
    checkArgument(fromDate > 0, "Invalid from date");
    checkArgument(toDate > fromDate, "Invalid to date");
    if (!pricesFolder.endsWith("/")) {
      pricesFolder += "/";
    }

    List<Tuple3<String, Long, Long>> orderedDays = getOrderedDays(
        instrumentId, instrumentService, fromDate, toDate);
    LOGGER.info("Processing [%d] days of [%s] from [%d] to [%d]. Using [%s] as destination folder.",
        orderedDays.size(), instrumentId, fromDate, toDate, pricesFolder);

    createSparkContext(String.format(TASK_NAME, fromDate, toDate))
        .parallelize(orderedDays)
        .filter(new FilterIsFilePresent(pricesFolder))
        .map(new TimesToPricesMap(priceService))
        .count();
  }

  private List<Tuple3<String, Long, Long>> getOrderedDays(
      String instrumentId, InstrumentService instrumentService, long fromDate, long toDate) {
    InstrumentInfoDetailed instrumentInfo = instrumentService.getInstrumentInfo(instrumentId);
    return SparkDataCollectionHelper.getOrderedDays(fromDate, toDate, instrumentId, instrumentInfo);
  }
}
