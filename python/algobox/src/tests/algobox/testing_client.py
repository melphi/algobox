from algobox.client import ApiClient
from algobox.client.generated.api.apis import IndicatorsApi, InstrumentsApi, \
    PricesApi
from algobox.client.generated.api.models import InstrumentInfoDetailed, \
    MarketHours, PriceOhlc
from algobox.util.preconditions import Preconditions
from numpy import array
from . import TestingConstants


class _TestingClientConstants(object):
    DEFAULT_MARKET_HOURS_DAX = MarketHours(
        market_open=1456128000000,
        market_close=1456159500000,
        orb5min_open=1456128000000,
        orb5min_close=1456128300000,
        previous_market_open=1455868800000,
        previous_market_close=1455900300000)
    DEFAULT_MARKET_HOURS_EURUSD = MarketHours(
        market_open=1456092000000,
        market_close=1456178400000,
        orb5min_open=1456146000000,
        orb5min_close=1456146300000,
        previous_market_open=1455832800000,
        previous_market_close=1455919200000)


class TestingInstrumentsApi(InstrumentsApi):
    def __init__(self):
        super().__init__()

    _MARKET_HOURS = {
        'DAX': {str(TestingConstants.DEFAULT_TIMESTAMP):
                _TestingClientConstants.DEFAULT_MARKET_HOURS_DAX,
                '1456128000065': MarketHours(market_open=1456128000000,
                                             market_close=1456159500000,
                                             orb5min_open=1456128000000,
                                             orb5min_close=1456128300000,
                                             previous_market_open=
                                             1455868800000,
                                             previous_market_close=
                                             1455900300000),
                '1456127940065': MarketHours(market_open=1456128000000,
                                             market_close=1456159500000,
                                             orb5min_open=1456128000000,
                                             orb5min_close=1456128300000,
                                             previous_market_open=
                                             1455868800000,
                                             previous_market_close=
                                             1455900300000),
                '1456214340065': MarketHours(market_open=1456214400000,
                                             market_close=1456245900000,
                                             orb5min_open=1456214400000,
                                             orb5min_close=1456214700000,
                                             previous_market_open=
                                             1456128000000,
                                             previous_market_close=
                                             1456159500000),
                '1456124400065': MarketHours(market_open=1456128000000,
                                             market_close=1456159500000,
                                             orb5min_open=1456128000000,
                                             orb5min_close=1456128300000,
                                             previous_market_open=
                                             1456128000000,
                                             previous_market_close=
                                             1456159500000),
                '1456045200000': None},
        'EURUSD': {str(TestingConstants.DEFAULT_TIMESTAMP):
                   _TestingClientConstants.DEFAULT_MARKET_HOURS_DAX}}

    _INSTRUMENT_INFO = {
        'MARKET:CS.D.EURUSD.TODAY.IP': InstrumentInfoDetailed(
            instrument_id='MARKET:CS.D.EURUSD.TODAY.IP', open_hour=17,
            open_minute=0, close_hour=17, close_minute=0, is24h_market=True,
            orb5_min_open_hour=8, pips_decimals=4,
            time_zone_id='America/New_York'),
        'MARKET:IX.D.DAX.DAILY.IP': InstrumentInfoDetailed(
            instrument_id='MARKET:IX.D.DAX.DAILY.IP', open_hour=9,
            open_minute=0, close_hour=17, close_minute=45, is24h_market=False,
            orb5_min_open_hour=9, pips_decimals=0,
            time_zone_id='Europe/Berlin'),
        'EURUSD': InstrumentInfoDetailed(
            instrument_id='MARKET:CS.D.EURUSD.TODAY.IP', open_hour=17,
            open_minute=0, close_hour=17, close_minute=0, is24h_market=True,
            orb5_min_open_hour=8, pips_decimals=4,
            time_zone_id='America/New_York'),
        'DAX': InstrumentInfoDetailed(
            instrument_id='MARKET:IX.D.DAX.DAILY.IP', open_hour=9,
            open_minute=0, close_hour=17, close_minute=45, is24h_market=False,
            orb5_min_open_hour=9, pips_decimals=0,
            time_zone_id='Europe/Berlin')}

    def get_instrument_info(self, instrument_id, **kwargs):
        """
        Returns:
            algobox.client.generated.api.models.InstrumentInfoDetailed
        """
        return self._INSTRUMENT_INFO[instrument_id]

    def get_market_hours(self, instrument_id, timestamp, **kwargs):
        """
        Returns:
            algobox.client.generated.api.models.MarketHours
        """
        Preconditions.check_timestamp(timestamp)
        info = self.get_instrument_info(instrument_id)
        if not info:
            raise ValueError('Instrument [%s] not found.' % instrument_id)
        return self._MARKET_HOURS[instrument_id][str(timestamp)]


class TestingIndicatorsApi(IndicatorsApi):
    def __init__(self):
        super().__init__()

    def get_ohlc(self, instrument_id, **kwargs):
        return PriceOhlc(instrument=instrument_id, ask_open=9470,
                         bid_open=9471, ask_high=9500, bid_high=9501,
                         ask_low=9430, bid_low=9431, ask_close=9475,
                         bid_close=9476)


class TestingPricesApi(PricesApi):
    def __init__(self):
        super().__init__()

    def get_price_ticks(
            self, instrument_id, from_timestamp, to_timestamp, **kwargs):
        if instrument_id == TestingConstants.DEFAULT_INSTRUMENT_ID_DAX:
            assert from_timestamp == _TestingClientConstants \
                .DEFAULT_MARKET_HOURS_DAX.market_open
            assert to_timestamp == _TestingClientConstants \
                .DEFAULT_MARKET_HOURS_DAX.market_close
            return TestingConstants.SAMPLE_PRICES_FEED_DAX.get_prices()
        elif instrument_id == TestingConstants.DEFAULT_INSTRUMENT_ID_EURUSD:
            assert from_timestamp == _TestingClientConstants \
                .DEFAULT_MARKET_HOURS_EURUSD.market_open
            assert to_timestamp == _TestingClientConstants \
                .DEFAULT_MARKET_HOURS_EURUSD.market_close
            return TestingConstants.SAMPLE_PRICES_FEED_EURUSD.get_prices()
        else:
            ValueError('Unsupported instrument')

    def get_price_ticks_ndarray(
            self, instrument_id, from_timestamp, to_timestamp):
        ticks = self.get_price_ticks(
            instrument_id, from_timestamp, to_timestamp)
        values = [[x.time, x.ask, x.bid] for x in ticks]
        return array(values) if values else None


class TestingApiClient(ApiClient):
    def __init__(self, api_url="test"):
        super().__init__(api_url)

    @property
    def prices_client(self):
        """
        Returns:
            algobox.client.generated.api.apis.PricesApi
        """
        return TestingPricesApi()

    @property
    def instruments_client(self):
        """
        Returns:
            algobox.client.generated.api.apis.InstrumentsApi
        """
        return TestingInstrumentsApi()

    @property
    def indicators_client(self):
        """
        Returns:
            algobox.client.generated.api.apis.IndicatorsApi
        """
        return TestingIndicatorsApi()
