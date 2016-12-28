package io.algobox.connector.oanada.domain;

import java.io.Serializable;

public final class OandaOrderRequestDetails implements Serializable {
  /**
   *  The type of the Order to Create. Must be set to “MARKET” when creating a Market Order.
   */
  private OandaOrderType type;

  /**
   * The Market Order’s Instrument.
   */
  private String instrument;

  /**
   * The quantity requested to be filled by the Market Order. A posititive number of units
   * results in a long Order, and a negative number of units results in a short Order.
   */
  private String units;

  /**
   * The time-in-force requested for the Market Order. Restricted to FOK or IOC for a MarketOrder.
   */
  private OandaTimeInForce timeInForce;

  /**
   * The worst price that the client is willing to have the Market Order filled at.
   */
  private String priceBound;

  /**
   * Specification of how Positions in the Account are modified when the Order is filled.
   */
  private OandaOrderPositionFill positionFill;

  /**
   * TakeProfitDetails specifies the details of a Take Profit Order to be created on behalf of a
   * client. This may happen when an Order is filled that opens a Trade requiring a Take Profit,
   * or when a Trade’s dependent Take Profit Order is modified directly through the Trade.
   */
  private OandaTakeProfitDetails takeProfitOnFill;

  /**
   * StopLossDetails specifies the details of a Stop Loss Order to be created on behalf of a client.
   * This may happen when an Order is filled that opens a Trade requiring a Stop Loss, or when a
   * Trade’s dependent Stop Loss Order is modified directly through the Trade.
   */
  private OandaStopLossDetails stopLossOnFill;

  public OandaOrderRequestDetails(OandaOrderType type, String instrument, String units,
      OandaTimeInForce timeInForce, String priceBound,
      OandaOrderPositionFill positionFill, OandaTakeProfitDetails takeProfitDetails,
      OandaStopLossDetails stopLossDetails) {
    this.type = type;
    this.instrument = instrument;
    this.units = units;
    this.timeInForce = timeInForce;
    this.priceBound = priceBound;
    this.positionFill = positionFill;
    this.takeProfitOnFill = takeProfitDetails;
    this.stopLossOnFill = stopLossDetails;
  }

  public OandaOrderType getType() {
    return type;
  }

  public String getInstrument() {
    return instrument;
  }

  public String getUnits() {
    return units;
  }

  public OandaTimeInForce getTimeInForce() {
    return timeInForce;
  }

  public String getPriceBound() {
    return priceBound;
  }

  public OandaOrderPositionFill getPositionFill() {
    return positionFill;
  }

  public OandaTakeProfitDetails getTakeProfitOnFill() {
    return takeProfitOnFill;
  }

  public OandaStopLossDetails getStopLossOnFill() {
    return stopLossOnFill;
  }
}
