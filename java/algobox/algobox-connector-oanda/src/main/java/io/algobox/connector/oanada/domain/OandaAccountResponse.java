package io.algobox.connector.oanada.domain;

import java.io.Serializable;

public final class OandaAccountResponse implements Serializable {
  private OandaAccount account;

  public OandaAccount getAccount() {
    return account;
  }
}
