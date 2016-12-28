package io.algobox.instrument;

import java.util.Optional;

public interface InstrumentService {
  InstrumentInfoDetailed getInstrumentInfo(String instrumentId);

  Optional<MarketHours> getMarketHours(String instrumentId, long timestamp);

  Optional<MarketHours> getMarketHoursYesterday(String instrumentId);
}
