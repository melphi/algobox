package io.algobox.instrument;

import java.io.Serializable;

public final class InstrumentInfoDetailed implements Serializable {
  private String instrumentId;

  private Integer openHour;

  private Integer openMinute;

  private Integer closeHour;

  private Integer closeMinute;

  private Boolean is24hMarket;

  private Integer orb5MinOpenHour;

  private String timeZoneId;

  private Integer pipsDecimals;

  public InstrumentInfoDetailed() {
    // Intentionally empty.
  }

  public InstrumentInfoDetailed(String instrumentId, Integer openHour, Integer openMinute,
      Integer closeHour, Integer closeMinute, Boolean is24hMarket, Integer orb5MinOpenHour,
      Integer pipsDecimals, String timeZoneId) {
    this.instrumentId = instrumentId;
    this.openHour = openHour;
    this.openMinute = openMinute;
    this.closeHour = closeHour;
    this.closeMinute = closeMinute;
    this.is24hMarket = is24hMarket;
    this.orb5MinOpenHour = orb5MinOpenHour;
    this.pipsDecimals = pipsDecimals;
    this.timeZoneId = timeZoneId;
  }

  public String getInstrumentId() {
    return instrumentId;
  }

  public Integer getOpenHour() {
    return openHour;
  }

  public Integer getOpenMinute() {
    return openMinute;
  }

  public Integer getCloseHour() {
    return closeHour;
  }

  public Integer getCloseMinute() {
    return closeMinute;
  }

  public Boolean getIs24hMarket() {
    return is24hMarket;
  }

  public Integer getOrb5MinOpenHour() {
    return orb5MinOpenHour;
  }

  public Integer getPipsDecimals() {
    return pipsDecimals;
  }

  public String getTimeZoneId() {
    return timeZoneId;
  }
}
