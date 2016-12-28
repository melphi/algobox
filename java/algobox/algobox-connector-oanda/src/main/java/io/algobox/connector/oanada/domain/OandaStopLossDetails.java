package io.algobox.connector.oanada.domain;

import java.io.Serializable;

public final class OandaStopLossDetails implements Serializable {
  /**
   * The price that the Stop Loss Order will be triggered at.
   */
  private String price;

  /**
   * The time in force for the created Stop Loss Order. This may only be GTC, GTD or GFD.
   */
  private OandaTimeInForce timeInForce;

  public OandaStopLossDetails(String price, OandaTimeInForce timeInForce) {
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
