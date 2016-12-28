package io.algobox.microservice.container.domain;

public final class ErrorInfo {
  private int status;
  private String message;
  private String exceptionClass;

  public ErrorInfo(int status, String message, String exceptionClass) {
    this.status = status;
    this.message = message;
    this.exceptionClass = exceptionClass;
  }

  public int getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }

  public String getExceptionClass() {
    return exceptionClass;
  }
}
