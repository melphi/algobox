package io.algobox.connector.rest;

import io.algobox.common.domain.StringValueDto;
import io.algobox.common.exception.ServiceException;
import io.algobox.connector.ConnectionInfo;
import io.algobox.connector.ConnectorException;
import io.algobox.connector.domain.dto.ConnectionRegistrationDto;
import io.algobox.connector.domain.dto.ConnectionRegistrationRequestDto;
import io.algobox.connector.domain.dto.InstrumentSubscriptionDto;
import io.algobox.connector.service.ConnectorService;
import io.algobox.instrument.InstrumentInfo;
import io.swagger.annotations.ApiOperation;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractConnectionController {
  protected ConnectorService connectorService;

  public AbstractConnectionController(ConnectorService connectionService) {
    this.connectorService = connectionService;
  }

  @ApiOperation(value = "Returns the list of available connections.")
  @GET
  public Collection<ConnectionRegistrationDto> findAllConnections() {
    return connectorService.findAllConnections();
  }

  @ApiOperation(value = "Returns the list of available connectors.")
  @GET
  @Path("/connectors")
  public Collection<StringValueDto> findAllConnectors() {
    return connectorService.findAllConnectors();
  }

  @ApiOperation(value = "Creates a new connection.")
  @POST
  public void registerConnection(ConnectionRegistrationRequestDto connectionRegistrationRequest)
      throws ConnectorException, ServiceException {
    connectorService.createConnection(connectionRegistrationRequest);
  }

  @ApiOperation(value = "Removes an existing connection.")
  @DELETE
  @Path("/{connectionId}")
  public void removeConnection(@PathParam("connectionId") String connectionId)
      throws ConnectorException, ServiceException {
    connectorService.removeConnection(connectionId);
  }

  @ApiOperation(value = "Returns the connection status.")
  @GET
  @Path("/{connectionId}/status")
  public ConnectionInfo getConnectionStatus(@PathParam("connectionId") String connectionId)
      throws ServiceException {
    return connectorService.getConnectionInfo(connectionId);
  }

  @ApiOperation(value = "Establishes a connection.")
  @Path("/{connectionId}/connect")
  @POST
  public void connect(@PathParam("connectionId") String connectionId) throws ServiceException {
    connectorService.connect(connectionId);
  }

  @ApiOperation(value = "Disconnects a connection.")
  @Path("/{connectionId}/disconnect")
  @POST
  public void disconnect(@PathParam("connectionId") String connectionId)
      throws ServiceException, ConnectorException {
    connectorService.disconnect(connectionId);
  }

  @ApiOperation(value = "Returns the subscribed instrument.")
  @GET
  @Path("/{connectionId}/subscriptions")
  public Collection<StringValueDto> getSubscribedInstruments(
      @PathParam("connectionId") String connectionId) {
    return connectorService.getSubscribedInstruments(connectionId);
  }

  @ApiOperation(value = "Subscribes an instrument.")
  @Path("/{connectionId}/subscriptions/register")
  @POST
  public void subscribeInstrument(@PathParam("connectionId") String connectionId,
      InstrumentSubscriptionDto instrumentSubscription) throws ServiceException {
    checkNotNull(instrumentSubscription);
    connectorService.subscribeInstrument(connectionId, instrumentSubscription.getInstrumentId());
  }

  @ApiOperation(value = "Un-subscribes an instrument.")
  @Path("/{connectionId}/subscriptions/remove")
  @PUT
  public void unSubscribeInstrument(@PathParam("connectionId") String connectionId,
      InstrumentSubscriptionDto instrumentSubscription) {
    checkNotNull(instrumentSubscription);
    connectorService.unSubscribeInstrument(connectionId, instrumentSubscription.getInstrumentId());
  }

  @ApiOperation(value = "Search instrument.")
  @GET
  @Path("/{connectionId}/instruments")
  public Collection<InstrumentInfo> searchInstruments(
      @PathParam("connectionId") String connectionId, @QueryParam("searchTerm") String searchTerm)
      throws ConnectorException, ServiceException {
    return connectorService.searchInstruments(connectionId, searchTerm);
  }
}
