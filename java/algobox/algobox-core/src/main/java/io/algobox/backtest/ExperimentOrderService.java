package io.algobox.backtest;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.algobox.connector.ConnectorException;
import io.algobox.instrument.InstrumentInfoDetailed;
import io.algobox.instrument.InstrumentService;
import io.algobox.order.CloseStrategy;
import io.algobox.order.Order;
import io.algobox.order.OrderDirection;
import io.algobox.order.OrderPreconditions;
import io.algobox.order.OrderRequest;
import io.algobox.order.OrderService;
import io.algobox.order.OrderState;
import io.algobox.order.OrderType;
import io.algobox.order.Trade;
import io.algobox.order.TradeState;
import io.algobox.price.PriceTick;
import io.algobox.price.util.PriceUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;

public final class ExperimentOrderService implements OrderService {
  public static final String DEFAULT_CONNECTION_ID = "connectionId1";

  private final long latencyMilliseconds;
  private final InstrumentService instrumentService;
  // TODO: Tune the initial size of the array lists.
  private final List<Order> activeOrders = Lists.newArrayListWithCapacity(100);
  private final List<Order> filledOrders = Lists.newArrayListWithCapacity(100);
  private final List<Order> cancelledOrders = Lists.newArrayListWithCapacity(100);
  private final List<Trade> activeTrades = Lists.newArrayListWithCapacity(500);
  private final List<Trade> closedTrades = Lists.newArrayListWithCapacity(500);
  private final Map<String, Integer> pipsDecimalsByInstrument = Maps.newHashMap();

  public ExperimentOrderService(InstrumentService instrumentService, long latencyMilliseconds) {
    checkArgument(latencyMilliseconds >= 0);
    this.instrumentService = instrumentService;
    this.latencyMilliseconds = latencyMilliseconds;
  }

  /**
   * To reduce complexity and errors while backtesting the sendOrderAsync is synchronous.
   */
  @Override
  public synchronized void sendOrderAsync(final OrderRequest orderRequest) {
    sendOrder(orderRequest);
  }

  @Override
  public synchronized void sendOrder(final OrderRequest orderRequest) {
    checkArgument(DEFAULT_CONNECTION_ID.equals(orderRequest.getConnectionId()),
        String.format("Expected connection id [%s].", DEFAULT_CONNECTION_ID));
    OrderPreconditions.checkOrderRequest(orderRequest);
    activeOrders.add(createPendingOrder(orderRequest));
  }

  @Override
  public synchronized Collection<Order> findOrders(String connectionId, OrderState orderState)
      throws ConnectorException {
    checkArgument(DEFAULT_CONNECTION_ID.equals(connectionId));
    checkNotNull(orderState);
    switch (orderState) {
      case PENDING:
        return ImmutableList.copyOf(activeOrders);
      case FILLED:
        return ImmutableList.copyOf(filledOrders);
      case CANCELLED:
        return ImmutableList.copyOf(cancelledOrders);
      default:
        throw new IllegalArgumentException(
            String.format("Unsupported order state [%s].", orderState));
    }
  }

  @Override
  public synchronized Collection<Trade> findTrades(String connectionId, TradeState tradeState)
      throws ConnectorException {
    checkArgument(DEFAULT_CONNECTION_ID.equals(connectionId));
    checkNotNull(tradeState);
    switch (tradeState) {
      case OPEN:
        return ImmutableList.copyOf(activeTrades);
      case CLOSED:
        return ImmutableList.copyOf(closedTrades);
      default:
        throw new IllegalArgumentException(
            String.format("Unsupported trade state [%s].", tradeState));
    }
  }

  // TODO: Do some microbenchmark, this method could be a bottleneck.
  @Override
  public synchronized void onPriceTick(String source, PriceTick priceTick) {
    List<Order> filledOrders = Lists.newArrayList();
    List<Order> cancelledOrders = Lists.newArrayList();
    for (Order order: activeOrders) {
      checkNotNull(order.getType());
      checkArgument(DEFAULT_CONNECTION_ID.equals(source), "Invalid source.");
      if (order.getInstrument().equals(priceTick.getInstrument())) {
        switch (order.getType()) {
          case MARKET:
            if (isPriceOutOfBound(order, priceTick)) {
              cancelledOrders.add(order);
            } else if (priceTick.getTime() >= order.getCreatedOn() + latencyMilliseconds) {
              filledOrders.add(order);
            }
            break;
          default:
            throw new IllegalArgumentException(
                String.format("Unsupported order type [%s].", order.getType()));
        }
      }
    }
    removeCancelledOrders(cancelledOrders);
    fillTradeOrders(filledOrders, source, priceTick);
    updateTradesStatus(priceTick);
  }

  public synchronized int getActiveTradesCount() {
    return activeTrades.size();
  }

  public synchronized double getActiveTradesPlPips() {
    return activeTrades.stream()
        .map(Trade::getProfitLossPips)
        .reduce(0.0, Double::sum);
  }

  public synchronized int getClosedTradesCount() {
    return closedTrades.size();
  }

  public synchronized double getClosedTradesPlPips() {
    return closedTrades.stream()
        .map(Trade::getProfitLossPips)
        .reduce(0.0, Double::sum);
  }

  public synchronized int getActiveOrdersCount() {
    return activeOrders.size();
  }

  public synchronized int getCancelledOrdersCount() {
    return cancelledOrders.size();
  }

  private void fillTradeOrders(Collection<Order> fillingOrders, String source, PriceTick priceTick) {
    for (Order order: fillingOrders) {
      openTrade(source, priceTick, order);
      activeOrders.remove(order);
      filledOrders.add(order);
    }
  }

  private void removeCancelledOrders(Collection<Order> cancellingOrders) {
    for (Order order: cancellingOrders) {
      cancelledOrders.add(order);
      activeOrders.remove(order);
    }
  }

  private void updateTradesStatus(PriceTick priceTick) {
    List<Trade> closingTrades = Lists.newArrayList();
    for (int i = 0; i < activeTrades.size(); i++) {
      Trade trade = activeTrades.get(i);
      if (priceTick.getInstrument().equals(trade.getInstrumentId()) &&
          DEFAULT_CONNECTION_ID.equals(trade.getConnectionId())) {
        if (isTimeToCloseTrade(trade, priceTick)) {
          trade = updateTradePlPips(trade, priceTick);
          closingTrades.add(trade);
        } else {
          trade = updateTradePlPips(trade, priceTick);
        }
        activeTrades.set(i, trade);
      }
    }
    for (Trade trade: closingTrades) {
      activeTrades.remove(trade);
      closedTrades.add(trade);
    }
  }

  private boolean isTimeToCloseTrade(Trade trade, PriceTick priceTick) {
    switch (trade.getDirection()) {
      case LONG:
        return priceTick.getBid() >= trade.getCloseStrategy().getTakeProfit()
            || priceTick.getBid() <= trade.getCloseStrategy().getStopLoss();
      case SHORT:
        return priceTick.getAsk() <= trade.getCloseStrategy().getTakeProfit()
            || priceTick.getAsk() >= trade.getCloseStrategy().getStopLoss();
      default:
        throw new IllegalArgumentException(
            String.format("Unsupported direction [%s].", trade.getDirection()));
    }
  }

  private void openTrade(String connectionId, PriceTick priceTick, Order order) {
    checkNotNullOrEmpty(connectionId);
    double price = OrderDirection.LONG.equals(order.getDirection())
        ? priceTick.getAsk() : priceTick.getBid();
    CloseStrategy closeStrategy = order.getCloseStrategy() != null
        ? new CloseStrategy(order.getCloseStrategy()) : null;
    Trade trade = new Trade(UUID.randomUUID().toString(), connectionId, priceTick.getInstrument(),
        priceTick.getTime(), null, price, TradeState.OPEN, order.getAmount(), order.getDirection(),
        0.0, null, closeStrategy);
    activeTrades.add(trade);
  }

  private boolean isPriceOutOfBound(Order order, PriceTick priceTick) {
    checkArgument(OrderType.MARKET.equals(order.getType()));
    switch (order.getDirection()) {
      case LONG:
        return priceTick.getAsk() > order.getWorstAcceptedPrice();
      case SHORT:
        return priceTick.getBid() < order.getWorstAcceptedPrice();
      default:
          throw new IllegalArgumentException(
              String.format("Unsupported direction [%s]", order.getDirection()));
    }
  }

  private Trade updateTradePlPips(Trade trade, PriceTick priceTick) {
    Integer pipsDecimals = pipsDecimalsByInstrument.get(priceTick.getInstrument());
    if (pipsDecimals == null) {
      InstrumentInfoDetailed instrumentInfo =
          instrumentService.getInstrumentInfo(priceTick.getInstrument());
      pipsDecimals = checkNotNull(instrumentInfo).getPipsDecimals();
      pipsDecimalsByInstrument.put(priceTick.getInstrument(), checkNotNull(pipsDecimals));
    }
    double plPips;
    switch (trade.getDirection()) {
      case LONG:
        plPips = PriceUtils.getPlPipsForLong(pipsDecimals, priceTick.getBid(), trade.getPrice());
        break;
      case SHORT:
        plPips = PriceUtils.getPlPipsForShort(pipsDecimals, priceTick.getAsk(), trade.getPrice());
        break;
      default:
        throw new IllegalArgumentException(String.format(
            "Unsupported direction [%s].", trade.getDirection()));
    }
    return new Trade(trade.getId(), trade.getConnectionId(), trade.getInstrumentId(),
        trade.getCreatedOn(), priceTick.getTime(), trade.getPrice(), trade.getState(),
        trade.getAmount(), trade.getDirection(), plPips, null, trade.getCloseStrategy());
  }

  private Order createPendingOrder(OrderRequest orderRequest) {
    return new Order(UUID.randomUUID().toString(), orderRequest.getPriceTick().getTime(),
        OrderState.PENDING, orderRequest.getAmount(), orderRequest.getOpenStrategy().getOrderType(),
        orderRequest.getInstrumentId(), null, orderRequest.getOpenStrategy().getOrderDirection(),
        orderRequest.getOpenStrategy().getWorstAcceptedPrice(), orderRequest.getCloseStrategy());
  }
}
