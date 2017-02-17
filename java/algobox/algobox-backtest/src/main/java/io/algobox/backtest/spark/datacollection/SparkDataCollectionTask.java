package io.algobox.backtest.spark.datacollection;

import com.google.common.collect.ImmutableList;
import io.algobox.backtest.spark.common.AbstractSparkTask;
import io.algobox.backtest.spark.optimisation.SparkOptimisationTask;
import io.algobox.price.PriceTick;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class SparkDataCollectionTask extends AbstractSparkTask {
  private static final Logger LOGGER = LoggerFactory.getLogger(SparkOptimisationTask.class);
  private static final Collection<Class<?>> KRYO_CLASSES = ImmutableList.<Class<?>>builder()
      .add(PriceTick.class)
      .build();

  public SparkDataCollectionTask() {
    super(KRYO_CLASSES);
  }

  public static void main(String[] args) {
    throw new IllegalArgumentException("Not yet implemented.");
  }
}
