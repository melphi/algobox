package io.algobox.backtest.spark.datacollection.function;

import com.google.common.collect.Lists;
import io.algobox.price.PriceService;
import io.algobox.price.PriceTick;
import org.apache.spark.api.java.function.Function;
import scala.Tuple3;
import scala.Tuple4;

import java.util.List;

public final class TimesToPricesMap implements Function<
    Tuple3<String, Long, Long>, Tuple4<String, Long, Long, List<PriceTick>>> {
  private final PriceService priceService;

  public TimesToPricesMap(PriceService priceService) {
    this.priceService = priceService;
  }

  @Override
  public Tuple4<String, Long, Long, List<PriceTick>> call(Tuple3<String, Long, Long> times)
      throws Exception {
    Iterable<PriceTick> prices = priceService.getPriceTicks(times._1(), times._2(), times._3());
    return Tuple4.apply(times._1(), times._2(), times._3(), Lists.newArrayList(prices));
  }
}
