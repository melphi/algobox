package io.algobox.price;

public interface Bar extends Ohlc {
  String getInstrument();

  String getTimeFrame();

  long getTime();
}
