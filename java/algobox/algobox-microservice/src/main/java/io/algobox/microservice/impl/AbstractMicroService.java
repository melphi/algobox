package io.algobox.microservice.impl;

import io.algobox.microservice.MicroService;
import io.algobox.microservice.container.context.BootConfigurator;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public abstract class AbstractMicroService implements MicroService {
  protected final BootConfigurator configurator =
      BootConfigurator.getInstance();

  @Override
  public synchronized void start() {
    startAsync();
    final CompletableFuture<Object> shutdownFuture = new CompletableFuture<>();
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      stopAsync();
      shutdownFuture.complete(true);
    }));
    // Application main loop.
    try {
      shutdownFuture.get();
    } catch (Exception e) {
      throw new RuntimeException(String.format("Fatal error [%s].", e.getMessage()), e);
    }
  }

  @Override
  public synchronized void stop() {
    try {
      stopAsync().get();
    } catch (ExecutionException | InterruptedException e) {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Starts the service without blocking.
   */
  @Override
  public abstract void startAsync();

  /**
   * Stops the service without blocking. Returns a future which completes when the service is
   * stopped.
   */
  @Override
  public abstract Future<Object> stopAsync();
}
