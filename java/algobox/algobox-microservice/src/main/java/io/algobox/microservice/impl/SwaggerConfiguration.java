package io.algobox.microservice.impl;

import io.swagger.models.Info;

import java.net.URI;

public class SwaggerConfiguration {
  private Info info;
  private URI apiUrl;

  public SwaggerConfiguration(Info info, URI apiUrl) {
    this.info = info;
    this.apiUrl = apiUrl;
  }

  public Info getInfo() {
    return info;
  }

  public URI getApiUrl() {
    return apiUrl;
  }
}
