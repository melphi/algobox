package io.algobox.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class ExceptionUtils {
  public static String stackTraceToString(Throwable throwable) {
    StringWriter stackWriter = new StringWriter();
    throwable.printStackTrace(new PrintWriter(stackWriter));
    return stackWriter.toString();
  }
}
