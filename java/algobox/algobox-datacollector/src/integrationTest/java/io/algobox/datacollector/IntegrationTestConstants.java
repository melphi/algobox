package io.algobox.datacollector;

import io.algobox.price.PriceTick;

public class IntegrationTestConstants {
  public static final String DEFAULT_MONGO_CONNECTION_URL = "mongodb://127.0.0.1:27017";
  public static final String DEFAULT_MONGO_DATABASE = "datacollector-itest";
  public static final String DEFAULT_SOURCE = "source1";
  public static final PriceTick DEFAULT_PRICE_TICK_1 =
      new PriceTick("instrument1", 123, 1.000002, 1.000001);
  public static final PriceTick DEFAULT_PRICE_TICK_2 =
      new PriceTick("instrument2", 124, 1.100004, 1.100003);
}
