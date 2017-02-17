package io.algobox.backtest.spark.common.client;

import io.algobox.indicator.IndicatorService;
import io.algobox.instrument.InstrumentService;
import io.algobox.price.PriceService;

import java.io.Serializable;

public interface AlgoboxClient extends Serializable {
  PriceService getPriceService();

  IndicatorService getIndicatorService();

  InstrumentService getInstrumentService();
}
