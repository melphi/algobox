package io.algobox.backtest.spark.common.client.impl;

import io.algobox.backtest.spark.common.client.AlgoboxClient;
import io.algobox.indicator.IndicatorService;
import io.algobox.instrument.InstrumentService;
import io.algobox.price.PriceService;

import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;

public final class AlgoboxClientImpl implements AlgoboxClient {
  private final String apiUrl;

  public AlgoboxClientImpl(String apiUrl) {
    this.apiUrl = checkNotNullOrEmpty(apiUrl);
  }

  @Override
  public PriceService getPriceService() {
    throw new IllegalArgumentException("Not yet implemented");
  }

  @Override
  public IndicatorService getIndicatorService() {
    throw new IllegalArgumentException("Not yet implemented");
  }

  @Override
  public InstrumentService getInstrumentService() {
    throw new IllegalArgumentException("Not yet implemented");
  }
}
