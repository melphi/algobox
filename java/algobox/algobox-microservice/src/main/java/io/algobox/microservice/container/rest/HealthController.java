package io.algobox.microservice.container.rest;

import io.algobox.microservice.container.domain.HealthStatus;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.annotation.security.PermitAll;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Api(tags = "health")
@Path("/health")
@PermitAll
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class HealthController {
  private static final String MESSAGE_HEALTHY = "Service healthy!";

  @ApiOperation(value = "Returns an healthy message.")
  @GET
  public HealthStatus getHealthStatus() {
    return new HealthStatus(MESSAGE_HEALTHY);
  }
}
