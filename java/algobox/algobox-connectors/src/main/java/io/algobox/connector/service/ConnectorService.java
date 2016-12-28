package io.algobox.connector.service;

import io.algobox.common.domain.StringValueDto;
import io.algobox.common.exception.ServiceException;
import io.algobox.connector.ConnectionInfo;
import io.algobox.connector.Connector;
import io.algobox.connector.ConnectorException;
import io.algobox.connector.domain.dto.ConnectionRegistrationDto;
import io.algobox.connector.domain.dto.ConnectionRegistrationRequestDto;
import io.algobox.instrument.InstrumentInfo;
import io.algobox.price.SourcedPriceTickListener;

import java.util.Collection;
import java.util.concurrent.Future;

public interface ConnectorService {
  Collection<StringValueDto> getSubscribedInstruments(String connectionId);

  ConnectionInfo getConnectionInfo(String connectionId) throws ServiceException;

  void disconnect(String connectionId) throws ServiceException, ConnectorException;

  Future<Boolean> connect(String connectionId);

  void subscribeInstrument(String connectionId, String instrumentId) throws ServiceException;

  void unSubscribeInstrument(String connectionId, String instrumentId);

  Collection<InstrumentInfo> searchInstruments(String connectionId, String searchTerm)
      throws ServiceException, ConnectorException;

  Connector getConnectionById(String connectionId);

  Collection<ConnectionRegistrationDto> findAllConnections();

  Collection<StringValueDto> findAllConnectors();

  void createConnection(ConnectionRegistrationRequestDto connectionRegistrationRequest)
      throws ConnectorException, ServiceException ;

  void removeConnection(String connectionId) throws ConnectorException, ServiceException;

  void setPriceTickListener(SourcedPriceTickListener sourcedPriceTickListener);
}
