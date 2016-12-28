package io.algobox.connector.oanada.domain;

public final class OandaTrade {
  private String id;

  private String instrument;

  private String price;

  private String openTime;

  private OandaTradeState state;

  private String initialUnits;

  private String currentUnits;

  private String realizedPL;

  private String unrealizedPL;

  private String closeTime;

  private OandaTakeProfitDetails takeProfitOrder;

  private OandaStopLossDetails stopLossOrder;

  public String getId() {
    return id;
  }

  public String getInstrument() {
    return instrument;
  }

  public String getPrice() {
    return price;
  }

  public String getOpenTime() {
    return openTime;
  }

  public String getInitialUnits() {
    return initialUnits;
  }

  public String getCurrentUnits() {
    return currentUnits;
  }

  public String getRealizedPL() {
    return realizedPL;
  }

  public String getUnrealizedPL() {
    return unrealizedPL;
  }

  public OandaTakeProfitDetails getTakeProfitOrder() {
    return takeProfitOrder;
  }

  public OandaStopLossDetails getStopLossOrder() {
    return stopLossOrder;
  }

  public String getCloseTime() {
    return closeTime;
  }

  public OandaTradeState getState() {
    return state;
  }
}
