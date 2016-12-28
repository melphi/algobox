package io.algobox.util;

import io.algobox.price.StandardTimeFrame;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class MoreDateTimeUtils {
  public static long getTimeFrameEnd(long timestampUtc, StandardTimeFrame timeFrame) {
    checkArgument(timestampUtc > 0.0);
    long millisecondsInTimeFrame = getMillisecondsInTimeFrame(timeFrame);
    double times = (timestampUtc / millisecondsInTimeFrame) + 1;
    return (long) (millisecondsInTimeFrame * times) - 1;
  }

  public static long getTimeFrameStart(long timestampUtc, StandardTimeFrame timeFrame) {
    checkArgument(timestampUtc > 0.0);
    long millisecondsInTimeFrame = getMillisecondsInTimeFrame(timeFrame);
    double times = timestampUtc / millisecondsInTimeFrame;
    return (long) (millisecondsInTimeFrame * times);
  }

  public static long getMillisecondsInTimeFrame(StandardTimeFrame timeFrame) {
    checkNotNull(timeFrame);
    switch (timeFrame) {
      case M5:
        return 60 * 5 * 1000;
      case M15:
        return 60 * 15 * 1000;
      default:
        throw new IllegalArgumentException(
            String.format("Unsupported time frame [%s].", timeFrame.getValue()));
    }
  }
}
