from datetime import datetime
from os import path, remove
from unittest import TestCase

from algobox.analysis.sampler import Sampler
from algobox.price.pricefactory import PriceFactory
from tests.algobox import TestingConstants


class TestSampler(TestCase):
    _TMP_FILE_NAME = '/tmp/sample_test.csv'

    def setUp(self):
        prices = TestingConstants.SAMPLE_PRICES_FEED_DAX_MORNING
        prices_array = PriceFactory .create_prices_ndarray(prices)
        from_date = datetime(2016, 2, 22, 9)
        to_date = datetime(2016, 2, 22, 10)
        self._sampler = Sampler(prices_array, from_date, to_date)

    def test_save_in_cvs_format(self):
        try:
            self._sampler.save(self._TMP_FILE_NAME)
            self.assertGreater(path.getsize(self._TMP_FILE_NAME), 0)
        finally:
            remove(self._TMP_FILE_NAME)

    def test_plot(self):
        plot = self._sampler.plot()
        self.assertIsNotNone(plot)
