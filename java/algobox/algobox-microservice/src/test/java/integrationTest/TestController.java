package integrationTest;

import io.algobox.microservice.container.domain.HealthStatus;

import javax.annotation.security.PermitAll;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/test")
@PermitAll
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class TestController {
  public static final String TEST_MESSAGE = "Test";

  @GET
  public HealthStatus getHealthStatus() {
    return new HealthStatus(TEST_MESSAGE);
  }
}
