package io.algobox.microservice.container;

import io.algobox.microservice.container.domain.ErrorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public final class GenericExceptionMapper implements ExceptionMapper<Exception> {
  private static final Logger LOGGER = LoggerFactory.getLogger(GenericExceptionMapper.class);

  @Override
  public Response toResponse(Exception exception) {
    int status = getStatus(exception);
    if (status == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()
        && LOGGER.isDebugEnabled()) {
      LOGGER.debug(String.format("Server error [%s].", exception.getMessage()), exception);
    }
    ErrorInfo errorInfo =
        new ErrorInfo(status, exception.getMessage(), exception.getClass().getName());
    return Response.serverError()
        .type(MediaType.APPLICATION_JSON)
        .status(status)
        .entity(errorInfo)
        .build();
  }

  private int getStatus(Exception exception) {
    int httpStatus = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
    if (exception instanceof WebApplicationException) {
      httpStatus = ((WebApplicationException) exception).getResponse().getStatus();
    }
    return httpStatus;
  }
}
