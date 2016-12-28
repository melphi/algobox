package io.algobox.api.module.order.rest;

import io.algobox.connector.ConnectorException;
import io.algobox.order.Order;
import io.algobox.order.OrderRequest;
import io.algobox.order.OrderService;
import io.algobox.order.OrderState;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.util.Collection;

@Api(tags = "orders")
@Path(value = "/orders")
@Singleton
public final class OrderController {
  private final OrderService orderService;

  @Inject
  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @ApiOperation(value = "Sends a new order.", notes = "No response is returned, the client needs " +
      "to check if order has been created and filled.")
  @POST
  public void crateOrder(OrderRequest orderRequest) {
    orderService.sendOrderAsync(orderRequest);
  }

  @ApiOperation(value = "Returns the list of orders.")
  @GET
  @Path("/{connectionId}")
  public Collection<Order> findOrders(@PathParam("connectionId") String connectionId,
        @QueryParam("state") OrderState orderState) throws ConnectorException {
    return orderService.findOrders(connectionId, orderState);
  }
}
