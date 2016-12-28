package io.algobox.api.module.strategy.service.impl;

import avro.shaded.com.google.common.collect.ImmutableMap;
import io.algobox.api.module.strategy.service.StrategyManager;
import io.algobox.microservice.container.context.AppContext;
import io.algobox.microservice.container.context.MapAppContext;
import io.algobox.strategy.Strategy;
import io.algobox.strategy.dummy.DummyStrategy;
import org.junit.Test;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StrategyManagerImplTest {
  private static final String DUMMY_STRATEGY_JAR = "/dummy-strategy.jar";

  @Test
  public void shouldLoadDummyStrategyByDefault() {
    URL noJarsFolder = this.getClass().getProtectionDomain().getCodeSource().getLocation();
    StrategyManager strategyManager = createStrategyManager(noJarsFolder.getPath(), true);
    assertContainsDummyStrategy(strategyManager);
  }

  @Test
  public void shouldUploadJarWithStrategy() throws URISyntaxException {
    StrategyManager strategyManager = createStrategyManager(getStrategyJarPath(), false);
    assertContainsDummyStrategy(strategyManager);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldAvoidDuplicatedId() throws URISyntaxException {
    StrategyManager strategyManager = createStrategyManager(getStrategyJarPath(), true);
    assertContainsDummyStrategy(strategyManager);
  }

  private String getStrategyJarPath() throws URISyntaxException {
    URL url = StrategyManagerImplTest.class.getResource(DUMMY_STRATEGY_JAR);
    checkNotNull(url, String.format("File [%s] not found in resources.", DUMMY_STRATEGY_JAR));
    return Paths.get(url.toURI()).getParent().toString();
  }

  private void assertContainsDummyStrategy(StrategyManager strategyManager) {
    Class<? extends Strategy> found = strategyManager.getStrategyById(DummyStrategy.STRATEGY_ID);
    assertNotNull(found);
    assertEquals(DummyStrategy.class, found);
  }

  private StrategyManager createStrategyManager(
      String strategiesJarPath, boolean loadDummyByDefault) {
    AppContext appContext = new MapAppContext(
        ImmutableMap.of(StrategyManagerImpl.PARAMETER_STRATEGIES_JAR_PATH, strategiesJarPath));
    return new StrategyManagerImpl(appContext, loadDummyByDefault);
  }
}
