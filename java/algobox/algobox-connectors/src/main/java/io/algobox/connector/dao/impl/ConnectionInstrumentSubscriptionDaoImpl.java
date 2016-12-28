package io.algobox.connector.dao.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import io.algobox.common.dao.AbstractMongoDao;
import io.algobox.connector.dao.ConnectionInstrumentSubscriptionDao;
import io.algobox.connector.domain.mdb.ConnectorInstrumentSubscriptionMdb;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;

public final class ConnectionInstrumentSubscriptionDaoImpl
    extends AbstractMongoDao<ConnectorInstrumentSubscriptionMdb>
    implements ConnectionInstrumentSubscriptionDao {
  private static final Document INDEX = new Document()
      .append(ConnectorInstrumentSubscriptionMdb.FIELD_CONNECTION_ID, 1)
      .append(ConnectorInstrumentSubscriptionMdb.FIELD_INSTRUMENT_ID, 1);
  private static final IndexOptions INDEX_OPTIONS = new IndexOptions().unique(true);

  public ConnectionInstrumentSubscriptionDaoImpl(MongoDatabase database) {
    super(database,
        ConnectorInstrumentSubscriptionMdb.COLLECTION_CONNECTION_INSTRUMENT_SUBSCRIPTIONS,
        ConnectorInstrumentSubscriptionMdb.class);
    createIndexIfNotExists(INDEX, INDEX_OPTIONS);
  }

  @Override
  public void subscribeInstrument(String connectionId, String instrumentId) {
    checkNotNullOrEmpty(connectionId);
    checkNotNullOrEmpty(instrumentId);
    Bson query = and(
        eq(ConnectorInstrumentSubscriptionMdb.FIELD_CONNECTION_ID, connectionId),
        eq(ConnectorInstrumentSubscriptionMdb.FIELD_INSTRUMENT_ID, instrumentId));
    Document update = new Document(OPERATOR_SET,
        new Document(ConnectorInstrumentSubscriptionMdb.FIELD_INSTRUMENT_ID, instrumentId));
    collection.updateOne(query, update, UPDATE_OPTION_UPSERT);
  }

  @Override
  public void unSubscribeInstrument(String connectionId, String instrumentId) {
    checkNotNullOrEmpty(connectionId);
    checkNotNullOrEmpty(instrumentId);
    Bson query = and(
        eq(ConnectorInstrumentSubscriptionMdb.FIELD_CONNECTION_ID, connectionId),
        eq(ConnectorInstrumentSubscriptionMdb.FIELD_INSTRUMENT_ID, instrumentId));
    collection.deleteMany(query);
  }

  @Override
  public Map<String, Set<String>> findAllSubscriptionsByConnection() {
    Map<String, Set<String>> values = Maps.newHashMap();
    collection.find()
        .forEach(new Consumer<Document>() {
          @Override
          public void accept(Document document) {
            String connectorId =
                document.getString(ConnectorInstrumentSubscriptionMdb.FIELD_CONNECTION_ID);
            String instrumentId =
                document.getString(ConnectorInstrumentSubscriptionMdb.FIELD_INSTRUMENT_ID);
            Set<String> instruments = values.get(connectorId);
            if (instruments == null) {
              instruments = Sets.newHashSet();
              values.put(connectorId, instruments);
            }
            instruments.add(instrumentId);
          }
        });
    return values;
  }
}

