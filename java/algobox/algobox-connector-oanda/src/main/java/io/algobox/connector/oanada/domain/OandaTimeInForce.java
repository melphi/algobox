package io.algobox.connector.oanada.domain;

public enum OandaTimeInForce {
  /**
   * The Order is "Good unTil Cancelled".
   */
  GTC("GTC"),

  /**
   *  The Order is "Good unTil Date" and will be cancelled at the provided time.
   */
  GTD("GTD"),

  /**
   * The Order is "Good For Day" and will be cancelled at 5pm New York time.
   */
  GFD("GFD"),


  /**
   * The Order must be immediately "Filled Or Killed"
   */
  FOK("FOK"),

  /**
   * The Order must be "Immediatedly paritally filled Or Cancelled"
   */
  IOC("IOC");

  private final String value;

  OandaTimeInForce(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return value;
  }
}
