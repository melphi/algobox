package io.algobox.util;

import io.algobox.instrument.MarketHours;
import io.algobox.instrument.InstrumentInfoDetailed;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

public final class MarketHoursUtils {
  /**
   * A local market, eg DAX index, starts and ends the same day in the local time.
   */
  public static Optional<MarketHours> getMarketHoursLocalMarket(
      InstrumentInfoDetailed info, ZonedDateTime localDateTime) {
    checkArgument(localDateTime.getZone().getId().equals(info.getTimeZoneId()));
    ZonedDateTime previousDayDateTime = localDateTime.minusDays(1);
    switch (localDateTime.getDayOfWeek()) {
      case MONDAY:
        previousDayDateTime = localDateTime.minusDays(3);
      case TUESDAY:
      case WEDNESDAY:
      case THURSDAY:
      case FRIDAY:
        ZonedDateTime marketOpen = ZonedDateTime.of(localDateTime.getYear(),
            localDateTime.getMonth().getValue(), localDateTime.getDayOfMonth(), info.getOpenHour(),
            info.getOpenMinute(), 0, 0, localDateTime.getZone());
        ZonedDateTime marketClose = ZonedDateTime.of(localDateTime.getYear(),
            localDateTime.getMonth().getValue(), localDateTime.getDayOfMonth(), info.getCloseHour(),
            info.getCloseMinute(), 0, 0, localDateTime.getZone());
        ZonedDateTime orb5minOpen = ZonedDateTime.of(localDateTime.getYear(),
            localDateTime.getMonth().getValue(), localDateTime.getDayOfMonth(),
            info.getOrb5MinOpenHour(), 0, 0, 0, localDateTime.getZone());
        ZonedDateTime previousMarketOpen = ZonedDateTime.of(previousDayDateTime.getYear(),
            previousDayDateTime.getMonth().getValue(), previousDayDateTime.getDayOfMonth(),
            info.getOpenHour(), info.getOpenMinute(), 0, 0, previousDayDateTime.getZone());
        ZonedDateTime previousMarketClose = ZonedDateTime.of(previousDayDateTime.getYear(),
            previousDayDateTime.getMonth().getValue(), previousDayDateTime.getDayOfMonth(),
            info.getCloseHour(), info.getCloseMinute(), 0, 0, localDateTime.getZone());
        return Optional.of(new MarketHours(
            DateTimeUtils.getUtcMilliseconds(marketOpen),
            DateTimeUtils.getUtcMilliseconds(marketClose),
            DateTimeUtils.getUtcMilliseconds(orb5minOpen),
            DateTimeUtils.getUtcMilliseconds(getOrb5MinClose(orb5minOpen)),
            DateTimeUtils.getUtcMilliseconds(previousMarketOpen),
            DateTimeUtils.getUtcMilliseconds(previousMarketClose)));
      case SATURDAY:
      case SUNDAY:
        return Optional.empty();
      default:
        throw new IllegalArgumentException(String.format("Unexpected day [%s] for [%s].",
            localDateTime.getDayOfWeek(), localDateTime.toString()));
    }
  }

  /**
   * A 24 hours market, eg forex, starts at market close and ends immediately before market
   * close, remaining open during all night.
   */
  public static Optional<MarketHours> getMarketHours24HoursMarket(
      InstrumentInfoDetailed info, ZonedDateTime localDateTime) {
    checkArgument(localDateTime.getZone().getId().equals(info.getTimeZoneId()));
    checkArgument(Objects.equals(info.getOpenHour(), info.getCloseHour()) &&
            Objects.equals(info.getOpenMinute(), info.getCloseMinute()),
        "Market open and close should match in a 24 hours market.");
    ZonedDateTime marketOpen;
    ZonedDateTime orb5minOpen;
    ZonedDateTime previousMarketOpen;
    ZonedDateTime previousDayDateTime = localDateTime.minusDays(1);
    switch (localDateTime.getDayOfWeek()) {
      case MONDAY:
        previousDayDateTime = localDateTime.minusDays(3);
      case TUESDAY:
      case WEDNESDAY:
      case THURSDAY:
      case FRIDAY:
        marketOpen = ZonedDateTime.of(localDateTime.getYear(), localDateTime.getMonth().getValue(),
            localDateTime.getDayOfMonth(), info.getOpenHour(), info.getOpenMinute(), 0, 0,
            localDateTime.getZone()).minusDays(1);
        previousMarketOpen = ZonedDateTime.of(previousDayDateTime.getYear(),
            previousDayDateTime.getMonth().getValue(), previousDayDateTime.getDayOfMonth(),
            info.getOpenHour(), info.getOpenMinute(), 0, 0,
            previousDayDateTime.getZone()).minusDays(1);
        orb5minOpen = ZonedDateTime.of(localDateTime.getYear(), localDateTime.getMonth().getValue(),
            localDateTime.getDayOfMonth(), info.getOrb5MinOpenHour(), 0, 0, 0,
            localDateTime.getZone());
        break;
      case SATURDAY:
        return Optional.empty();
      case SUNDAY:
        previousDayDateTime = localDateTime.minusDays(2);
        marketOpen = ZonedDateTime.of(localDateTime.getYear(), localDateTime.getMonth().getValue(),
            localDateTime.getDayOfMonth(), info.getOpenHour(), info.getOpenMinute(), 0, 0,
            localDateTime.getZone());
        previousMarketOpen = ZonedDateTime.of(previousDayDateTime.getYear(),
            previousDayDateTime.getMonth().getValue(), previousDayDateTime.getDayOfMonth(),
            info.getOpenHour(), info.getOpenMinute(), 0, 0, previousDayDateTime.getZone())
            .minusDays(1);
        orb5minOpen = ZonedDateTime.of(localDateTime.getYear(), localDateTime.getMonth().getValue(),
            localDateTime.getDayOfMonth(), info.getOrb5MinOpenHour(), 0, 0, 0,
            localDateTime.getZone())
            .plusDays(1);
        break;
      default:
        throw new IllegalArgumentException(String.format("Unexpected day [%s] for [%s].",
            localDateTime.getDayOfWeek(), localDateTime));
    }
    return Optional.of(new MarketHours(
        DateTimeUtils.getUtcMilliseconds(marketOpen),
        DateTimeUtils.getUtcMilliseconds(marketOpen.plusDays(1)),
        DateTimeUtils.getUtcMilliseconds(orb5minOpen),
        DateTimeUtils.getUtcMilliseconds(getOrb5MinClose(orb5minOpen)),
        DateTimeUtils.getUtcMilliseconds(previousMarketOpen),
        DateTimeUtils.getUtcMilliseconds(previousMarketOpen.plusDays(1))));
  }

  private static ZonedDateTime getOrb5MinClose(ZonedDateTime orb5MinOpen) {
    return orb5MinOpen.plusMinutes(5);
  }
}
