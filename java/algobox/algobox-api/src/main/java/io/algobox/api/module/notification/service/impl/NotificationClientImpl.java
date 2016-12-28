package io.algobox.api.module.notification.service.impl;

import io.algobox.api.module.notification.service.NotificationClient;
import org.jvnet.hk2.annotations.Service;

@Service
public final class NotificationClientImpl implements NotificationClient {
  @Override
  public void notifyMessage(String message) {
    // TODO: Send text via Plivo.
  }
}
