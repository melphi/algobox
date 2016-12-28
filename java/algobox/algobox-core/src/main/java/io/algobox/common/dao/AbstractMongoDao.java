package io.algobox.common.dao;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoClientException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.util.JSON;
import io.algobox.util.JsonUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;
import static io.algobox.util.MorePreconditions.checkPagination;

public abstract class AbstractMongoDao<T> {
  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMongoDao.class);

  protected static final String OPERATOR_SET = "$set";
  protected static final UpdateOptions UPDATE_OPTION_UPSERT = new UpdateOptions().upsert(true);

  protected final String collectionName;
  protected final Class<T> persistentClass;
  protected final MongoCollection<Document> collection;

  public AbstractMongoDao(MongoDatabase database, String collectionName, Class<T> persistentClass) {
    this.collectionName = checkNotNullOrEmpty(collectionName);
    this.persistentClass = checkNotNull(persistentClass);
    this.collection = checkNotNull(database.getCollection(collectionName));
  }

  protected void createIndexIfNotExists(Document newIndex) {
    createIndexIfNotExists(newIndex, null);
  }

  protected void createIndexIfNotExists(Document newIndex, IndexOptions indexOptions) {
    checkNotNull(newIndex);
    Iterable<Document> indexes = collection.listIndexes();
    for (Document index: indexes) {
      if (index.keySet().containsAll(newIndex.keySet()) &&
          newIndex.keySet().containsAll(index.keySet())) {
        return;
      }
    }
    if (indexOptions != null) {
      collection.createIndex(newIndex, indexOptions);
    } else {
      collection.createIndex(newIndex);
    }
  }

  protected long countWithRetry(Bson query) {
    try {
      return collection.count(query);
    } catch (MongoClientException e) {
      LOGGER.warn(String.format("MongoDb exception [%s], retrying count().", e.getMessage()));
      return collection.count(query);
    }
  }

  protected void insertOneWithRetry(T object) {
    checkNotNull(object);
    Document document = Document.parse(JsonUtils.toJson(object));
    try {
      collection.insertOne(document);
    } catch (MongoClientException e) {
      LOGGER.warn(String.format("MongoDb exception [%s], retrying insertOne().", e.getMessage()));
      try {
        collection.insertOne(document);
      } catch (DuplicateKeyException ex) {
        // Record already inserted, ignore exception.
      }
    }
  }

  protected Iterable<T> findManyWithRetry(Bson query) {
    checkNotNull(query);
    FindIterable<Document> find = collection.find(query);
    try {
      return find.map(item -> JsonUtils.fromJson(JSON.serialize(item), persistentClass));
    } catch (MongoClientException e) {
      LOGGER.warn(String.format("MongoDb exception [%s], retrying findMany().", e.getMessage()));
      return find.map(item -> JsonUtils.fromJson(JSON.serialize(item), persistentClass));
    }
  }

  protected Iterable<T> findManyWithRetry(
      Bson query, int pageNumber, int pageSize, Optional<Bson> sort) {
    checkNotNull(query);
    checkPagination(pageNumber, pageSize);
    FindIterable<Document> find = collection.find(query)
        .skip(pageNumber * pageSize)
        .limit(pageSize);
    if (sort.isPresent()) {
      find.sort(sort.get());
    }
    try {
      return find.map(item -> JsonUtils.fromJson(JSON.serialize(item), persistentClass));
    } catch (MongoClientException e) {
      LOGGER.warn(String.format("MongoDb exception [%s], retrying findMany().", e.getMessage()));
      return find.map(item -> JsonUtils.fromJson(JSON.serialize(item), persistentClass));
    }
  }

  protected T findFirstWithRetry(Bson query) {
    checkNotNull(query);
    FindIterable<Document> find = collection.find(query).limit(1);
    Document result;
    try {
      result = find.first();
    } catch (MongoClientException e) {
      LOGGER.warn(String.format("MongoDb exception [%s], retrying findFirst().", e.getMessage()));
      result = find.first();
    }
    return result != null ? JsonUtils.fromJson(JSON.serialize(result), persistentClass) : null;
  }

  protected Iterable<T> findAllWithRetry() {
    try {
      return collection.find()
          .map(item -> JsonUtils.fromJson(JSON.serialize(item), persistentClass));
    } catch (MongoClientException e) {
      LOGGER.warn(String.format("MongoDb exception [%s], retrying findAll().", e.getMessage()));
      return collection.find()
          .map(item -> JsonUtils.fromJson(JSON.serialize(item), persistentClass));
    }
 }

  protected void deleteWithRetry(Bson query) {
    try {
      collection.deleteMany(query);
    } catch (MongoClientException e) {
      collection.deleteMany(query);
    }
  }
}
