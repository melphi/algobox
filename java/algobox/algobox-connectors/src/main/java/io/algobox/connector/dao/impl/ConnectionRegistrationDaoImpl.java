package io.algobox.connector.dao.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.result.UpdateResult;
import io.algobox.common.dao.AbstractMongoDao;
import io.algobox.connector.dao.ConnectionRegistrationDao;
import io.algobox.connector.domain.ConnectionRegistration;
import io.algobox.connector.domain.dto.ConnectionRegistrationRequestDto;
import io.algobox.connector.domain.mdb.ConnectionRegistrationMdb;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Collection;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.mongodb.client.model.Filters.eq;
import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;

public final class ConnectionRegistrationDaoImpl extends AbstractMongoDao<ConnectionRegistrationMdb>
    implements ConnectionRegistrationDao {
  private static final Document INDEX = new Document()
      .append(ConnectionRegistrationMdb.FIELD_CONNECTION_ID, 1);
  private static final IndexOptions INDEX_OPTIONS = new IndexOptions().unique(true);

  public ConnectionRegistrationDaoImpl(MongoDatabase database) {
    super(database, ConnectionRegistrationMdb.COLLECTION_CONNECTION_REGISTRATIONS,
        ConnectionRegistrationMdb.class);
    createIndexIfNotExists(INDEX, INDEX_OPTIONS);
  }

  @Override
  public void setKeepAlive(String connectionId, boolean keepAlive) {
    checkNotNullOrEmpty(connectionId);
    Bson query = eq(ConnectionRegistrationMdb.FIELD_CONNECTION_ID, connectionId);
    Document update = new Document(
        OPERATOR_SET, new Document(ConnectionRegistrationMdb.FIELD_KEEP_ALIVE, keepAlive));
    UpdateResult updateResult = collection.updateOne(query, update);
    checkArgument(updateResult.getMatchedCount() > 0,
        String.format("Record [%s] not found.", connectionId));
  }

  @Override
  public Collection<ConnectionRegistration> findAll() {
    return createEntities(collection.find());
  }

  @Override
  public void deleteById(String connectionId) {
    checkNotNullOrEmpty(connectionId);
    Bson query = eq(ConnectionRegistrationMdb.FIELD_CONNECTION_ID, connectionId);
    collection.deleteMany(query);
  }

  @Override
  public void save(
      ConnectionRegistrationRequestDto connectionRegistrationRequest) {
    checkNotNullOrEmpty(connectionRegistrationRequest.getConnectionId());
    checkNotNullOrEmpty(connectionRegistrationRequest.getConnectorId());
    checkNotNull(connectionRegistrationRequest.getParameters());
    collection.insertOne(createDocument(connectionRegistrationRequest));
  }

  private Document createDocument(ConnectionRegistrationRequestDto request) {
    Document parameters = new Document();
    for (Map.Entry<String, String> parameter: request.getParameters().entrySet()) {
      parameters.put(parameter.getKey(), parameter.getValue());
    }
    Document document = new Document();
    document.put(ConnectionRegistrationMdb.FIELD_CONNECTION_ID, request.getConnectionId());
    document.put(ConnectionRegistrationMdb.FIELD_CONNECTOR_ID, request.getConnectorId());
    document.put(ConnectionRegistrationMdb.FIELD_PARAMETERS, parameters);
    document.put(ConnectionRegistrationMdb.FIELD_KEEP_ALIVE, request.getKeepAlive());
    return document;
  }

  private Collection<ConnectionRegistration> createEntities(Iterable<Document> documents) {
    ImmutableList.Builder<ConnectionRegistration> result = ImmutableList.builder();
    for (Document document: documents) {
      Document parameters = (Document) document.get(ConnectionRegistrationMdb.FIELD_PARAMETERS);
      ImmutableMap.Builder<String, String> parametersMap = ImmutableMap.builder();
      for (Map.Entry<String, Object> parameter: parameters.entrySet()) {
        parametersMap.put(parameter.getKey(), (String) parameter.getValue());
      }
      result.add(new ConnectionRegistrationMdb(
          document.getString(ConnectionRegistrationMdb.FIELD_CONNECTION_ID),
          document.getString(ConnectionRegistrationMdb.FIELD_CONNECTOR_ID),
          parametersMap.build(),
          document.getBoolean(ConnectionRegistrationMdb.FIELD_KEEP_ALIVE)));
    }
    return result.build();
  }
}
