package io.algobox.connector;

import io.algobox.instrument.InstrumentInfo;

import java.util.Collection;

public interface ConnectorInstrumentService {
  InstrumentInfo getInstrumentInfo(String instrumentId) throws ConnectorException;

  Collection<InstrumentInfo> findInstrumentsBySearchTerm(String searchTerm)
      throws ConnectorException;
}
