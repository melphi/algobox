package io.algobox.api.module.strategy.dao.impl.impl;

import com.google.common.collect.ImmutableList;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import io.algobox.api.module.strategy.dao.impl.StrategyEventDao;
import io.algobox.api.module.strategy.domain.mdb.StrategyEventMdb;
import io.algobox.common.dao.AbstractMongoDao;
import io.algobox.strategy.StrategyEvent;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.mongodb.client.model.Filters.eq;
import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;
import static io.algobox.util.MorePreconditions.checkPagination;

@Service
public class StrategyEventDaoImpl extends AbstractMongoDao<StrategyEventMdb>
    implements StrategyEventDao {
  private static final Document INDEX = new Document()
      .append(StrategyEventMdb.FIELD_STRATEGY_INSTANCE_ID, 1)
      .append(StrategyEventMdb.FIELD_TIMESTAMP, -1);

  @Inject
  public StrategyEventDaoImpl(MongoDatabase database) {
    super(database, StrategyEventMdb.COLLECTION_STRATEGY_EVENTS, StrategyEventMdb.class);
    createIndexIfNotExists(INDEX);
  }

  @Override
  public void logEvent(String instanceId, StrategyEvent strategyEvent) {
    checkNotNullOrEmpty(instanceId, "Empty instance id.");
    checkNotNull(strategyEvent, "Empty event.");
    insertOneWithRetry(new StrategyEventMdb(instanceId, strategyEvent));
  }

  @Override
  public Collection<StrategyEvent> findEventsLog(String instanceId, int pageNumber, int pageSize) {
    checkNotNullOrEmpty(instanceId);
    checkPagination(pageNumber, pageSize);
    Bson query = eq(StrategyEventMdb.FIELD_STRATEGY_INSTANCE_ID, instanceId);
    return ImmutableList.copyOf(findManyWithRetry(query, pageNumber, pageSize,
        Optional.of(Sorts.descending(StrategyEventMdb.FIELD_TIMESTAMP))));
  }
}
