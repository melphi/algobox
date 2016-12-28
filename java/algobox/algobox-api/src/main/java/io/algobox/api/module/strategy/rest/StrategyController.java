package io.algobox.api.module.strategy.rest;

import io.algobox.api.module.strategy.domain.StrategyHistory;
import io.algobox.api.module.strategy.domain.StrategyInfo;
import io.algobox.api.module.strategy.domain.StrategyRegistration;
import io.algobox.api.module.strategy.domain.dto.StrategyRegistrationRequestDto;
import io.algobox.api.module.strategy.service.StrategyService;
import io.algobox.strategy.StrategyEvent;
import io.algobox.strategy.StrategyEventService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

@Api(tags = "strategies")
@Path("/strategies")
@Produces(MediaType.APPLICATION_JSON)
@Service
public class StrategyController {
  private final StrategyService strategyService;
  private final StrategyEventService strategyEventService;

  @Inject
  public StrategyController(
      StrategyService strategyService, StrategyEventService strategyEventService) {
    this.strategyService = checkNotNull(strategyService);
    this.strategyEventService = checkNotNull(strategyEventService);
  }

  @ApiOperation(value = "Returns the list of active instances.")
  @GET
  public Collection<StrategyRegistration> getActiveInstances() {
    return strategyService.getActiveInstances();
  }

  @ApiOperation(value = "Returns the instances history.")
  @GET
  @Path("/history")
  public Collection<StrategyHistory> getInstancesHistory(
      @QueryParam("pageNumber") int pageNumber, @QueryParam("pageSize") int pageSize) {
    return strategyService.getInstancesHistory(pageNumber, pageSize);
  }

  @ApiOperation(value = "Returns the instance status.")
  @GET
  @Path("/{instanceId}/status")
  public StrategyInfo getInstanceStatus(@ApiParam(name = "instanceId",
      value = "The dummy instance id") @PathParam("instanceId") String instanceId) {
    return strategyService.getInstanceStatus(instanceId);
  }

  @ApiOperation(value = "Returns the instance events log.")
  @GET
  @Path("/{instanceId}/log")
  public Collection<StrategyEvent> getInstanceEventsLog(@ApiParam(name = "instanceId",
      value = "The dummy instance id") @PathParam("instanceId") String instanceId,
      @QueryParam("pageNumber") int pageNumber, @QueryParam("pageSize") int pageSize) {
    return strategyEventService.getInstanceEventsLog(instanceId, pageNumber, pageSize);
  }

  @ApiOperation(value = "Starts a new instance.")
  @POST
  public void createInstance(StrategyRegistrationRequestDto strategyCreationRequest) {
    strategyService.createInstance(strategyCreationRequest);
  }

  @ApiOperation(value = "Removes an existing instance.")
  @DELETE
  @Path("/{instanceId}")
  public void removeInstance(
      @ApiParam(name = "instanceId") @PathParam("instanceId") String instanceId) {
    strategyService.removeInstance(instanceId);
  }
}
