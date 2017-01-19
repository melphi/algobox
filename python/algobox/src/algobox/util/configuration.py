from os import environ


def singleton(cls, *args, **kw):
    instances = {}

    def _singleton():
        if cls not in instances:
            instances[cls] = cls(*args, **kw)
        return instances[cls]
    return _singleton


@singleton
class Configuration(object):
    KEY_API_URL = "api.apiUrl"
    KEY_DATACOLLECTOR_URL = "datacollector.apiUrl"

    def get_value(self, key):
        """Returns configuration value for the given key, or None if value was
        not found.

        Arguments:
            key (str)
        Returns:
            str
        """
        assert key, 'Missing key to retrieve parameter value.'
        return environ[key]

    def get_required_value(self, key):
        """Returns configuration value for the given key, or exception if
        value was not found.

        Arguments:
            key (str)
        Returns:
            str
        """
        value = Configuration().get_value(key)
        assert value, 'Required value not found for key [%s].' % key
        return value
