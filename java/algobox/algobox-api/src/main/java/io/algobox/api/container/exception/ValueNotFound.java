package io.algobox.api.container.exception;

import javax.ws.rs.NotFoundException;

public final class ValueNotFound extends NotFoundException {
  public ValueNotFound() {
    super("Value not found.");
  }

  public ValueNotFound(String message) {
    super(message);
  }
}
