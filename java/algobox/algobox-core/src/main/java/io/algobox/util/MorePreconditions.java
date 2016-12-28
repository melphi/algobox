package io.algobox.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class MorePreconditions {
  public static String checkNotNullOrEmpty(final String value) {
    return checkNotNullOrEmpty(value, "String is null or empty.");
  }

  public static String checkNotNullOrEmpty(final String value, final String errorMessage) {
    if (value == null || value.isEmpty()) {
      throw new IllegalArgumentException(errorMessage);
    }
    return value;
  }

  public static Long checkTimestamp(Long timestamp) {
    checkNotNull(timestamp, "Timestamp is null.");
    checkArgument(timestamp > 1000000000000L, "Timestamp should be positive and in milliseconds.");
    return timestamp;
  }

  public static void checkPagination(int pageNumber, int pageSize) {
    checkArgument(pageNumber >= 0, "Invalid page number");
    checkArgument(pageSize > 0, "Invalid page size");
  }
}
