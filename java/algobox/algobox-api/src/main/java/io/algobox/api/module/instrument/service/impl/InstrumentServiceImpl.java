package io.algobox.api.module.instrument.service.impl;

import com.google.common.collect.ImmutableMap;
import io.algobox.api.module.instrument.exception.InstrumentNotFoundException;
import io.algobox.instrument.InstrumentInfoDetailed;
import io.algobox.instrument.InstrumentService;
import io.algobox.instrument.MarketHours;
import io.algobox.util.DateTimeUtils;
import io.algobox.util.MarketHoursUtils;
import org.jvnet.hk2.annotations.Service;

import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;

@Service
public final class InstrumentServiceImpl implements InstrumentService {
  private static final String INSTRUMENT_DAX = "DAX";
  private static final String INSTRUMENT_EURUSD = "EURUSD";
  private static final String INSTRUMENT_GER30 = "GER30";
  private static final String INSTRUMENT_DE30_EUR = "DE30_EUR";
  private static final String INSTRUMENT_DAX_DAILY = "MARKET:IX.D.DAX.DAILY.IP";

  // TODO(robertom): Store instrument info in database.
  private static final Map<String, InstrumentInfoDetailed> INSTRUMENTS_INFO =
      ImmutableMap.<String, InstrumentInfoDetailed>builder()
          .put(INSTRUMENT_EURUSD, new InstrumentInfoDetailed(INSTRUMENT_EURUSD, 17, 0, 17, 0, true,
              8, 4, "America/New_York"))
          .put(INSTRUMENT_GER30, new InstrumentInfoDetailed(INSTRUMENT_GER30, 9, 0, 17, 45, false,
              9, 0, "Europe/Berlin"))
          .put(INSTRUMENT_DAX, new InstrumentInfoDetailed(INSTRUMENT_DAX, 9, 0, 17, 45, false, 9,
              0, "Europe/Berlin"))
          .put(INSTRUMENT_DAX_DAILY, new InstrumentInfoDetailed(INSTRUMENT_DAX_DAILY, 9, 0, 17,
              45, false, 9, 0, "Europe/Berlin"))
          .put(INSTRUMENT_DE30_EUR, new InstrumentInfoDetailed(INSTRUMENT_DE30_EUR, 9, 0, 17, 45,
              false, 9, 0, "Europe/Berlin"))
          .build();

  @Override
  public InstrumentInfoDetailed getInstrumentInfo(String instrumentId) {
    checkNotNullOrEmpty(instrumentId);
    InstrumentInfoDetailed instrumentInfo = INSTRUMENTS_INFO.get(instrumentId);
    if (instrumentInfo == null) {
      throw new InstrumentNotFoundException();
    }
    return instrumentInfo;
  }

  @Override
  public Optional<MarketHours> getMarketHours(String instrumentId, long timestampUtc) {
    InstrumentInfoDetailed info = checkNotNull(getInstrumentInfo(instrumentId));
    return MarketHoursUtils.getMarketHours(info, timestampUtc);
  }

  @Override
  public Optional<MarketHours> getMarketHoursYesterday(String instrumentId) {
    long yesterday = DateTimeUtils.getCurrentUtcTimestampMilliseconds() - (24 * 60 * 60 * 1000);
    return getMarketHours(instrumentId, yesterday);
  }
}
