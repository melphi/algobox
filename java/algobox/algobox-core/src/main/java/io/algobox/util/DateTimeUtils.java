package io.algobox.util;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public final class DateTimeUtils {
  public static final int MAX_NANOSECONDS = 999999999;

  public static ZonedDateTime getDateTime(Instant instant) {
    return ZonedDateTime.ofInstant(instant, ZoneOffset.UTC);
  }

  public static ZonedDateTime getDateTime(Long timestamp) {
    MorePreconditions.checkTimestamp(timestamp);
    return ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC);
  }

  public static ZonedDateTime getCurrentUtcDateTime() {
    return ZonedDateTime.now(ZoneOffset.UTC);
  }

  public static long getCurrentUtcTimestampMilliseconds() {
    ZonedDateTime dateTime = ZonedDateTime.now(ZoneOffset.UTC);
    return dateTime.toInstant().toEpochMilli();
  }

  public static long getCurrentUtcTimestampSeconds() {
    ZonedDateTime dateTime = ZonedDateTime.now(ZoneOffset.UTC);
    return dateTime.toInstant().getEpochSecond();
  }

  public static long getEndOfDayTimestamp(long timestampUtc) {
    return getEndOfDayTimestamp(getDateTime(timestampUtc));
  }

  public static long getEndOfDayTimestamp(ZonedDateTime dateTime) {
    return ZonedDateTime.of(dateTime.getYear(), dateTime.getMonth().getValue(),
        dateTime.getDayOfMonth(), 23, 59, 59, 999999999, ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli();
  }

  public static long getBeginOfDayTimestamp(ZonedDateTime dateTime) {
    return ZonedDateTime.of(dateTime.getYear(), dateTime.getMonth().getValue(),
        dateTime.getDayOfMonth(), 0, 0, 0, 0, ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli();
  }

  public static long getUtcMilliseconds(ZonedDateTime dateTime) {
    return dateTime.withZoneSameInstant(ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli();
  }
}
