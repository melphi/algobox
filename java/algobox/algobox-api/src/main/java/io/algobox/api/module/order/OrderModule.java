package io.algobox.api.module.order;

import io.algobox.api.module.order.service.impl.OrderServiceImpl;
import io.algobox.order.OrderService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

public class OrderModule extends AbstractBinder {
  @Override
  protected void configure() {
    bind(OrderServiceImpl.class).to(OrderService.class).in(Singleton.class);;
  }
}
