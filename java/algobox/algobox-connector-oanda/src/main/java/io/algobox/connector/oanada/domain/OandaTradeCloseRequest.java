package io.algobox.connector.oanada.domain;

public final class OandaTradeCloseRequest {
  public static final String ALL = "ALL";

  private String units;

  public OandaTradeCloseRequest(String units) {
    this.units = units;
  }

  public String getUnits() {
    return units;
  }
}
