package io.algobox.datacollector.module.pricestage.dao.impl;

import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import io.algobox.common.dao.AbstractMongoDao;
import io.algobox.datacollector.module.pricestage.dao.PriceTickStageDao;
import io.algobox.datacollector.module.pricestage.domain.mdb.PriceTickStageMdb;
import io.algobox.price.PriceTick;
import io.algobox.util.JsonUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.mongodb.client.model.Filters.eq;
import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;

@Service
public final class PriceTickStageDaoImpl extends AbstractMongoDao<PriceTickStageMdb>
    implements PriceTickStageDao {
  @Inject
  public PriceTickStageDaoImpl(MongoDatabase database) {
    super(database, PriceTickStageMdb.COLLECTION_PRICE_TICKS_STAGE, PriceTickStageMdb.class);
  }

  @Override
  public PriceTickStageMdb findLast(String instrumentId) {
    checkNotNullOrEmpty(instrumentId);
    long size = count(instrumentId);
    if (size > 0) {
      Bson query = eq(PriceTickStageMdb.FIELD_INSTRUMENT, instrumentId);
      Document result = collection.find(query)
          .skip((int) size - 1)
          .first();
      return JsonUtils.fromJson(JSON.serialize(result), persistentClass);
    } else {
      return null;
    }
  }

  @Override
  public long count(String instrumentId) {
    checkNotNullOrEmpty(instrumentId);
    return collection.count(eq(PriceTickStageMdb.FIELD_INSTRUMENT, instrumentId));
  }

  @Override
  public void save(PriceTick priceTick, String source) {
    checkNotNull(priceTick);
    checkNotNullOrEmpty(source);
    PriceTickStageMdb priceTickStageMdb = new PriceTickStageMdb(priceTick.getInstrument(),
        priceTick.getTime(), priceTick.getAsk(), priceTick.getBid(), source);
    insertOneWithRetry(priceTickStageMdb);
  }
}
