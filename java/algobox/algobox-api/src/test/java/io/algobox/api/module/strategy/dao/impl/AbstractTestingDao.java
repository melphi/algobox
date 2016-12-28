package io.algobox.api.module.strategy.dao.impl;

import avro.shaded.com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Map;

import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;

public abstract class AbstractTestingDao<T> {
  protected Map<String, T> values = Maps.newConcurrentMap();

  protected void internalSaveValue(String id, T value) {
    if (internalGetValue(id) != null) {
      throw new IllegalArgumentException(String.format("Duplicated key [%s].", id));
    }
    values.put(id, value);
  }

  protected T internalGetValue(String id) {
    checkNotNullOrEmpty(id);
    return values.get(id);
  }

  protected boolean internalExists(String id) {
    checkNotNullOrEmpty(id);
    return values.containsKey(id);
  }

  protected Collection<T> internalGetAllValues() {
    return values.values();
  }

  protected void internalDelete(String id) {
    values.remove(id);
  }
}
