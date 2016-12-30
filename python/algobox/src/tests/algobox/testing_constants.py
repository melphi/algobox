from datetime import datetime, timezone
from os.path import dirname

from algobox.client.generated.api.models.price_ohlc import PriceOhlc
from algobox.price import PriceTick
from algobox.price.pricefeed import LocalFilePriceFeed


class TestingConstants(object):
    _DIRECTORY_RESOURCES = dirname(__file__) + '/../resources/'

    DEFAULT_CONNECTOR_ID = 'connectorId'
    DEFAULT_DATE = datetime(2016, 2, 22, 9, 0, tzinfo=timezone.utc)
    DEFAULT_TIMESTAMP = int(DEFAULT_DATE.timestamp() * 1000)
    DEFAULT_INSTRUMENT_ID_EURUSD = 'EURUSD'
    DEFAULT_INSTRUMENT_ID_DAX = 'DAX'
    DEFAULT_INFRA_DAY_FROM_TIMESTAMP_UTC = int(DEFAULT_DATE.timestamp() * 1000)
    DEFAULT_INFRA_DAY_TO_TIMESTAMP_UTC = int(datetime(
        2016, 2, 22, 11, 0, tzinfo=timezone.utc).timestamp() * 1000)

    DEFAULT_PRICE_TICK = PriceTick(
        DEFAULT_INSTRUMENT_ID_EURUSD,
        DEFAULT_INFRA_DAY_FROM_TIMESTAMP_UTC,
        1.1162,
        1.1161)
    DEFAULT_TRADING_ORDER_ID_1 = 'tradingOrderId1'

    DEFAULT_OPENING_RANGE_BAR = PriceOhlc(DEFAULT_INSTRUMENT_ID_DAX, 9490,
                                          9489, 9499, 9498, 9488, 9487, 9498,
                                          9497)
    DEFAULT_PREVIOUS_DAY_BAR = PriceOhlc(DEFAULT_INSTRUMENT_ID_DAX, 9570, 9569,
                                         9879, 9878, 9568, 9567, 9578, 9577)

    FILE_SAMPLE_DAX_TICKS = _DIRECTORY_RESOURCES + \
        'dax_ticks_small_sample.csv'
    FILE_SAMPLE_DAX_TICKS_NUMERIC_DATE = _DIRECTORY_RESOURCES + \
        'dax_ticks_small_sample_numeric_date.csv'
    FILE_SAMPLE_DAX_TICKS_WITH_PREMARKET = _DIRECTORY_RESOURCES + \
        'dax_ticks_small_sample_with_premarket.csv'
    FILE_SAMPLE_DAX_TICKS_TWO_DAYS = _DIRECTORY_RESOURCES + \
        'dax_ticks_small_sample_two_days.csv'
    FILE_SAMPLE_DAX_TICKS_MORNING = _DIRECTORY_RESOURCES + \
        'dax_ticks_morning_22.02.2016.csv'
    FILE_SAMPLE_DAX_15M_BARS = _DIRECTORY_RESOURCES + \
        'dax_15min_22.02.2016.csv'

    SAMPLE_PRICES_FEED_DAX = LocalFilePriceFeed(
        DEFAULT_INSTRUMENT_ID_DAX, FILE_SAMPLE_DAX_TICKS)
    SAMPLE_PRICES_FEED_DAX_WITH_PREMARKET = LocalFilePriceFeed(
        DEFAULT_INSTRUMENT_ID_DAX, FILE_SAMPLE_DAX_TICKS_WITH_PREMARKET)
    SAMPLE_PRICES_FEED_DAX_TWO_DAYS = LocalFilePriceFeed(
        DEFAULT_INSTRUMENT_ID_DAX, FILE_SAMPLE_DAX_TICKS_TWO_DAYS)
    SAMPLE_PRICES_FEED_DAX_MORNING = LocalFilePriceFeed(
        DEFAULT_INSTRUMENT_ID_DAX, FILE_SAMPLE_DAX_TICKS_MORNING)
    SAMPLE_PRICES_FEED_EURUSD = LocalFilePriceFeed(
        DEFAULT_INSTRUMENT_ID_EURUSD, FILE_SAMPLE_DAX_TICKS)
