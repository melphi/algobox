package io.algobox.api.module.indicator.service.impl;

import io.algobox.api.container.exception.ValueNotFound;
import io.algobox.api.module.indicator.dao.PriceOhlcCacheDao;
import io.algobox.indicator.IndicatorService;
import io.algobox.price.Ohlc;
import io.algobox.price.PriceOhlc;
import io.algobox.price.PriceService;
import io.algobox.price.PriceTick;
import io.algobox.util.MorePreconditions;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;

@Service
public final class IndicatorServiceImpl implements IndicatorService {
  private final PriceOhlcCacheDao priceOhlcCacheDao;
  private final PriceService priceService;

  @Inject
  public IndicatorServiceImpl(PriceOhlcCacheDao priceBarCacheDao, PriceService priceService) {
    this.priceOhlcCacheDao = priceBarCacheDao;
    this.priceService = priceService;
  }

  @Override
  public PriceOhlc getOhlc(String instrumentId, Long fromTimestamp, Long toTimestamp) {
    checkNotNullOrEmpty(instrumentId);
    MorePreconditions.checkTimestamp(fromTimestamp);
    MorePreconditions.checkTimestamp(toTimestamp);
    Optional<Ohlc> cachedOhlc = priceOhlcCacheDao.getPriceOhlc(
        instrumentId, fromTimestamp, toTimestamp);
    if (cachedOhlc.isPresent()) {
      return createPriceOhlc(cachedOhlc.get());
    } else {
      Iterable<PriceTick> priceTicks = priceService.getPriceTicks(
          instrumentId, fromTimestamp, toTimestamp);
      if (!priceTicks.iterator().hasNext()) {
        throw new ValueNotFound("No prices found for the given timestamp.");
      }
      PriceOhlc pricepOhlc = createPriceOhlc(priceTicks, instrumentId);
      priceOhlcCacheDao.saveOrUpdatePriceOhlc(pricepOhlc, fromTimestamp, toTimestamp);
      return pricepOhlc;
    }
  }

  private PriceOhlc createPriceOhlc(Ohlc ohlc) {
    return new PriceOhlc(ohlc.getInstrument(), ohlc.getAskOpen(), ohlc.getBidOpen(),
        ohlc.getAskHigh(), ohlc.getBidHigh(), ohlc.getAskLow(), ohlc.getBidLow(),
        ohlc.getAskClose(), ohlc.getBidClose());
  }

  private PriceOhlc createPriceOhlc(Iterable<PriceTick> priceTicks, String instrumentId) {
    double openAsk = 0;
    double openBid = 0;
    double highAsk = 0;
    double highBid = 0;
    double lowAsk = 0;
    double lowBid = 0;
    double closeAsk = 0;
    double closeBid = 0;
    for (PriceTick priceTick: priceTicks) {
      checkArgument(priceTick.getAsk() > 0);
      checkArgument(priceTick.getBid() > 0);
      if (openAsk == 0) {
        lowAsk = priceTick.getAsk();
        openAsk = priceTick.getAsk();
      }
      if (openBid == 0) {
        lowBid = priceTick.getBid();
        openBid = priceTick.getBid();
      }
      highAsk = Math.max(priceTick.getAsk(), highAsk);
      highBid = Math.max(priceTick.getBid(), highBid);
      lowAsk = Math.min(priceTick.getAsk(), lowAsk);
      lowBid = Math.min(priceTick.getBid(), lowBid);
      closeAsk = priceTick.getAsk();
      closeBid = priceTick.getBid();
    }
    checkArgument(openAsk > 0);
    checkArgument(openBid > 0);
    checkArgument(closeAsk > 0);
    checkArgument(closeBid > 0);
    return new PriceOhlc(
        instrumentId, openAsk, openBid, highAsk, highBid, lowAsk, lowBid, closeAsk, closeBid);
  }
}
