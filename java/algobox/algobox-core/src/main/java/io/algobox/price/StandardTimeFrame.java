package io.algobox.price;

public enum StandardTimeFrame {
  M5("M5"),
  M15("M15"),
  D1("D1");

  private final String value;

  StandardTimeFrame(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
