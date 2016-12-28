package io.algobox.connector.oanada;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import io.algobox.connector.ConnectorException;
import io.algobox.connector.ConnectorListener;
import io.algobox.connector.ConnectorPriceService;
import io.algobox.price.PriceTick;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class OandaPriceService implements ConnectorPriceService {
  private final ConnectorListener connectorListener;
  private final OandaConnectorHelper connectorHelper;
  private final Set<String> subscribedInstruments = Sets.newConcurrentHashSet();
  private final Lock priceStreamLock = new ReentrantLock();

  private PriceStreamReaderThread streamReaderThread;

  public OandaPriceService(
      OandaConnectorHelper connectorHelper, ConnectorListener connectorListener) {
    this.connectorListener = checkNotNull(connectorListener);
    this.connectorHelper = checkNotNull(connectorHelper);
  }

  @Override
  public boolean isInstrumentSubscribed(String connectorInstrumentId) {
    return subscribedInstruments.contains(connectorInstrumentId);
  }

  @Override
  public void subscribeInstrument(String connectorInstrumentId) throws ConnectorException {
    checkArgument(!isInstrumentSubscribed(connectorInstrumentId),
        String.format("Instrument [%s] already subscribed.", connectorInstrumentId));
    Set<String> newInstruments = ImmutableSet.<String>builder()
        .addAll(subscribedInstruments)
        .add(connectorInstrumentId)
        .build();
    try {
      replacePricesStream(newInstruments);
    } catch (Exception e) {
      throw new ConnectorException(String.format("Error while subscribing instrument [%s]: [%s]",
          connectorInstrumentId, e.getMessage()), e);
    }
    subscribedInstruments.add(connectorInstrumentId);
  }

  @Override
  public void unSubscribeInstrument(String connectorInstrumentId) throws ConnectorException {
    checkArgument(isInstrumentSubscribed(connectorInstrumentId),
        String.format("Instrument [%s] is not subscribed.", connectorInstrumentId));
    Set<String> newInstruments = subscribedInstruments.stream()
        .filter(item -> !item.equals(connectorInstrumentId))
        .collect(Collectors.toSet());
    try {
      replacePricesStream(newInstruments);
    } catch (Exception e) {
      throw new ConnectorException(String.format("Error while un-subscribing instrument [%s]: [%s]",
          connectorInstrumentId, e.getMessage()), e);
    }
    subscribedInstruments.remove(connectorInstrumentId);
  }

  @Override
  public Collection<String> getSubscribedInstruments() {
    return ImmutableSet.copyOf(subscribedInstruments);
  }

  /**
   * Replaces the price stream if the new prices stream is successfully created, otherwise keeps
   * the old price stream running.
   */
  private void replacePricesStream(Set<String> newInstruments) throws IOException {
    if (newInstruments.isEmpty()) {
      replacePriceStream(null);
    } else {
      CloseableHttpResponse response = connectorHelper.getPricesStream(newInstruments);
      replacePriceStream(response);
    }
  }

  private void replacePriceStream(CloseableHttpResponse response) throws IOException {
    priceStreamLock.lock();
    try {
      if (streamReaderThread != null) {
        streamReaderThread.kill();
        streamReaderThread = null;
      }
      if (response != null) {
        PriceStreamReaderThread thread = new PriceStreamReaderThread(response);
        thread.start();
        this.streamReaderThread = thread;
      }
    } finally {
      priceStreamLock.unlock();
    }
  }

  private final class PriceStreamReaderThread extends Thread {
    private final CloseableHttpResponse response;
    private final BufferedReader bufferedReader;

    private volatile boolean killed = false;

    public PriceStreamReaderThread(CloseableHttpResponse response) {
      this.response = response;
      try {
        this.bufferedReader = new BufferedReader(new InputStreamReader(
            response.getEntity().getContent()));
      } catch (IOException e) {
        throw new IllegalArgumentException(e);
      }
    }

    @Override
    public void run() {
      String line;
      try {
        while ((line = bufferedReader.readLine()) != null) {
          PriceTick priceTick = OandaConnectorHelper.parsePriceLine(line);
          if (priceTick != null && !killed) {
            connectorListener.onPriceTick(priceTick);
          }
        }
      } catch (IOException e) {
        connectorListener.onGenericError(e);
      }
    }

    public void kill() throws IOException {
      response.close();
      bufferedReader.close();
      killed = true;
    }
  }
}
