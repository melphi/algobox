package io.algobox.connector.oanada.domain;

import java.io.Serializable;

public final class OandaTransaction implements Serializable {
  private String id;

  private String time;

  private Integer userID;

  private String accountID;

  private String batchID;

  public String getId() {
    return id;
  }

  public String getTime() {
    return time;
  }

  public Integer getUserID() {
    return userID;
  }

  public String getAccountID() {
    return accountID;
  }

  public String getBatchID() {
    return batchID;
  }
}
