package integrationTest;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import io.algobox.microservice.MicroService;
import io.algobox.microservice.MicroServiceBuilder;
import io.algobox.microservice.container.domain.HealthStatus;
import io.algobox.microservice.impl.AbstractMicroService;
import io.swagger.models.Info;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.junit.After;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import static com.google.common.base.Preconditions.checkNotNull;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public abstract class AbstractMicroServiceIT {
  private final Class<? extends AbstractMicroService> containerImplementation;
  private final Client client;

  private MicroService microService;

  public AbstractMicroServiceIT(Class<? extends AbstractMicroService> microServiceImplementation) {
    this.containerImplementation = checkNotNull(microServiceImplementation);
    this.client = ClientBuilder.newBuilder().build();
  }

  @After
  public void after() {
    if (microService != null) {
      microService.stop();
    }
  }

  @Test
  public void testShouldStartAndStopWithDefaultConfiguration()
      throws ExecutionException, InterruptedException {
    microService = MicroServiceBuilder.newBuilder()
        .withImplementationClass(containerImplementation)
        .build();
    microService.startAsync();
    assertServiceHealthy(MicroServiceBuilder.DEFAULT_PORT);
    microService.stop();
  }

  @Test
  public void testShouldDisableSwaggerByDefault()
      throws ExecutionException, InterruptedException {
    microService = MicroServiceBuilder.newBuilder()
        .withImplementationClass(containerImplementation)
        .build();
    microService.startAsync();
    assertServiceHealthy(MicroServiceBuilder.DEFAULT_PORT);
    try {
      assertSwaggerDefinition(MicroServiceBuilder.DEFAULT_PORT);
      throw new IllegalArgumentException("Swagger definitions should be disabled.");
    } catch (AssertionError error) {
      // Passes.
    }
    try {
      assertSwaggerWebApp(MicroServiceBuilder.DEFAULT_PORT);
      throw new IllegalArgumentException("Swagger web application should be disabled.");
    } catch (AssertionError error) {
      // Passes.
    }
    microService.stop();
  }

  @Test
  public void testShouldEnableSwagger() throws URISyntaxException {
    microService = MicroServiceBuilder.newBuilder()
        .withSwagger(new Info())
        .withImplementationClass(containerImplementation)
        .withPort(IntegrationTestConstants.DEFAULT_API_PORT)
        .build();
    microService.startAsync();
    assertServiceHealthy(IntegrationTestConstants.DEFAULT_API_PORT);
    assertSwaggerDefinition(IntegrationTestConstants.DEFAULT_API_PORT);
    assertSwaggerWebApp(IntegrationTestConstants.DEFAULT_API_PORT);
    microService.stop();
  }

  @Test
  public void testShouldRegisterAController() {
    microService = MicroServiceBuilder.newBuilder()
        .withImplementationClass(containerImplementation)
        .withRestControllers(ImmutableList.of(TestController.class))
        .withPort(IntegrationTestConstants.DEFAULT_API_PORT)
        .build();
    microService.startAsync();
    assertServiceHealthy(IntegrationTestConstants.DEFAULT_API_PORT);
    HealthStatus healthStatus = sendGetRequest(IntegrationTestConstants.DEFAULT_API_PORT,
        "http://localhost:" + IntegrationTestConstants.DEFAULT_API_PORT + "/test",
        HealthStatus.class);
    assertNotNull(healthStatus);
    assertEquals(TestController.TEST_MESSAGE, healthStatus.getMessage());
    microService.stop();
  }

  private <T> T sendGetRequest(int port, String url, Class<T> responseClass) {
    WebTarget target = client.target(url);
    Response response = target.request()
        .buildGet()
        .invoke();
    assertEquals(HttpStatus.OK_200.getStatusCode(), response.getStatus());
    return response.readEntity(responseClass);
  }

  private void assertServiceHealthy(int port) {
    HealthStatus healthStatus = sendGetRequest(
        port, "http://localhost:" + port + "/health", HealthStatus.class);
    assertNotNull(healthStatus);
    assertFalse(Strings.isNullOrEmpty(healthStatus.getMessage()));
  }

  private void assertSwaggerDefinition(int port) {
    String swagger = sendGetRequest(
        port, "http://localhost:" + port + "/swagger.json", String.class);
    assertFalse(Strings.isNullOrEmpty(swagger));
  }

  private void assertSwaggerWebApp(int port) {
    String index = sendGetRequest(
        port, "http://localhost:" + port + "/swagger", String.class);
    assertFalse(Strings.isNullOrEmpty(index));
    String css = sendGetRequest(
        port, "http://localhost:" + port + "/swagger/css/style.css", String.class);
    assertFalse(Strings.isNullOrEmpty(css));
  }
}
