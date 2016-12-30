from requests import get
from requests.exceptions import RequestException


def singleton(cls, *args, **kw):
    instances = {}

    def _singleton():
        if cls not in instances:
            instances[cls] = cls(*args, **kw)
        return instances[cls]
    return _singleton


@singleton
class Configuration(object):
    _CONSUL_BASE_URL = 'http://127.0.0.1:8500/v1/kv/web/'

    def _has_consul(self):
        try:
            response = get(self._CONSUL_BASE_URL)
            return response.status_code == 200
        except RequestException:
            return False

    def __init__(self):
        self._use_consul = self._has_consul()

    pass
