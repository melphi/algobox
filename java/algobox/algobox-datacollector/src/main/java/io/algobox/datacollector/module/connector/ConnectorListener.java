package io.algobox.datacollector.module.connector;

import io.algobox.connector.service.ConnectorService;
import io.algobox.datacollector.module.datacollector.service.DataCollectorService;
import io.algobox.price.PriceTick;
import io.algobox.price.SourcedPriceTickListener;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;

@Service
public final class ConnectorListener implements SourcedPriceTickListener {
  private DataCollectorService dataCollectorService;

  @Inject
  public ConnectorListener(
      ConnectorService connectorService, DataCollectorService dataCollectorService) {
    this.dataCollectorService = dataCollectorService;
    connectorService.setPriceTickListener(this);
  }

  @Override
  public void onPriceTick(String source, PriceTick priceTick) {
    dataCollectorService.onPriceTick(source, priceTick);
  }
}
