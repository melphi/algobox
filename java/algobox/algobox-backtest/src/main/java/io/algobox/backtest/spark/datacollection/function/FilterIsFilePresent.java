package io.algobox.backtest.spark.datacollection.function;

import io.algobox.backtest.spark.common.util.FileUtil;
import io.algobox.backtest.spark.datacollection.SparkDataCollectionHelper;
import org.apache.spark.api.java.function.Function;
import scala.Tuple3;

import static com.google.common.base.Preconditions.checkArgument;
import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;

public final class FilterIsFilePresent implements Function<Tuple3<String, Long, Long>, Boolean> {
  private final String pricesFolder;

  public FilterIsFilePresent(String pricesFolder) {
    this.pricesFolder = checkNotNullOrEmpty(pricesFolder);
    checkArgument(pricesFolder.endsWith("/"),
        String.format("[%s] should end with /", pricesFolder));
  }

  @Override
  public Boolean call(Tuple3<String, Long, Long> parameters) throws Exception {
    String fileName = pricesFolder + SparkDataCollectionHelper.getFileName(
        parameters._1(), parameters._2(), parameters._3());
    return FileUtil.isFilePresent(fileName);
  }
}
