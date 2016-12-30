from unittest import TestCase

from algobox.price.pricefeed import LocalFilePriceFeed
from tests.algobox import TestingConstants


class TestLocalPriceFeed(TestCase):
    def test_load_price_bars_date_string(self):
        price_feed = LocalFilePriceFeed(
            TestingConstants.DEFAULT_INSTRUMENT_ID_DAX,
            TestingConstants.FILE_SAMPLE_DAX_TICKS)
        for price in price_feed.get_prices():
            self.assertTrue(price)
            return
        self.fail('Should never reach here.')

    def test_load_price_bars_date_numeric(self):
        price_feed = LocalFilePriceFeed(
            TestingConstants.DEFAULT_INSTRUMENT_ID_DAX,
            TestingConstants.FILE_SAMPLE_DAX_TICKS_NUMERIC_DATE)
        for price in price_feed.get_prices():
            self.assertTrue(price)
            return
        self.fail('Should never reach here.')
