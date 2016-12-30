from unittest import TestCase

from algobox.analysis import QuickAnalysis
from tests.algobox import TestingApiClient
from tests.algobox import TestingConstants


class TestQuickAnalysis(TestCase):
    def setUp(self):
        self._analysis = QuickAnalysis(algobox_client=TestingApiClient())

    def test_get_day_overview(self):
        overview = self._analysis.day_overview(
            instrument_id=TestingConstants.DEFAULT_INSTRUMENT_ID_DAX,
            date=TestingConstants.DEFAULT_DATE)
        self.assertIsNotNone(overview.plot)
        self.assertGreater(overview.prices.size, 0)
        overview.plot.show()
