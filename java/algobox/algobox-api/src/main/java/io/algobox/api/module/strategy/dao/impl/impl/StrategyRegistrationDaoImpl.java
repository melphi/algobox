package io.algobox.api.module.strategy.dao.impl.impl;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import io.algobox.api.module.strategy.dao.impl.StrategyRegistrationDao;
import io.algobox.api.module.strategy.domain.StrategyRegistration;
import io.algobox.api.module.strategy.domain.mdb.StrategyRegistrationMdb;
import io.algobox.common.dao.AbstractMongoDao;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.mongodb.client.model.Filters.eq;
import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;

@Service
// TODO: Use AbstractMongoDao method for serialisation and de-serialisation.
public final class StrategyRegistrationDaoImpl extends AbstractMongoDao<StrategyRegistrationMdb>
    implements StrategyRegistrationDao {
  private static final Document INDEX_INSTANCE_ID = new Document()
      .append(StrategyRegistrationMdb.FIELD_INSTANCE_ID, 1);
  private static final Document INDEX_TITLE = new Document()
      .append(StrategyRegistrationMdb.FIELD_TITLE, 1);
  private static final IndexOptions INDEX_OPTIONS_UNIQUE = new IndexOptions().unique(true);

  @Inject
  public StrategyRegistrationDaoImpl(MongoDatabase database) {
    super(database, StrategyRegistrationMdb.COLLECTION_STRATEGY_REGISTRATIONS,
        StrategyRegistrationMdb.class);
    createIndexIfNotExists(INDEX_INSTANCE_ID, INDEX_OPTIONS_UNIQUE);
    createIndexIfNotExists(INDEX_TITLE, INDEX_OPTIONS_UNIQUE);
  }

  @Override
  public void deleteById(String instanceId) {
    checkNotNullOrEmpty(instanceId);
    Bson query = eq(StrategyRegistrationMdb.FIELD_INSTANCE_ID, instanceId);
    deleteWithRetry(query);
  }

  @Override
  public void save(StrategyRegistration strategyRegistration) {
    checkNotNull(strategyRegistration);
    checkNotNullOrEmpty(strategyRegistration.getStrategyId());
    checkNotNullOrEmpty(strategyRegistration.getTitle());
    checkNotNull(strategyRegistration.getParameters());
    checkNotNull(strategyRegistration.getInstrumentsMapping());
    StrategyRegistrationMdb record = new StrategyRegistrationMdb(strategyRegistration);
    insertOneWithRetry(record);
  }

  @Override
  public boolean exists(String instanceId) {
    checkNotNullOrEmpty(instanceId);
    Bson query = eq(StrategyRegistrationMdb.FIELD_INSTANCE_ID, instanceId);
    return countWithRetry(query) > 0;
  }

  @Override
  public Iterable<? extends StrategyRegistration> findAll() {
    return findAllWithRetry();
  }

  @Override
  public StrategyRegistration findByInstanceId(String instanceId) {
    checkNotNullOrEmpty(instanceId);
    Bson query = eq(StrategyRegistrationMdb.FIELD_INSTANCE_ID, instanceId);
    return findFirstWithRetry(query);
  }

  @Override
  public Iterable<? extends StrategyRegistration> findByStrategyId(String strategyId) {
    checkNotNullOrEmpty(strategyId);
    Bson query = eq(StrategyRegistrationMdb.FIELD_STRATEGY_ID, strategyId);
    return findManyWithRetry(query);
  }
}
