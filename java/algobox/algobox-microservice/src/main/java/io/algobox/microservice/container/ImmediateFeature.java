package io.algobox.microservice.container;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

import javax.inject.Inject;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

/**
 * Enables the @{@link org.glassfish.hk2.api.Immediate} annotation for eager singletons.
 */
public class ImmediateFeature implements Feature {
  @Inject
  public ImmediateFeature(ServiceLocator locator) {
    ServiceLocatorUtilities.enableImmediateScope(locator);
  }

  @Override
  public boolean configure(FeatureContext context) {
    return true;
  }
}
