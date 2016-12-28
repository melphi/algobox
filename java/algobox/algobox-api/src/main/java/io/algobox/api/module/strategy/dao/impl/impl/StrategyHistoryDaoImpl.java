package io.algobox.api.module.strategy.dao.impl.impl;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Sorts;
import io.algobox.api.module.strategy.dao.impl.StrategyHistoryDao;
import io.algobox.api.module.strategy.domain.StrategyHistory;
import io.algobox.api.module.strategy.domain.StrategyRegistration;
import io.algobox.api.module.strategy.domain.mdb.StrategyHistoryMdb;
import io.algobox.common.dao.AbstractMongoDao;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

@Service
public final class StrategyHistoryDaoImpl extends AbstractMongoDao<StrategyHistoryMdb>
    implements StrategyHistoryDao {
  private static final Document INDEX_INSTANCE_ID = new Document()
      .append(StrategyHistoryMdb.FIELD_INSTANCE_ID, 1);
  private static final Document INDEX_TIMESTAMP = new Document()
      .append(StrategyHistoryMdb.FIELD_TIMESTAMP, -1);

  @Inject
  public StrategyHistoryDaoImpl(MongoDatabase database) {
    super(database, StrategyHistoryMdb.COLLECTION_STRATEGY_HISTORIES, StrategyHistoryMdb.class);
    createIndexIfNotExists(INDEX_TIMESTAMP);
    createIndexIfNotExists(INDEX_INSTANCE_ID, new IndexOptions().unique(true));
  }

  @Override
  public void save(StrategyRegistration strategyRegistration, long timestampUtc,
      Optional<Throwable> exception, long receivedTicks) {
    checkArgument(timestampUtc > 0);
    StrategyHistoryMdb strategyHistory = new StrategyHistoryMdb(
        strategyRegistration, timestampUtc, exception.orElse(null), receivedTicks);
    insertOneWithRetry(strategyHistory);
  }

  @Override
  public Iterable<? extends StrategyHistory> findAll(int pageNumber, int pageSize) {
    Bson sort = Sorts.descending(StrategyHistoryMdb.FIELD_TIMESTAMP);
    return findManyWithRetry(new BsonDocument(), pageNumber, pageSize, Optional.of(sort));
  }
}
