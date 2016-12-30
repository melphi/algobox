from unittest import TestCase

import numpy as np

from algobox.analysis.indicator import OhlcIndicators, OhlcPattern
from algobox.price import StandardTimeFrame
from algobox.price.pricefactory import PriceFactory
from tests.algobox import TestingConstants


class TestOhlcIndicators(TestCase):
    def test_get_ohlc_pattern(self):
        prices = TestingConstants.SAMPLE_PRICES_FEED_DAX_MORNING.get_prices()
        array = PriceFactory.create_prices_ndarray(prices)
        ohlc = PriceFactory.create_ohlc_matrix(array, StandardTimeFrame.M15)
        pattern = OhlcIndicators.get_ohlc_patterns(
            opens=ohlc['open'].values, highs=ohlc['high'].values,
            lows=ohlc['low'].values, closes=ohlc['close'].values,
            patterns=OhlcPattern)
        self.assertEquals(16, len(pattern))
        self.assertIn(OhlcPattern.DOJI, pattern[14])

    def test_get_ohlc(self):
        prices = np.array([0.001, 0.0001, 0.01, 0.0001])
        ohlc = OhlcIndicators.get_ohlc(prices)
        self.assertEqual(0.001, ohlc.open)
        self.assertEqual(0.01, ohlc.high)
        self.assertEqual(0.0001, ohlc.low)
        self.assertEqual(0.0001, ohlc.close)
