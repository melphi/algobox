from unittest import TestCase

from algobox.client import ApiClient
from algobox.analysis import QuickAnalysis
from itests.algobox import IntegrationTestingConstants


class TestQuickAnalysis(TestCase):
    def setUp(self):
        self._analysis = QuickAnalysis(algobox_client=ApiClient(
            IntegrationTestingConstants.DEFAULT_API_SERVICE_URL))

    def test_get_day_overview(self):
        overview = self._analysis.day_overview(
            instrument_id=
            IntegrationTestingConstants.DEFAULT_INSTRUMENT_ID_DAX,
            date=
            IntegrationTestingConstants.DEFAULT_DATE)
        self.assertIsNotNone(overview.plot)
        self.assertGreater(overview.prices.size, 0)
        self.assertIsNotNone(overview.opening_5min_bar)
        self.assertGreater(overview.opening_5min_bar.low_ask, 0)
        self.assertIsNotNone(overview.previous_day_bar)
