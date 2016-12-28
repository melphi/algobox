package io.algobox.price;

import java.io.Serializable;

public interface Ohlc extends Serializable {
  String getInstrument();

  double getAskOpen();

  double getBidOpen();

  double getAskHigh();

  double getBidHigh();

  double getAskLow();

  double getBidLow();

  double getAskClose();

  double getBidClose();
}
