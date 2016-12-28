package io.algobox.api.module.order.rest;

import io.algobox.connector.ConnectorException;
import io.algobox.order.OrderService;
import io.algobox.order.Trade;
import io.algobox.order.TradeState;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.util.Collection;

@Api(tags = "trades")
@Path(value = "/trades")
@Singleton
public final class TradesController {
  private final OrderService orderService;

  @Inject
  public TradesController(OrderService orderService) {
    this.orderService = orderService;
  }

  @ApiOperation(value = "Returns the list of trades.")
  @GET
  @Path("/{connectionId}")
  public Collection<Trade> findTrades(@PathParam("connectionId") String connectionId,
      @QueryParam("state") TradeState tradeState) throws ConnectorException {
    return orderService.findTrades(connectionId, tradeState);
  }
}
