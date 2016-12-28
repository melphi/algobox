package io.algobox.connector.oanada.domain;

import java.io.Serializable;

public final class OandaAccount implements Serializable {
  private String id;

  private String currency;

  private Double balance;

  public String getId() {
    return id;
  }

  public String getCurrency() {
    return currency;
  }

  public Double getBalance() {
    return balance;
  }
}
