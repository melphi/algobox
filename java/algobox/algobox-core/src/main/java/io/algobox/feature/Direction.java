package io.algobox.feature;

public enum Direction {
  UP("UP"),
  DOWN("DOWN");

  private final String value;

  Direction(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
