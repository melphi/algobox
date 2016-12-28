package io.algobox.connector.oanada.domain;

import java.io.Serializable;

public final class OandaTakeProfitDetails implements Serializable {
  /**
   * The price that the Take Profit Order will be triggered at.
   */
  private String price;

  /**
   * The time in force for the created Take Profit Order. This may only be GTC, GTD or GFD.
   */
  private OandaTimeInForce timeInForce;

  public OandaTakeProfitDetails(String price, OandaTimeInForce timeInForce) {
    this.price = price;
    this.timeInForce = timeInForce;
  }

  public String getPrice() {
    return price;
  }

  public OandaTimeInForce getTimeInForce() {
    return timeInForce;
  }
}
