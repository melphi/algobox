package io.algobox.connector.oanada.domain;

import java.io.Serializable;

public final class OandaOrder implements Serializable {
  private String id;

  private String createTime;

  private OandaOrderState state;

  private String instrument;

  private String units;

  private OandaOrderType type;

  private String filledTime;

  private String cancelledTime;

  private String priceBound;

  private OandaTakeProfitDetails takeProfitOnFill;

  private OandaStopLossDetails stopLossOnFill;

  public String getId() {
    return id;
  }

  public String getCreateTime() {
    return createTime;
  }

  public OandaOrderState getState() {
    return state;
  }

  public String getInstrument() {
    return instrument;
  }

  public String getUnits() {
    return units;
  }

  public OandaOrderType getType() {
    return type;
  }

  public String getFilledTime() {
    return filledTime;
  }

  public String getCancelledTime() {
    return cancelledTime;
  }

  public String getPriceBound() {
    return priceBound;
  }

  public OandaTakeProfitDetails getTakeProfitOnFill() {
    return takeProfitOnFill;
  }

  public OandaStopLossDetails getStopLossOnFill() {
    return stopLossOnFill;
  }
}
