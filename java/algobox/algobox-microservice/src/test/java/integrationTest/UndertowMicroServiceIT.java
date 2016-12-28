package integrationTest;

import io.algobox.microservice.impl.undertow.UndertowMicroService;

public class UndertowMicroServiceIT extends AbstractMicroServiceIT {
  public UndertowMicroServiceIT() {
    super(UndertowMicroService.class);
  }
}
