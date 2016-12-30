from unittest import TestCase

import matplotlib.pyplot as plt

from algobox.analysis.indicator import OhlcPattern
from algobox.analysis.plot import OverviewPlotBuilder
from algobox.price.pricefactory import PriceFactory
from . import TestingConstants


class TestOverviewPlotBuilder(TestCase):
    def test_build(self):
        prices = PriceFactory.create_prices_ndarray(
            TestingConstants.SAMPLE_PRICES_FEED_DAX_MORNING.get_prices())
        opening_range_ohlc = TestingConstants.DEFAULT_OPENING_RANGE_BAR
        previous_day_ohlc = TestingConstants.DEFAULT_PREVIOUS_DAY_BAR
        plot = OverviewPlotBuilder().with_prices(prices)\
            .with_ohlc_patterns(OhlcPattern)\
            .with_opening_range_bar(opening_range_ohlc) \
            .with_previous_day_bar(previous_day_ohlc) \
            .build()
        self.assertIsInstance(plot, plt.Figure)
