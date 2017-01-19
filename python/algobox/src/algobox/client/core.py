from algobox.util.configuration import Configuration
from algobox.util.preconditions import Preconditions
from algobox.price import PriceTick
from avro.datafile import DataFileReader
from avro.io import DatumReader
from importlib import import_module
from io import BytesIO
from numpy import array
from requests import get
from types import MethodType

try:
    import snappy
except ImportError as error:
    raise SystemError('Can not load snappy library: [%s].' % error)


def _get_prices_reader(api_url, instrument_id, from_timestamp, to_timestamp):
    """Support method to use the Avro serialisation instead of Json.
    Args:
        api_url (str): The api url
        instrument_id (str): The instrument
        from_timestamp (int): From timestamp in milliseconds UTC
        to_timestamp (int): To timestamp in milliseconds UTC

    Returns
        avro.datafile.DataFileReader
    """
    Preconditions.check_timestamp(from_timestamp)
    Preconditions.check_timestamp(to_timestamp)
    url = '%s/prices/%s/avro' % (api_url, instrument_id)
    parameters = {'fromTimestamp': int(from_timestamp),
                  'toTimestamp': int(to_timestamp)}
    response = get(url, params=parameters)
    assert response.status_code == 200, 'Invalid status [%d]: [%r].' % (
        response.status_code, response.content)
    buffer = BytesIO(response.content)
    return DataFileReader(buffer, DatumReader())


def _get_price_ticks(self, instrument_id, from_timestamp, to_timestamp):
    """Returns the price ticks. Uses Avro serialisation to improve network
        performance.

    Args:
        self: algobox.client.generated.api.apis.prices_api.PricesApi
        instrument_id (str): The instrument
        from_timestamp (int): From timestamp in milliseconds UTC.
        to_timestamp (int): To timestamp in milliseconds UTC.

    Returns
        list of algobox.price.PriceTick: The collection of price ticks
    """
    reader = _get_prices_reader(
        self.api_client.host, instrument_id, from_timestamp, to_timestamp)
    return [PriceTick(x['instrument'], x['time'], x['ask'], x['bid'])
            for x in reader]


def _get_price_ticks_ndarray(
        self, instrument_id, from_timestamp, to_timestamp):
    """Returns the prices in ndarray format (timestamp, ask, bid). Uses Avro
        serialisation to improve network performance.

    Args:
        self: algobox.client.generated.api.apis.prices_api.PricesApi
        instrument_id (str): The instrument.
        from_timestamp (int): From timestamp in milliseconds UTC.
        to_timestamp (int): To timestamp in milliseconds UTC.

    Returns
        numpy.ndarray: Three column ndarray (timestamp, ask, bid).
    """
    reader = _get_prices_reader(
        self.api_client.host, instrument_id, from_timestamp, to_timestamp)
    values = [[x['time'], x['ask'], x['bid']] for x in reader]
    return array(values) if values else None


class _ClientBase(object):
    _API_CLIENT_CLASS = 'ApiClient'

    @staticmethod
    def _get_base_package():
        """Returns the base package."""
        raise NotImplementedError('Implement _get_base_package() first.')

    def __init__(self, api_url=None):
        """Arguments:
            api_url (str): The api url. If None, as default, the api url
                will be retrieved from the environment configuration."""
        base_package = self._get_base_package()
        if base_package is None:
            raise ValueError('Missing base module.')
        if api_url is None:
            raise ValueError('Missing api_url.')
        api_client_class = getattr(
            import_module(base_package + '.api_client'), 'ApiClient')
        self._base_module = base_package
        self._api_client = api_client_class(api_url)
        self._clients = {}

    def _create_client(self, item):
        if not item.endswith('_client'):
            raise ValueError('Client name should end with %s instead of [%s].'
                             % ('_client', item))
        class_name = item.title().replace('_Client', 'Api')
        client_class = getattr(
            import_module('.apis', self._base_module), class_name)
        return client_class(self._api_client)

    def __getattr__(self, item):
        if item not in self._clients:
            self._clients[item] = self._create_client(item)
        return self._clients[item]


class ApiClient(_ClientBase):
    @staticmethod
    def _get_base_package():
        return 'algobox.client.generated.api'

    def __init__(self, api_url):
        """Api client. To get sub-client use the *_client syntax, for example
        self.health_client.

        Arguments:
            api_url (str): The api url. If None, as default, the api url
                will be retrieved from the environment configuration."""
        if api_url is None:
            api_url = Configuration().get_required_value(
                Configuration.KEY_API_URL)
        super().__init__(api_url)
        self._prices_client = self._create_client('prices_client')
        self._prices_client.get_price_ticks_ndarray = MethodType(
            _get_price_ticks_ndarray, self._prices_client)
        self._prices_client.get_price_ticks = MethodType(
            _get_price_ticks, self._prices_client)

    @property
    def prices_client(self):
        return self._prices_client


class DataCollectorClient(_ClientBase):
    @staticmethod
    def _get_base_package():
        return 'algobox.client.generated.datacollector'

    def __init__(self, api_url):
        """DataCollector client. To get sub-client use the *_client syntax,
        for example self.health_client

        Arguments:
            api_url (str): The api url. If None, as default, the api url
                will be retrieved from the environment configuration."""
        if api_url is None:
            api_url = Configuration().get_required_value(
                Configuration.KEY_DATACOLLECTOR_URL)
        super().__init__(api_url)
