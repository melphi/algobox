package io.algobox.datacollector.module.connector.rest;

import io.algobox.connector.rest.AbstractConnectionController;
import io.algobox.connector.service.ConnectorService;
import io.swagger.annotations.Api;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Api(tags = "connections")
@Path("/connections")
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public final class ConnectionController extends AbstractConnectionController {
  @Inject
  public ConnectionController(ConnectorService connectionService) {
    super(connectionService);
  }
}
