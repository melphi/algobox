package io.algobox.api.module.indicator.dao.impl;

import io.algobox.api.module.indicator.dao.PriceOhlcCacheDao;
import io.algobox.api.module.indicator.domain.mdb.PriceOhlcCacheMdb;
import io.algobox.api.module.strategy.dao.impl.AbstractTestingDao;
import io.algobox.price.Ohlc;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;

public class TestingPriceOhlcCacheDao extends AbstractTestingDao<PriceOhlcCacheMdb>
    implements PriceOhlcCacheDao {
  @Override
  public Optional<Ohlc> getPriceOhlc(String instrumentId, Long fromTimestamp, Long toTimestamp) {
    String id = getId(instrumentId, fromTimestamp, toTimestamp);
    return Optional.ofNullable(internalGetValue(id));
  }

  @Override
  public void saveOrUpdatePriceOhlc(Ohlc ohlc, Long fromTimestamp, Long toTimestamp) {
    for (PriceOhlcCacheMdb value: internalGetAllValues()) {
      if (value.getInstrument().equals(ohlc.getInstrument())
          && value.getFromTime() == fromTimestamp
          && value.getToTime() == toTimestamp) {
        throw new IllegalArgumentException("Duplicated record.");
      }
    }
    String id = getId(ohlc.getInstrument(), fromTimestamp, toTimestamp);
    internalSaveValue(id, new PriceOhlcCacheMdb(ohlc, fromTimestamp, toTimestamp, 111));
  }

  private String getId(String instrumentId, long fromTimestamp, long toTimestamp) {
    checkNotNullOrEmpty(instrumentId);
    checkArgument(fromTimestamp > 0);
    checkArgument(toTimestamp > 0);
    return instrumentId + ":" + fromTimestamp + ":" + toTimestamp;
  }
}
