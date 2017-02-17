package io.algobox.backtest.spark.common;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.Collection;

public abstract class AbstractSparkTask {
  private final Class<?>[] kryoClasses;

  public AbstractSparkTask(Collection<Class<?>> kryoClasses) {
    this.kryoClasses = kryoClasses.toArray(new Class<?>[] {});
  }

  protected JavaSparkContext createSparkContext(String taskName) {
    SparkConf sparkConf = new SparkConf()
        .registerKryoClasses(kryoClasses)
        .setMaster("local[*]")
        .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
        .setAppName(taskName);
    return JavaSparkContext.fromSparkContext(SparkContext.getOrCreate(sparkConf));
  }
}
