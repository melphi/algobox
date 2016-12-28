package io.algobox.microservice.impl.undertow;

import io.algobox.microservice.container.JerseyApplication;
import io.algobox.microservice.impl.AbstractMicroService;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletInfo;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;

import javax.servlet.ServletException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public final class UndertowMicroService extends AbstractMicroService {
  private static final String CONTEXT_ROOT = "/";
  private static final String DEFAULT_HOST = "0.0.0.0";

  private final Undertow undertow;

  public UndertowMicroService() {
    this.undertow = createServer();
  }

  @Override
  public void startAsync() {
    undertow.start();
  }

  @Override
  public Future<Object> stopAsync() {
    CompletableFuture<Object> future = new CompletableFuture<>();
    undertow.stop();
    future.complete(null);
    return future;
  }

  private Undertow createServer() {
    ServletInfo holder = Servlets.servlet(JerseyApplication.class.getName(), ServletContainer.class)
        .setLoadOnStartup(0)
        .setAsyncSupported(true)
        .setEnabled(true)
        .addMapping("/*")
        .addInitParam(
            ServletProperties.JAXRS_APPLICATION_CLASS, JerseyApplication.class.getName());

    DeploymentInfo webApp = Servlets.deployment()
        .setClassLoader(Thread.currentThread().getContextClassLoader())
        .setContextPath(CONTEXT_ROOT)
        .setDefaultEncoding(StandardCharsets.UTF_8.toString())
        .setDeploymentName(UndertowMicroService.class.getSimpleName())
        .setDisplayName(UndertowMicroService.class.getSimpleName())
        .setEagerFilterInit(true)
        .addServlet(holder);
    DeploymentManager manager = Servlets.defaultContainer()
        .addDeployment(webApp);
    manager.deploy();

    try {
      PathHandler path = Handlers.path(Handlers.redirect(CONTEXT_ROOT))
          .addPrefixPath(CONTEXT_ROOT, manager.start());
      return Undertow.builder()
          .addHttpListener(configurator.getPort(), DEFAULT_HOST)
          .setHandler(path)
          .build();
    } catch (ServletException e) {
      throw new IllegalArgumentException(String.format(
          "Unable to start application: [%s].", e.getMessage()), e);
    }
  }
}
