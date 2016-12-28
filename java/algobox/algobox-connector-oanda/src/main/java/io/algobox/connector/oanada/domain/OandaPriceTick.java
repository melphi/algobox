package io.algobox.connector.oanada.domain;

public final class OandaPriceTick {
  private String closeoutAsk;

  private String closeoutBid;

  private String instrument;

  private String time;

  public String getCloseoutAsk() {
    return closeoutAsk;
  }

  public void setCloseoutAsk(String closeoutAsk) {
    this.closeoutAsk = closeoutAsk;
  }

  public String getCloseoutBid() {
    return closeoutBid;
  }

  public void setCloseoutBid(String closeoutBid) {
    this.closeoutBid = closeoutBid;
  }

  public String getInstrument() {
    return instrument;
  }

  public void setInstrument(String instrument) {
    this.instrument = instrument;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }
}
