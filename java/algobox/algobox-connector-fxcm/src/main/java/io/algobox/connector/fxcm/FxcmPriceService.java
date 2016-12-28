package io.algobox.connector.fxcm;

import com.fxcm.fix.Instrument;
import com.fxcm.fix.SubscriptionRequestTypeFactory;
import com.fxcm.fix.pretrade.MarketDataRequest;
import com.fxcm.messaging.ITransportable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import io.algobox.connector.ConnectorException;
import io.algobox.connector.ConnectorPriceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkArgument;
import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;

public final class FxcmPriceService implements ConnectorPriceService {
  private static final Logger LOGGER = LoggerFactory.getLogger(FxcmPriceService.class);

  private final FxcmSessionHelper fxcmSessionHelper;
  private final Set<String> subscribedInstruments = Sets.newConcurrentHashSet();

  public FxcmPriceService(FxcmSessionHelper fxcmSessionHelper) {
    this.fxcmSessionHelper = fxcmSessionHelper;
  }

  @Override
  public boolean isInstrumentSubscribed(String instrumentId) {
    return subscribedInstruments.contains(instrumentId);
  }

  @Override
  public void subscribeInstrument(String instrumentId) throws ConnectorException {
    checkNotNullOrEmpty(instrumentId);
    checkArgument(fxcmSessionHelper.isConnected(), "Service not connected.");
    MarketDataRequest marketDataRequest = new MarketDataRequest();
    marketDataRequest.addRelatedSymbol(fxcmSessionHelper.getSymbol(instrumentId));
    marketDataRequest.setSubscriptionRequestType(SubscriptionRequestTypeFactory.SUBSCRIBE);
    marketDataRequest.setMDEntryTypeSet(MarketDataRequest.MDENTRYTYPESET_TICKALL);
    try {
      Consumer<ITransportable> callback = message -> subscribedInstruments.add(instrumentId);
      fxcmSessionHelper.sendMessage(marketDataRequest, callback);
    } catch (Exception e) {
      throw new ConnectorException(String.format(
          "Error while subscribing instrument [%s]: [%s]", instrumentId, e.getMessage()), e);
    }
  }

  @Override
  public void unSubscribeInstrument(String instrumentId) throws ConnectorException {
    checkNotNullOrEmpty(instrumentId);
    checkArgument(fxcmSessionHelper.isConnected(), "Service not connected.");
    MarketDataRequest marketDataRequest = new MarketDataRequest();
    marketDataRequest.addRelatedSymbol(new Instrument(instrumentId));
    marketDataRequest.setSubscriptionRequestType(SubscriptionRequestTypeFactory.UNSUBSCRIBE);
    try {
      Consumer<ITransportable> callback = message -> subscribedInstruments.remove(instrumentId);
      fxcmSessionHelper.sendMessage(marketDataRequest, callback);
    } catch (Exception e) {
      throw new ConnectorException(String.format(
          "Error while un-subscribing instrument [%s]: [%s]", instrumentId, e.getMessage()), e);
    }
  }

  @Override
  public Collection<String> getSubscribedInstruments() {
    return ImmutableSet.copyOf(subscribedInstruments);
  }

  void onConnectorDisconnected() {
    LOGGER.info("Service disconnection, clearing subscribed instrument.");
    subscribedInstruments.clear();
  }
}
