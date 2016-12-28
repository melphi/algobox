package io.algobox.api.module.indicator.rest;

import io.algobox.indicator.IndicatorService;
import io.algobox.price.PriceOhlc;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Api(tags = "indicators")
@Path("/indicators")
@Produces(MediaType.APPLICATION_JSON)
public final class IndicatorController {
  private final IndicatorService indicatorService;

  @Inject
  public IndicatorController(IndicatorService indicatorService) {
    this.indicatorService = indicatorService;
  }

  @ApiOperation(value = "Returns the period ohlc.")
  @GET
  @Path("/{instrumentId}/ohlc")
  public PriceOhlc getOhlc(
      @PathParam("instrumentId") String instrumentId,
      @QueryParam("fromTimestamp") Long fromTimestamp,
      @QueryParam("toTimestamp") Long toTimestamp) {
    return indicatorService.getOhlc(instrumentId, fromTimestamp, toTimestamp);
  }
}
