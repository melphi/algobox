package io.algobox.microservice.container;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import io.algobox.microservice.container.context.BootConfigurator;
import io.algobox.microservice.container.filter.CorsFilter;
import io.algobox.microservice.container.rest.HealthController;
import io.algobox.microservice.container.rest.SwaggerController;
import io.algobox.microservice.impl.SwaggerConfiguration;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import org.glassfish.hk2.utilities.Binder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Collection;
import java.util.Set;

public class JerseyApplication extends ResourceConfig {
  private static final Logger LOGGER = LoggerFactory.getLogger(JerseyApplication.class);
  private static final Collection<Class<?>> BASE_CONTROLLERS =
      ImmutableList.of(HealthController.class);

  public JerseyApplication() {
    BootConfigurator configurator = BootConfigurator.getInstance();
    if (configurator.getAppContext().isPresent()) {
      registerInstances(new ContainerModule(configurator.getAppContext().get()));
    }
    for (Binder binder: configurator.getBinders()) {
      registerInstances(binder);
    }
    Iterable<Class<?>> allControllers = Iterables.concat(
        BASE_CONTROLLERS, configurator.getRestControllers());
    configureJersey(allControllers);
    if (configurator.getSwaggerConfiguration().isPresent()) {
      configureSwagger(configurator.getSwaggerConfiguration().get(), allControllers);
    }
  }

  private void configureJersey(Iterable<Class<?>> restControllers) {
    for (Class controller: restControllers) {
      register(controller);
      LOGGER.info(String.format("Controller [%s] registered.", controller));
    }

    registerClasses(GenericExceptionMapper.class,
        ImmediateFeature.class,
        JacksonFeature.class);

    // Important: The order of registration classes is relevant, filters must be set at the end.
    registerClasses(CorsFilter.class);
  }

  private void configureSwagger(
      SwaggerConfiguration swaggerConfiguration, Iterable<Class<?>> restControllers) {
    LOGGER.warn("Exposing Api documentation at <root>/swagger.json and <root>/swagger.yaml.");
    registerClasses(ApiListingResource.class, SwaggerSerializers.class);
    BeanConfig beanConfig = new BeanConfig();
    beanConfig.setInfo(swaggerConfiguration.getInfo());

    if (swaggerConfiguration.getApiUrl() != null) {
      URI apiUrl = swaggerConfiguration.getApiUrl();
      if (apiUrl.getPort() > 0) {
        beanConfig.setHost(apiUrl.getHost() + ":" + apiUrl.getPort());
      } else {
        beanConfig.setHost(apiUrl.getHost());
      }
      if (!Strings.isNullOrEmpty(apiUrl.getPath())) {
        beanConfig.setBasePath(apiUrl.getPath());
      }
      beanConfig.setSchemes(new String[] { apiUrl.getScheme() });
    }

    Set<String> packages = Sets.newHashSet();
    for (Class controller: restControllers) {
      packages.add(controller.getPackage().getName());
    }
    beanConfig.setResourcePackage(Joiner.on(",").join(packages));
    beanConfig.setScan(true);

    LOGGER.warn("Exposing Swagger web application at <root>/swagger.");
    register(SwaggerController.class);
  }
}
