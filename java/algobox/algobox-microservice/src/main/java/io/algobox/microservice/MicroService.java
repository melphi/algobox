package io.algobox.microservice;

import java.util.concurrent.Future;

public interface MicroService {
  /**
   * Start blocking version.
   */
  void start();

  /**
   * Stop blocking version.
   */
  void stop();

  /**
   * Start non-blocking version.
   */
  void startAsync();

  /**
   * Stop non-blocking version. Returns a future which completes when service has stopped.
   */
  Future stopAsync();
}
