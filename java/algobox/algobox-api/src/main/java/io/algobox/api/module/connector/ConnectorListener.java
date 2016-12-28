package io.algobox.api.module.connector;

import io.algobox.api.module.strategy.service.StrategyService;
import io.algobox.connector.service.ConnectorService;
import io.algobox.order.OrderService;
import io.algobox.price.PriceTick;
import io.algobox.price.SourcedPriceTickListener;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;

@Service
public final class ConnectorListener implements SourcedPriceTickListener {
  private final OrderService orderService;
  private final StrategyService strategyService;

  @Inject
  public ConnectorListener(ConnectorService connectorService, OrderService orderService,
      StrategyService strategyService) {
    this.orderService = orderService;
    this.strategyService = strategyService;
    connectorService.setPriceTickListener(this);
  }

  @Override
  public void onPriceTick(String source, PriceTick priceTick) {
    new Thread(() -> orderService.onPriceTick(source, priceTick));
    strategyService.onPriceTick(source, priceTick);
  }
}
