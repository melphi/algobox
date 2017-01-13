from numpy import ndarray


class PricesDao(object):
    def __init__(self, connection_url, database, credentials):
        """
        Args:
            connection_url (str)
            database (str)
            credentials (tuple of (str, str))
        """

    def get_prices_stage(self):
        """
        Returns:
            list: An list of PriceTick
        """
        return []
