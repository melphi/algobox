package io.algobox.connector.oanada;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import io.algobox.connector.ConnectorException;
import io.algobox.connector.ConnectorListener;
import io.algobox.connector.ConnectorOrderRequest;
import io.algobox.connector.ConnectorOrderService;
import io.algobox.connector.oanada.domain.OandaOrder;
import io.algobox.connector.oanada.domain.OandaOrderPositionFill;
import io.algobox.connector.oanada.domain.OandaOrderRequest;
import io.algobox.connector.oanada.domain.OandaOrderRequestDetails;
import io.algobox.connector.oanada.domain.OandaOrderResponse;
import io.algobox.connector.oanada.domain.OandaOrderState;
import io.algobox.connector.oanada.domain.OandaOrderType;
import io.algobox.connector.oanada.domain.OandaOrdersResponse;
import io.algobox.connector.oanada.domain.OandaStopLossDetails;
import io.algobox.connector.oanada.domain.OandaTakeProfitDetails;
import io.algobox.connector.oanada.domain.OandaTimeInForce;
import io.algobox.connector.oanada.domain.OandaTrade;
import io.algobox.connector.oanada.domain.OandaTradeCloseRequest;
import io.algobox.connector.oanada.domain.OandaTradeState;
import io.algobox.connector.oanada.domain.OandaTradesResponse;
import io.algobox.order.CloseStrategy;
import io.algobox.order.Order;
import io.algobox.order.OrderDirection;
import io.algobox.order.OrderResponse;
import io.algobox.order.OrderState;
import io.algobox.order.OrderType;
import io.algobox.order.Trade;
import io.algobox.order.TradeState;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static io.algobox.connector.oanada.OandaConnectorHelper.parseTimestamp;

public final class OandaOrderService implements ConnectorOrderService {
  private static final String PATH_OPEN_TRADES = "/v3/accounts/%s/openTrades";
  private static final String PATH_ORDERS = "/v3/accounts/%s/orders";
  private static final String PATH_ORDER_CANCEL = "/v3/accounts/%s/orders/%s/cancel";
  private static final String PATH_PENDING_ORDERS = "/v3/accounts/%s/pendingOrders";
  private static final String PATH_TRADES = "/v3/accounts/%s/trades";
  private static final String PATH_TRADE_CLOSE = "/v3/accounts/%s/trades/%s/close";
  private static final NumberFormat UNITS_FORMAT = createNumberFormat(0);

  private final OandaConnectorHelper connectorHelper;
  private final ConnectorListener connectorListener;

  public OandaOrderService(
      OandaConnectorHelper connectorHelper, ConnectorListener connectorListener) {
    this.connectorHelper = connectorHelper;
    this.connectorListener = checkNotNull(connectorListener);
  }

  @Override
  public void sendOrderAsync(final ConnectorOrderRequest orderRequest) throws ConnectorException {
    OandaOrderRequest oandaOrderRequest = createOandaOrderRequest(orderRequest);
    String path = String.format(PATH_ORDERS, connectorHelper.getAccountNumber());
    new Thread(() -> {
      try {
        OandaOrderResponse orderResponse =
            connectorHelper.doPost(path, OandaOrderResponse.class, oandaOrderRequest);
        if (orderResponse.getOrderCancelTransaction() != null) {
          Exception error = new Exception(orderResponse.getOrderCancelTransaction().getReason());
          connectorListener.onOrderError(orderRequest, error);
        } else {
          connectorListener.onOrderOpen(new OrderResponse(orderRequest.getOrderRequestId()));
        }
      } catch (ConnectorException e) {
        connectorListener.onOrderError(orderRequest, e);
      }
    }).start();
  }

  @Override
  public void closeAllOrdersAndPositions() throws ConnectorException {
    new Thread(() -> {
      try {
        closeAllPositions();
      } catch (ConnectorException e) {
        connectorListener.onGenericError(e);
      }
    }).start();
    closeAllOrders();
    // Close new positions which could have been opened in the meantime.
    closeAllPositions();
  }

  @Override
  public Collection<Order> findOrders(OrderState orderState) throws ConnectorException {
    String path;
    if (OrderState.PENDING.equals(orderState)) {
      path = String.format(PATH_PENDING_ORDERS, connectorHelper.getAccountNumber());
    } else {
      OandaOrderState oandaOrderState = checkNotNull(getOandaOrderState(orderState));
      path = String.format(PATH_ORDERS + "?state=%s",
          connectorHelper.getAccountNumber(), oandaOrderState.getValue());
    }
    OandaOrdersResponse oandaOrders = connectorHelper.doGet(path, OandaOrdersResponse.class);
    if (oandaOrders.getOrders() != null) {
      return oandaOrders.getOrders().stream()
          .map(this::createOrder)
          .collect(Collectors.toList());
    }
    return ImmutableList.of();
  }

  @Override
  public Collection<Trade> findTrades(TradeState tradeState) throws ConnectorException {
    String path;
    if (TradeState.OPEN.equals(tradeState)) {
      path = String.format(PATH_OPEN_TRADES, connectorHelper.getAccountNumber());
    } else {
      OandaTradeState oandaTradeState = checkNotNull(getOandaTradeState(tradeState));
      path = String.format(PATH_TRADES + "?state=%s",
          connectorHelper.getAccountNumber(), oandaTradeState.getValue());
    }
    OandaTradesResponse openTrades = connectorHelper.doGet(path, OandaTradesResponse.class);
    if (openTrades.getTrades() != null) {
      return openTrades.getTrades().stream()
          .map(this::createTrade)
          .collect(Collectors.toList());
    }
    return ImmutableSet.of();
  }

  @Override
  public void closeOpenOrder(String orderId) throws ConnectorException {
    String path = String.format(PATH_ORDER_CANCEL, connectorHelper.getAccountNumber(), orderId);
    connectorHelper.doPut(path);
  }

  @Override
  public void closeOpenTrade(String positionId) throws ConnectorException {
    String path = String.format(PATH_TRADE_CLOSE, connectorHelper.getAccountNumber(), positionId);
    connectorHelper.doPut(path, new OandaTradeCloseRequest(OandaTradeCloseRequest.ALL));
  }

  private Order createOrder(OandaOrder oandaOrder) {
    long createdOn = parseTimestamp(oandaOrder.getCreateTime());
    OrderState orderState = getOrderState(oandaOrder.getState());
    OrderDirection orderDirection = null;
    double amount = 0;
    if (!Strings.isNullOrEmpty(oandaOrder.getUnits())) {
      orderDirection = Double.parseDouble(oandaOrder.getUnits()) > 0.0
          ? OrderDirection.LONG : OrderDirection.SHORT;
      amount = Math.abs(Double.parseDouble(oandaOrder.getUnits()));
    }
    OrderType orderType = getOrderType(oandaOrder.getType());
    Long updatedOn = null;
    if (!Strings.isNullOrEmpty(oandaOrder.getFilledTime())) {
      updatedOn = parseTimestamp(oandaOrder.getFilledTime());
    } else if (!Strings.isNullOrEmpty(oandaOrder.getCancelledTime())) {
      updatedOn = parseTimestamp(oandaOrder.getCancelledTime());
    }
    Double worstAcceptedPrice = Strings.isNullOrEmpty(oandaOrder.getPriceBound())
        ? null : Double.parseDouble(oandaOrder.getPriceBound());
    CloseStrategy closeStrategy = getCloseStrategy(
        oandaOrder.getTakeProfitOnFill(), oandaOrder.getStopLossOnFill());
    return new Order(oandaOrder.getId(), createdOn, orderState, amount, orderType,
        oandaOrder.getInstrument(), updatedOn, orderDirection,
        worstAcceptedPrice, closeStrategy);
  }

  private Trade createTrade(OandaTrade oandaTrade) {
    long createdOn = parseTimestamp(oandaTrade.getOpenTime());
    Long updatedOn = Strings.isNullOrEmpty(oandaTrade.getCloseTime())
        ? null : parseTimestamp(oandaTrade.getCloseTime());
    TradeState tradeState = getTradeState(oandaTrade.getState());
    double initialAmount = Double.parseDouble(oandaTrade.getInitialUnits());
    OrderDirection orderDirection = initialAmount > 0 ? OrderDirection.LONG : OrderDirection.SHORT;
    double amount = Math.abs(Double.parseDouble(oandaTrade.getCurrentUnits()));
    double price = Double.parseDouble(oandaTrade.getPrice());
    double realisedPl = Double.parseDouble(oandaTrade.getRealizedPL());
    double unrealisedPl = Strings.isNullOrEmpty(oandaTrade.getUnrealizedPL()) ?
        0.0 : Double.parseDouble(oandaTrade.getUnrealizedPL());
    CloseStrategy closeStrategy = getCloseStrategy(
        oandaTrade.getTakeProfitOrder(), oandaTrade.getStopLossOrder());
    return new Trade(oandaTrade.getId(), null, oandaTrade.getInstrument(), createdOn, updatedOn,
        price, tradeState, amount, orderDirection, null, realisedPl + unrealisedPl, closeStrategy);
  }

  private CloseStrategy getCloseStrategy(
      OandaTakeProfitDetails takeProfitDetails, OandaStopLossDetails stopLossDetails) {
    Double takeProfit = takeProfitDetails != null && takeProfitDetails.getPrice() != null
        ? Double.parseDouble(takeProfitDetails.getPrice()) : null;
    Double stopLoss = stopLossDetails != null && stopLossDetails.getPrice() != null
        ? Double.parseDouble(stopLossDetails.getPrice()) : null;
    return (takeProfit != null || stopLoss != null)
        ? new CloseStrategy(takeProfit, stopLoss) : null;
  }

  private TradeState getTradeState(OandaTradeState state) {
    if (state == null) {
      return null;
    }
    switch (state) {
      case OPEN:
        return TradeState.OPEN;
      case CLOSED:
        return TradeState.CLOSED;
      case CLOSE_WHEN_TRADEABLE:
        return TradeState.OPEN;
      default:
        throw new IllegalArgumentException(String.format(
            "Unsupported Oanda trade state [%s].", state));
    }
  }

  private OandaTradeState getOandaTradeState(TradeState state) {
    if (state == null) {
      return null;
    }
    switch (state) {
      case OPEN:
        return OandaTradeState.OPEN;
      case CLOSED:
        return OandaTradeState.CLOSED;
      default:
        throw new IllegalArgumentException(String.format("Unsupported trade state [%s].", state));
    }
  }

  private OrderType getOrderType(OandaOrderType type) {
    if (type == null) {
      return null;
    }
    switch (type) {
      case MARKET:
        return OrderType.MARKET;
      case LIMIT:
        return OrderType.LIMIT;
      case STOP:
        return OrderType.STOP;
      case MARKET_IF_TOUCHED:
        return OrderType.MARKET;
      case TAKE_PROFIT:
        return OrderType.TAKE_PROFIT;
      case STOP_LOSS:
        return OrderType.STOP_LOSS;
      case TRAILING_STOP_LOSS:
        return OrderType.STOP_LOSS;
      default:
        throw new IllegalArgumentException(String.format(
            "Unsupported Oanda order type [%s].", type));
    }
  }

  private OrderState getOrderState(OandaOrderState state) {
    if (state == null) {
      return null;
    }
    switch (state) {
      case CANCELLED:
        return OrderState.CANCELLED;
      case FILLED:
        return OrderState.FILLED;
      case PENDING:
        return OrderState.PENDING;
      case TRIGGERED:
        return OrderState.PENDING;
      default:
        throw new IllegalArgumentException(String.format(
            "Unsupported Oanda order state [%s].", state));
    }
  }

  private OandaOrderState getOandaOrderState(OrderState state) {
    if (state == null) {
      return null;
    }
    switch (state) {
      case CANCELLED:
        return OandaOrderState.CANCELLED;
      case FILLED:
        return OandaOrderState.FILLED;
      case PENDING:
        return OandaOrderState.PENDING;
      default:
        throw new IllegalArgumentException(String.format("Unsupported order state [%s].", state));
    }
  }

  private void closeAllOrders() throws ConnectorException {
    for (Order order: findOrders(OrderState.PENDING)) {
      try {
        closeOpenOrder(order.getId());
      } catch (Exception e) {
        connectorListener.onGenericError(new Exception(String.format(
            "Unable to cancel the order [%s]: [%s].", order.getId(), e.getMessage()), e));
      }
    }
  }

  private void closeAllPositions() throws ConnectorException {
    for (Trade trade: findTrades(TradeState.OPEN)) {
      try {
        closeOpenTrade(trade.getId());
      } catch (Exception e) {
        connectorListener.onGenericError(new Exception(String.format(
            "Unable to cancel the position [%s]: [%s].", trade.getId(), e.getMessage()), e));
      }
    }
  }

  private static NumberFormat createNumberFormat(int minimumFractionDigits) {
    NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.ROOT);
    numberFormat.setGroupingUsed(false);
    numberFormat.setMaximumIntegerDigits(1);
    numberFormat.setMinimumFractionDigits(minimumFractionDigits);
    numberFormat.setRoundingMode(RoundingMode.UNNECESSARY);
    return numberFormat;
  }

  private OandaOrderRequest createOandaOrderRequest(ConnectorOrderRequest orderRequest) {
    checkNotNull(orderRequest);
    checkArgument(OrderType.MARKET.equals(orderRequest.getOpenStrategy().getOrderType()),
        String.format("Unsupported open dummy type [%s].",
            orderRequest.getOpenStrategy().getOrderType()));
    checkArgument(orderRequest.getCloseStrategy().getTakeProfit() > 0.0, "Missing take profit.");
    checkArgument(orderRequest.getCloseStrategy().getStopLoss() > 0.0, "Missing stop loss.");
    String units = getOrderUnits(orderRequest);
    NumberFormat priceFormat = createNumberFormat(4);
    String priceBound = orderRequest.getOpenStrategy().getWorstAcceptedPrice() > 0
        ? String.valueOf(orderRequest.getOpenStrategy().getWorstAcceptedPrice()) : null;
    String takeProfit = priceFormat.format(orderRequest.getCloseStrategy().getTakeProfit());
    String stopLoss = priceFormat.format(orderRequest.getCloseStrategy().getStopLoss());
    OandaTakeProfitDetails oandaTakeProfitDetails =
        new OandaTakeProfitDetails(takeProfit, OandaTimeInForce.GTC);
    OandaStopLossDetails oandaStopLossDetails =
        new OandaStopLossDetails(stopLoss, OandaTimeInForce.GTC);
    OandaOrderRequestDetails oandaOrderRequestDetails = new OandaOrderRequestDetails(
        OandaOrderType.MARKET, orderRequest.getInstrumentId(), units, OandaTimeInForce.FOK,
        priceBound, OandaOrderPositionFill.DEFAULT, oandaTakeProfitDetails, oandaStopLossDetails);
    return new OandaOrderRequest(oandaOrderRequestDetails);
  }

  private String getOrderUnits(ConnectorOrderRequest orderRequest) {
    Double amount = orderRequest.getAmount();
    switch (orderRequest.getOpenStrategy().getOrderDirection()) {
      case LONG:
        return UNITS_FORMAT.format(amount);
      case SHORT:
        return UNITS_FORMAT.format(-1.0 * amount);
      default:
        throw new IllegalArgumentException(String.format("Unsupported order direction [%s].",
            orderRequest.getOpenStrategy().getOrderDirection()));
    }
  }
}
