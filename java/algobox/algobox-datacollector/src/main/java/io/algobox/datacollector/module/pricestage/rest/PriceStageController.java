package io.algobox.datacollector.module.pricestage.rest;

import io.algobox.datacollector.module.pricestage.domain.PriceTickStage;
import io.algobox.datacollector.module.pricestage.service.PriceTickStageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Api(tags = "pricestages")
@Path("/pricestages")
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class PriceStageController {
  private final PriceTickStageService priceTickStageService;

  @Inject
  public PriceStageController(PriceTickStageService priceTickStageService) {
    this.priceTickStageService = priceTickStageService;
  }

  @ApiOperation(value = "Counts the staged prices.")
  @GET
  @Path("/{instrumentId}/count")
  public long countPrices(@PathParam("instrumentId") String instrumentId) {
    return priceTickStageService.countPrices(instrumentId);
  }

  @ApiOperation(value = "Returns the last staged prices.")
  @GET
  @Path("/{instrumentId}/last")
  public PriceTickStage findLastPrice(@PathParam("instrumentId") String instrumentId) {
    return priceTickStageService.findLastPrice(instrumentId);
  }
}
