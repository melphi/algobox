package io.algobox.api.module.notification;

import io.algobox.api.module.notification.service.NotificationClient;
import io.algobox.api.module.notification.service.impl.NotificationClientImpl;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

public class NotificationModule extends AbstractBinder {
  @Override
  protected void configure() {
    bind(NotificationClientImpl.class).to(NotificationClient.class).in(Singleton.class);;
  }
}
