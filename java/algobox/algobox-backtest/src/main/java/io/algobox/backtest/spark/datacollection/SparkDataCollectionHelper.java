package io.algobox.backtest.spark.datacollection;

import com.google.common.collect.Lists;
import io.algobox.instrument.InstrumentInfoDetailed;
import io.algobox.instrument.MarketHours;
import io.algobox.util.MarketHoursUtils;
import scala.Tuple3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

public final class SparkDataCollectionHelper {
  private static final String BASE_FILE_NAME = "prices_";
  private static final long ONE_DAY = 24 * 60 * 60 * 1000L;

  public static List<Tuple3<String, Long, Long>> getOrderedDays(
      long fromDate, long toDate, String instrumentId, InstrumentInfoDetailed instrumentInfo) {
    checkArgument(toDate > fromDate, "Invalid from or to date.");
    ArrayList<Tuple3<String, Long, Long>> result = Lists.newArrayList();
    for (long i = fromDate; i < toDate + ONE_DAY;) {
      Optional<MarketHours> marketHours = MarketHoursUtils.getMarketHours(instrumentInfo, i);
      if (marketHours.isPresent()) {
        if (marketHours.get().getMarketClose() > toDate) {
          break;
        } else {
          result.add(Tuple3.apply(instrumentId, marketHours.get().getMarketOpen(),
              marketHours.get().getMarketClose()));
        }
      }
      i += ONE_DAY;
    }
    return result;
  }

  public static String getFileName(String instrumentId, long fromTimestamp, long toTimestamp) {
    return String.format("%s%s_%d_%d.parquet", BASE_FILE_NAME, instrumentId, fromTimestamp,
        toTimestamp);
  }
}
