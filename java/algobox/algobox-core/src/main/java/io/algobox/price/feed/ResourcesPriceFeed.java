package io.algobox.price.feed;

import io.algobox.price.PriceTick;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;

public final class ResourcesPriceFeed implements PriceFeed {
  private final String instrumentId;
  private final String resourceName;

  public ResourcesPriceFeed(String resourceName, String instrumentId) {
    this.instrumentId = checkNotNullOrEmpty(instrumentId);
    this.resourceName = checkNotNullOrEmpty(resourceName).startsWith("/") ?
      resourceName : "/" + resourceName;
  }

  @Override
  public Iterable<PriceTick> getPrices() {
    InputStream inputStream = ResourcesPriceFeed.class.getResourceAsStream(resourceName);
    checkNotNull(inputStream, String.format("Resource [%s] not found.", resourceName));
    return () -> new PricesIterator(inputStream);
  }

  private class PricesIterator implements Iterator<PriceTick> {
    private final BufferedReader bufferedReader;
    private String nextLine = null;

    public PricesIterator(InputStream inputStream) {
      try {
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        // Skip the header.
        nextLine = bufferedReader.readLine();
        // Initialise first line.
        nextLine = bufferedReader.readLine();
      } catch (IOException e) {
        throw new IllegalArgumentException(e);
      }
    }

    @Override
    public boolean hasNext() {
      return nextLine != null;
    }

    @Override
    public PriceTick next() {
      if (nextLine == null) {
        throw new NoSuchElementException();
      }
      PriceTick priceTick = createPriceTick(nextLine);
      try {
        nextLine = bufferedReader.readLine();
      } catch (IOException e) {
        throw new IllegalArgumentException(e);
      }
      return priceTick;
    }

    private PriceTick createPriceTick(String nextLine) {
      checkNotNullOrEmpty(nextLine, "Empty or null line.");
      String[] parts =  nextLine.split(",");
      checkArgument(parts.length > 2, "Unsupported number of columns in CSV.");
      return new PriceTick(instrumentId,
          getUtcTimestampFromDateString(parts[0]),
          Double.parseDouble(parts[1]),
          Double.parseDouble(parts[2]));
    }

    /**
     * Returns the timestamp from string, which can be a formatted date or a timestamp.
     */
    private long getUtcTimestampFromDateString(String line) {
      checkNotNullOrEmpty(line);
      try {
        return Long.parseLong(line);
      } catch (NumberFormatException e) {
        int day = Integer.parseInt(line.substring(0, 2));
        int month = Integer.parseInt(line.substring(3, 5));
        int year = Integer.parseInt(line.substring(6, 10));
        int hour = Integer.parseInt(line.substring(11, 13));
        int minute = Integer.parseInt(line.substring(14, 16));
        int second = Integer.parseInt(line.substring(17, 19));
        int nanoseconds = Integer.parseInt(line.substring(20));
        return ZonedDateTime.of(
            year, month, day, hour, minute, second, nanoseconds, ZoneOffset.UTC).toInstant()
            .toEpochMilli();
      }
    }
  }
}
