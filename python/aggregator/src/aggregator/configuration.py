from requests import request


class Configuration(object):
    def __init__(self, config_api_url):
        """
        The base api url from which the parameters are loaded, eg from a
        Consul or etcd service.

        Args:
            config_api_url (str)
        """
        self._config_api_url = config_api_url

    def get_parameters(self):
        return {}
