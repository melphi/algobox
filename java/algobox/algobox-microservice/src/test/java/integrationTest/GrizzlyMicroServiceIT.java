package integrationTest;

import io.algobox.microservice.impl.grizzly.GrizzlyMicroService;

public class GrizzlyMicroServiceIT extends AbstractMicroServiceIT {
  public GrizzlyMicroServiceIT() {
    super(GrizzlyMicroService.class);
  }
}
