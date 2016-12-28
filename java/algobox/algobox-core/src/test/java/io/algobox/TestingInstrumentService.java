package io.algobox;

import com.google.common.collect.ImmutableMap;
import io.algobox.instrument.InstrumentInfoDetailed;
import io.algobox.instrument.InstrumentService;
import io.algobox.instrument.MarketHours;
import io.algobox.util.DateTimeUtils;
import io.algobox.util.MarketHoursUtils;
import io.algobox.util.MorePreconditions;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

public class TestingInstrumentService implements InstrumentService, Serializable {
  public static final String INSTRUMENT_DAX = "DAX";

  private static final Map<String, InstrumentInfoDetailed> INSTRUMENTS_INFO =
      ImmutableMap.<String, InstrumentInfoDetailed>builder()
          .put(INSTRUMENT_DAX,
              new InstrumentInfoDetailed("DAX", 9, 0, 17, 45, false, 9, 0, "Europe/Berlin"))
          .build();

  @Override
  public InstrumentInfoDetailed getInstrumentInfo(String instrumentId) {
    MorePreconditions.checkNotNullOrEmpty(instrumentId);
    InstrumentInfoDetailed instrumentInfo = INSTRUMENTS_INFO.get(instrumentId);
    if (instrumentInfo == null) {
      throw new IllegalArgumentException("Instrument not found.");
    }
    return instrumentInfo;
  }

  @Override
  public Optional<MarketHours> getMarketHours(String instrumentId, long timestampUtc) {
    InstrumentInfoDetailed info = checkNotNull(getInstrumentInfo(instrumentId));
    ZonedDateTime localDateTime = DateTimeUtils.getDateTime(timestampUtc)
        .withZoneSameInstant(ZoneId.of(info.getTimeZoneId()));
    return Boolean.TRUE.equals(info.getIs24hMarket())
        ? MarketHoursUtils.getMarketHours24HoursMarket(info, localDateTime)
        : MarketHoursUtils.getMarketHoursLocalMarket(info, localDateTime);
  }

  @Override
  public Optional<MarketHours> getMarketHoursYesterday(String instrumentId) {
    long yesterday = DateTimeUtils.getCurrentUtcTimestampMilliseconds() - (24 * 60 * 60 * 1000);
    return getMarketHours(instrumentId, yesterday);
  }
}
