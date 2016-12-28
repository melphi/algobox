package io.algobox.api.module.instrument.exception;

import javax.ws.rs.NotFoundException;

public final class InstrumentNotFoundException extends NotFoundException {
  public InstrumentNotFoundException() {
    super("Instrument not found,");
  }
}
