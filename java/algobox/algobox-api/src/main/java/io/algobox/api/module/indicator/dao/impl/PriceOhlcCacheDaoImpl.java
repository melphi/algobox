package io.algobox.api.module.indicator.dao.impl;

import com.mongodb.client.MongoDatabase;
import io.algobox.api.module.indicator.dao.PriceOhlcCacheDao;
import io.algobox.api.module.indicator.domain.mdb.PriceOhlcCacheMdb;
import io.algobox.common.dao.AbstractMongoDao;
import io.algobox.price.Ohlc;
import io.algobox.util.DateTimeUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

@Service
public final class PriceOhlcCacheDaoImpl extends AbstractMongoDao<PriceOhlcCacheMdb>
    implements PriceOhlcCacheDao {
  private static final Document INDEX = new Document()
      .append(PriceOhlcCacheMdb.FIELD_INSTRUMENT, 1)
      .append(PriceOhlcCacheMdb.FIELD_FROM_TIME, 1)
      .append(PriceOhlcCacheMdb.FIELD_TO_TIME, 1);

  @Inject
  public PriceOhlcCacheDaoImpl(MongoDatabase database) {
    super(database, PriceOhlcCacheMdb.COLLECTION_PRICE_OHLC_CACHE, PriceOhlcCacheMdb.class);
    createIndexIfNotExists(INDEX);
  }

  @Override
  public Optional<Ohlc> getPriceOhlc(
      String instrumentId, Long fromTimestamp, Long toTimestamp) {
    Bson query = and(eq(PriceOhlcCacheMdb.FIELD_INSTRUMENT, instrumentId),
        eq(PriceOhlcCacheMdb.FIELD_FROM_TIME, fromTimestamp),
        eq(PriceOhlcCacheMdb.FIELD_TO_TIME, toTimestamp));
    return Optional.ofNullable(findFirstWithRetry(query));
  }

  @Override
  public void saveOrUpdatePriceOhlc(Ohlc ohlc, Long fromTimestamp, Long toTimestamp) {
    checkNotNull(ohlc);
    checkArgument(fromTimestamp > 0);
    checkArgument(toTimestamp > 0);
    checkArgument(toTimestamp > fromTimestamp);
    long createdOn = DateTimeUtils.getCurrentUtcTimestampMilliseconds();
    PriceOhlcCacheMdb priceBarCacheMdb = new PriceOhlcCacheMdb(
        ohlc, fromTimestamp, toTimestamp, createdOn);
    insertOneWithRetry(priceBarCacheMdb);
  }
}
