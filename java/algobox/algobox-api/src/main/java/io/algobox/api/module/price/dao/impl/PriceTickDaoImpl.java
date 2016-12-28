package io.algobox.api.module.price.dao.impl;

import com.google.common.collect.Maps;
import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoClientException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Sorts;
import com.mongodb.util.JSON;
import io.algobox.api.module.price.dao.PriceTickDao;
import io.algobox.api.module.price.domain.mdb.PriceTickMdb;
import io.algobox.price.PriceTick;
import io.algobox.util.JsonUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;
import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;

/**
 * Prices are saved in separated collection by instrument id.
 */
@Service
public final class PriceTickDaoImpl implements PriceTickDao {
  private final Map<String, MongoCollection<Document>> collectionsByInstrumentId =
      Maps.newConcurrentMap();
  private static final Document INDEX = new Document().append(PriceTickMdb.FIELD_TIME, -1);

  private final MongoDatabase mongoDatabase;

  @Inject
  public PriceTickDaoImpl(MongoDatabase database) {
    this.mongoDatabase = database;
  }

  @Override
  public void save(PriceTick priceTick, String source) {
    checkNotNull(priceTick);
    checkNotNullOrEmpty(source);
    checkNotNullOrEmpty(priceTick.getInstrument());
    PriceTickMdb priceTickMdb = new PriceTickMdb(source, priceTick.getInstrument(),
        priceTick.getTime(), priceTick.getAsk(), priceTick.getBid());
    Document document = Document.parse(JsonUtils.toJson(priceTickMdb));
    MongoCollection<Document> collection = getCollection(priceTick.getInstrument());
    try {
      collection.insertOne(document);
    } catch (MongoClientException e) {
      // Handle drivers failures.
      try {
        collection.insertOne(document);
      } catch (DuplicateKeyException ex) {
        // Record already inserted, ignore exception.
      }
    }
  }

  @Override
  public Iterable<PriceTick> find(
      String instrumentId, long fromTimestampUtc, long toTimeStampUtc) {
    return internalFind(instrumentId, fromTimestampUtc, toTimeStampUtc)
        .map(this::createPriceTick);
  }

  private FindIterable<Document> internalFind(
      String instrumentId, long fromTimestampUtc, long toTimeStampUtc) {
    checkNotNullOrEmpty(instrumentId);
    checkArgument(fromTimestampUtc > 0, "From timestamp must be greater than 0.");
    checkArgument(toTimeStampUtc > 0, "To timestamp must be greater than 0.");
    checkArgument(toTimeStampUtc > fromTimestampUtc,
        "To timestamp must be greater than from timestamp.");
    Bson query = and(gte(PriceTickMdb.FIELD_TIME, fromTimestampUtc),
        lte(PriceTickMdb.FIELD_TIME, toTimeStampUtc));
    Bson sort = Sorts.ascending(PriceTickMdb.FIELD_TIME);
    return getCollection(instrumentId).find(query)
        .sort(sort);
  }

  private PriceTick createPriceTick(Document document) {
    return JsonUtils.fromJson(JSON.serialize(document), PriceTick.class);
  }

  private MongoCollection<Document> getCollection(String instrumentId) {
    instrumentId = instrumentId.toUpperCase();
    MongoCollection<Document> collection = collectionsByInstrumentId.get(instrumentId);
    if (collection == null) {
      String collectionName = getCollectionName(
          PriceTickMdb.COLLECTION_PRICE_TICKS_PREFIX, instrumentId);
      collection = mongoDatabase.getCollection(collectionName);
      createIndexIfNotExists(collection);
      collectionsByInstrumentId.put(instrumentId, collection);
    }
    return collection;
  }

  private void createIndexIfNotExists(MongoCollection<Document> collection) {
    Iterable<Document> indexes = collection.listIndexes();
    for (Document index: indexes) {
      if (index.keySet().containsAll(INDEX.keySet()) &&
          INDEX.keySet().containsAll(index.keySet())) {
        return;
      }
    }
    collection.createIndex(INDEX, new IndexOptions().unique(true));
  }

  /**
   * Returns the collection name used by persistence layer or message queues to store and
   * dispatch prices.
   */
  private String getCollectionName(String collectionPrefix, String instrumentId) {
    return checkNotNullOrEmpty(collectionPrefix) +
        checkNotNullOrEmpty(instrumentId).toUpperCase().replaceAll("[^A-Za-z0-9]", "_");
  }
}
