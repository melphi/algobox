package io.algobox.price;

import java.io.Serializable;

public interface Tick extends Serializable {
  String getInstrument();

  long getTime();

  double getAsk();

  double getBid();
}
