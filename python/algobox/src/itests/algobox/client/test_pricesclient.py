from algobox.client import ApiClient
from algobox.price import PriceTick
from algobox.util.preconditions import Preconditions
from itests.algobox import IntegrationTestingConstants
from unittest import TestCase


class TestPricesClient(TestCase):
    def setUp(self):
        api_client = ApiClient(
            IntegrationTestingConstants.DEFAULT_API_SERVICE_URL)
        self._client = api_client.prices_client

    def test_get_prices(self):
        results = self._client.get_price_ticks(
            IntegrationTestingConstants.DEFAULT_INSTRUMENT_ID_EURUSD,
            IntegrationTestingConstants.DEFAULT_INFRA_DAY_FROM_TIMESTAMP_UTC,
            IntegrationTestingConstants.DEFAULT_INFRA_DAY_TO_TIMESTAMP_UTC)
        self.assertIsInstance(results, list)
        self.assertGreater(len(results), 0)
        for result in results:
            self.assertIsInstance(result, PriceTick)

    def test_get_prices_ndarray(self):
        result = self._client.get_price_ticks_ndarray(
            IntegrationTestingConstants.DEFAULT_INSTRUMENT_ID_EURUSD,
            IntegrationTestingConstants.DEFAULT_INFRA_DAY_FROM_TIMESTAMP_UTC,
            IntegrationTestingConstants.DEFAULT_INFRA_DAY_TO_TIMESTAMP_UTC)
        self.assertIsNotNone(Preconditions.check_prices_array(result))
