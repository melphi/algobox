package io.algobox.backtest.spark.client.impl;

import io.algobox.backtest.spark.client.AlgoboxService;
import io.algobox.indicator.IndicatorService;
import io.algobox.instrument.InstrumentService;
import io.algobox.price.PriceService;
import io.algobox.testing.TestingIndicatorService;
import io.algobox.testing.TestingInstrumentService;

import java.io.Serializable;

public class TestingAlgoboxService implements AlgoboxService, Serializable {
  private final PriceService priceService = new TestingPriceService();
  private final IndicatorService indicatorService = new TestingIndicatorService();
  private final InstrumentService instrumentService = new TestingInstrumentService();

  @Override
  public PriceService getPriceService() {
    return priceService;
  }

  @Override
  public IndicatorService getIndicatorService() {
    return indicatorService;
  }

  @Override
  public InstrumentService getInstrumentService() {
    return instrumentService;
  }
}
