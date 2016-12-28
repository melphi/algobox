package io.algobox.connector;

public interface ConnectorMapper {
  String toConnectorInstrumentId(String platformInstrumentId);

  String fromConnectorInstrumentId(String connectorInstrumentId);
}
