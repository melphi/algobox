package io.algobox.microservice.impl.grizzly;

import io.algobox.microservice.container.JerseyApplication;
import io.algobox.microservice.impl.AbstractMicroService;
import org.glassfish.grizzly.CompletionHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public final class GrizzlyMicroService extends AbstractMicroService {
  private final HttpServer server;

  public GrizzlyMicroService() {
    server = createGrizzlyServer();
  }

  private HttpServer createGrizzlyServer() {
    URI apiUri = null;
    try {
      apiUri = new URI("http://0.0.0.0:" + configurator.getPort());
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException(
          String.format("Invalid port value [%d].", configurator.getPort()), e);
    }
    return GrizzlyHttpServerFactory.createHttpServer(apiUri, new JerseyApplication(), false);
  }

  @Override
  public void startAsync() {
    try {
      server.start();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public Future<Object> stopAsync() {
    CompletableFuture<Object> future = new CompletableFuture<>();
    server.shutdown()
        .addCompletionHandler(new CompletionHandler<HttpServer>() {
          @Override
          public void cancelled() {
            future.completeExceptionally(new Exception("Stop cancelled."));
          }

          @Override
          public void failed(Throwable throwable) {
            future.completeExceptionally(throwable);
          }

          @Override
          public void completed(HttpServer result) {
            future.complete(result);
          }

          @Override
          public void updated(HttpServer result) {
            future.complete(result);
          }
        });
    return future;
  }
}
