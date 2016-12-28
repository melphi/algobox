package io.algobox.connector.dao;

import io.algobox.connector.domain.ConnectionRegistration;
import io.algobox.connector.domain.dto.ConnectionRegistrationRequestDto;

import java.util.Collection;

public interface ConnectionRegistrationDao {
  void setKeepAlive(String connectionId, boolean keepAlive);

  Collection<ConnectionRegistration> findAll();

  void deleteById(String connectionId);

  void save(ConnectionRegistrationRequestDto connectionRegistrationRequest);
}
