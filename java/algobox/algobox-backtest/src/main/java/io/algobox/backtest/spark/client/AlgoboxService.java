package io.algobox.backtest.spark.client;

import io.algobox.indicator.IndicatorService;
import io.algobox.instrument.InstrumentService;
import io.algobox.price.PriceService;

public interface AlgoboxService {
  PriceService getPriceService();

  IndicatorService getIndicatorService();

  InstrumentService getInstrumentService();
}
