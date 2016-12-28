package io.algobox.instrument;

import java.io.Serializable;

public final class MarketHours implements Serializable {
  private Long marketOpen;

  private Long marketClose;

  private Long orb5minOpen;

  private Long orb5minClose;

  private Long previousMarketOpen;

  private Long previousMarketClose;

  public MarketHours() {
    // Intentionally empty.
  }

  public MarketHours(Long marketOpen, Long marketClose, Long orb5minOpen, Long orb5minClose,
      Long previousMarketOpen, Long previousMarketClose) {
    this.marketOpen = marketOpen;
    this.marketClose = marketClose;
    this.orb5minOpen = orb5minOpen;
    this.orb5minClose = orb5minClose;
    this.previousMarketOpen= previousMarketOpen;
    this.previousMarketClose = previousMarketClose;
  }

  public Long getMarketOpen() {
    return marketOpen;
  }

  public Long getMarketClose() {
    return marketClose;
  }

  public Long getOrb5minOpen() {
    return orb5minOpen;
  }

  public Long getOrb5minClose() {
    return orb5minClose;
  }

  public Long getPreviousMarketOpen() {
    return previousMarketOpen;
  }

  public Long getPreviousMarketClose() {
    return previousMarketClose;
  }
}
