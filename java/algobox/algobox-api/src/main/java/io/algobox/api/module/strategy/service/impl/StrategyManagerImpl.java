package io.algobox.api.module.strategy.service.impl;

import avro.shaded.com.google.common.collect.ImmutableList;
import avro.shaded.com.google.common.collect.ImmutableMap;
import io.algobox.api.module.strategy.service.StrategyManager;
import io.algobox.microservice.container.context.AppContext;
import io.algobox.strategy.Strategy;
import io.algobox.strategy.dummy.DummyStrategy;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;

@Service
public final class StrategyManagerImpl implements StrategyManager {
  public static final String PARAMETER_STRATEGIES_JAR_PATH = "application.strategiesJarPath";

  private static final FilenameFilter JAR_FILTER = (file, name) -> name.endsWith(".jar");
  private static final String PROPERTY_STRATEGY_ID = "STRATEGY_ID";
  private static final String SUFFIX_CLASS = ".class";
  private static final String SUFFIX_STRATEGY_CLASS = "Strategy" + SUFFIX_CLASS;
  private static final Logger LOGGER = LoggerFactory.getLogger(StrategyManagerImpl.class);

  private final Map<String, Class<? extends Strategy>> strategies;

  @Inject
  public StrategyManagerImpl(AppContext appContext) {
    this(appContext, true);
  }

  public StrategyManagerImpl(AppContext appContext, boolean loadDummyByDefault) {
    String strategiesJarPath = appContext.getRequiredValue(PARAMETER_STRATEGIES_JAR_PATH);
    try {
      strategies = loadStrategies(strategiesJarPath, loadDummyByDefault);
    } catch (Exception e) {
      LOGGER.error(String.format("Error while loading strategy jars from [%s]: [%s].",
          strategiesJarPath, e.getMessage()), e);
      throw new IllegalArgumentException(e);
    }
  }

  @Override
  public synchronized Class<? extends Strategy> getStrategyById(String strategyId) {
    checkNotNullOrEmpty(strategyId);
    return checkNotNull(strategies.get(strategyId),
        String.format("Strategy [%s] is not registered.", strategyId));
  }

  @SuppressWarnings("unchecked")
  private Map<String, Class<? extends Strategy>> loadStrategies(
      String strategiesJarPath, boolean loadDummyByDefault) throws Exception {
    checkArgument(strategiesJarPath.startsWith("/"),
        String.format("Strategies jar folder [%s] should start with /.", strategiesJarPath));
    File folder = new File(strategiesJarPath);
    checkArgument(folder.exists() && folder.isDirectory(),
        String.format("Strategies jar folder [%s] not found.", strategiesJarPath));
    boolean strategiesFound = false;
    ImmutableMap.Builder<String, Class<? extends Strategy>> result = ImmutableMap.builder();
    File[] jars = folder.listFiles(JAR_FILTER);
    if (jars != null) {
      for (File jar : jars) {
        Collection<String> strategyClasses = findStrategyClasses(jar);
        if (!strategyClasses.isEmpty()) {
          URL[] urls = new URL[]{jar.toURI().toURL()};
          ClassLoader loader = new URLClassLoader(urls, this.getClass().getClassLoader());
          for (String strategyClass : strategyClasses) {
            Class clazz = loader.loadClass(strategyClass);
            if (isStrategyImplementation(clazz)) {
              String strategyId = getStrategyId((Class<? extends Strategy>) clazz);
              try {
                result.put(strategyId, (Class<? extends Strategy>) clazz);
                strategiesFound = true;
                LOGGER.info(String.format("Loaded strategy [%s] with id [%s] from jar [%s].",
                    clazz.getName(), strategyId, jar.getName()));
              } catch (Exception e) {
                throw new IllegalArgumentException(String.format(
                    "Can not load strategy [%s] from [%s] because id [%s] is duplicated.",
                    clazz.getName(), jar.getName(), strategyId));
              }
            }
          }
        }
      }
    }
    if (!strategiesFound) {
      LOGGER.warn(String.format(
          "No jars containing strategies found in [%s].", strategiesJarPath));
    }
    if (loadDummyByDefault) {
      result.put(DummyStrategy.STRATEGY_ID, DummyStrategy.class);
    }
    return result.build();
  }

  private boolean isStrategyImplementation(Class clazz) {
    return Strategy.class.isAssignableFrom(clazz)
        && !Modifier.isInterface(clazz.getModifiers())
        && !Modifier.isAbstract(clazz.getModifiers());
  }

  private Collection<String> findStrategyClasses(File jar) throws IOException {
    ImmutableList.Builder<String> result = ImmutableList.builder();
    ZipEntry entry;
    try (ZipInputStream inputStream = new ZipInputStream(new FileInputStream(jar.toString()))) {
      while ((entry = inputStream.getNextEntry()) != null) {
        if (entry.getName().endsWith(SUFFIX_STRATEGY_CLASS)) {
          String className = entry.getName()
              .replace('/', '.')
              .substring(0, entry.getName().length() - SUFFIX_CLASS.length());
          result.add(className);
        }
      }
    }
    return result.build();
  }

  private String getStrategyId(Class<? extends Strategy> clazz) {
    checkNotNull(clazz);
    try {
      Field field = clazz.getField(PROPERTY_STRATEGY_ID);
      return (String) field.get(null);
    } catch (Exception e) {
      throw new IllegalArgumentException(String.format(
          "Strategy [%s] should contain the [public static String %s] property.",
          clazz.getName(), PROPERTY_STRATEGY_ID));
    }
  }
}
