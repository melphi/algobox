package io.algobox.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public final class JsonUtils {
  private static final Gson GSON = new GsonBuilder().create();

  public static String toJson(Object object) {
    return GSON.toJson(object);
  }

  public static <T> T fromJson(String json, Class<T> clazz) {
    return GSON.fromJson(json, clazz);
  }

  public static <T> List<T> fromJsonAsList(String json) {
    Type listType = new TypeToken<ArrayList<T>>(){}.getType();
    return GSON.fromJson(json, listType);
  }
}
