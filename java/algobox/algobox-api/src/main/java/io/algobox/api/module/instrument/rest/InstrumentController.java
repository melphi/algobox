package io.algobox.api.module.instrument.rest;

import io.algobox.instrument.InstrumentInfoDetailed;
import io.algobox.instrument.InstrumentService;
import io.algobox.instrument.MarketHours;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Api(tags = "instruments")
@Path("/instruments")
@Produces(MediaType.APPLICATION_JSON)
public final class InstrumentController {
  private final InstrumentService instrumentService;

  @Inject
  public InstrumentController(InstrumentService instrumentService) {
    this.instrumentService = instrumentService;
  }

  @ApiOperation(value = "Returns the instrument info.")
  @GET
  @Path("/{instrumentId}")
  public InstrumentInfoDetailed getInstrumentInfo(@PathParam("instrumentId") String instrumentId) {
    return instrumentService.getInstrumentInfo(instrumentId);
  }

  @ApiOperation(value = "Returns the market hours.")
  @GET
  @Path("/{instrumentId}/hours/{timestamp}")
  public MarketHours getMarketHours(
      @PathParam("instrumentId") String instrumentId, @PathParam("timestamp") long timestamp) {
    return instrumentService.getMarketHours(instrumentId, timestamp).orElse(null);
  }

  @ApiOperation(value = "Returns the market hours of the previous day.")
  @GET
  @Path("/{instrumentId}/hours/yesterday")
  public MarketHours getMarketHoursYesterday(@PathParam("instrumentId") String instrumentId) {
    return instrumentService.getMarketHoursYesterday(instrumentId).orElse(null);
  }
}
