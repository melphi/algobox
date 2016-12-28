package io.algobox.microservice.container.filter;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import java.io.IOException;

@Priority(Priorities.HEADER_DECORATOR)
public final class CorsFilter implements ContainerResponseFilter {
  @Override
  public void filter(
      ContainerRequestContext requestContext, ContainerResponseContext responseContext)
          throws IOException {
    responseContext.getHeaders().add("Access-Control-Allow-Methods",
        "GET, POST, PUT, DELETE, OPTIONS");
    responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
    responseContext.getHeaders().add("Access-Control-Allow-Headers",
        "Content-Type, Accept, X-Requested-With");
    responseContext.getHeaders().add("Access-Control-Max-Age", "1800");
  }
}
