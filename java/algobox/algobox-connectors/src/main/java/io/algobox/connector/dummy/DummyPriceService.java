package io.algobox.connector.dummy;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import io.algobox.connector.ConnectorException;
import io.algobox.connector.ConnectorListener;
import io.algobox.connector.ConnectorPriceService;
import io.algobox.price.PriceTick;
import io.algobox.util.DateTimeUtils;

import java.util.Collection;
import java.util.Set;

public final class DummyPriceService implements ConnectorPriceService {
  public static final long DEFAULT_POLLING_MILLISECONDS = 1000L;

  private final Set<String> subscribedInstruments = Sets.newConcurrentHashSet();
  private final ConnectorListener connectorListener;
  private final long pollingMilliseconds;

  private Thread pricesGenerator;

  private volatile boolean started = false;

  public DummyPriceService(ConnectorListener connectorListener, long pollingMilliseconds) {
    this.connectorListener = connectorListener;
    this.pollingMilliseconds = pollingMilliseconds;
  }

  @Override
  public boolean isInstrumentSubscribed(String connectorInstrumentId) {
    return subscribedInstruments.contains(connectorInstrumentId);
  }

  @Override
  public void subscribeInstrument(String connectorInstrumentId) throws ConnectorException {
    subscribedInstruments.add(connectorInstrumentId);
  }

  @Override
  public void unSubscribeInstrument(String connectorInstrumentId) throws ConnectorException {
    subscribedInstruments.remove(connectorInstrumentId);
  }

  @Override
  public Collection<String> getSubscribedInstruments() {
    return ImmutableSet.copyOf(subscribedInstruments);
  }

  public synchronized void start() {
    started = true;
    if (pricesGenerator == null) {
      pricesGenerator = new GeneratePricesThread();
      pricesGenerator.start();
    }
  }

  public synchronized void stop() {
    started = false;
    subscribedInstruments.clear();
    pricesGenerator = null;
  }

  private final class GeneratePricesThread extends Thread {
    @Override
    public void run() {
      try {
        while(started) {
          Thread.sleep(pollingMilliseconds);
          for (String instrumentId: subscribedInstruments) {
            connectorListener.onPriceTick(createRandomPriceTick(instrumentId));
          }
        }
      } catch (InterruptedException e) {
        // Intentionally empty
      }
    }

    private PriceTick createRandomPriceTick(String instrumentId) {
      long timestamp = DateTimeUtils.getCurrentUtcTimestampMilliseconds();
      double ask = Math.random() * 2;
      double bid = ask - 0.0001;
      return new PriceTick(instrumentId, timestamp, ask, bid);
    }
  }
}
