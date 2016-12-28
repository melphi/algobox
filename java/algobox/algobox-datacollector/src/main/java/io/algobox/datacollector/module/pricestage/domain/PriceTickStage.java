package io.algobox.datacollector.module.pricestage.domain;

import java.io.Serializable;

public interface PriceTickStage extends Serializable {
  String getInstrument();

  long getTime();

  double getAsk();

  double getBid();

  String getSrc();
}
