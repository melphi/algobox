package io.algobox.connector.oanada.domain;

import java.util.Collection;

public final class OandaTradesResponse {
  private Collection<OandaTrade> trades;

  public Collection<OandaTrade> getTrades() {
    return trades;
  }
}
