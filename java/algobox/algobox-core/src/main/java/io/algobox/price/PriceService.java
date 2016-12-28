package io.algobox.price;

public interface PriceService {
  Iterable<PriceTick> getPriceTicks(
      String instrumentId, Long fromTimestampUtc, Long toTimestampUtc);
}
