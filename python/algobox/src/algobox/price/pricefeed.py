from algobox.price import PriceTick
from datetime import datetime, timezone


class PriceFeed(object):
    """Prices provider."""

    def get_prices(self):
        """Returns a generator of prices."""
        pass


class LocalFilePriceFeed(PriceFeed):
    """Prices provider which loads data from a local file."""

    def __init__(self, instrument_id, file_name):
        """
        Args:
            instrument_id (string): The instrument id.
            file_name (string): The file to load prices from.
        """
        self._file_name = file_name
        self._instrument_id = instrument_id
        self._prices_cache = []

    def _create_price_tick(self, line):
        tokens = line.split(',')
        # Date can be either in string format (22.02.2016 07:00:11.660) or
        # numeric (UTC timestmap in milliseconds).
        try:
            timestamp_utc = int(tokens[0])
        except ValueError:
            time = tokens[0][:-4]
            microseconds = int(tokens[0][-3:]) * 1000
            timestamp_utc = int(datetime.strptime(time, '%d.%m.%Y %H:%M:%S')
                                .replace(tzinfo=timezone.utc)
                                .replace(microsecond=microseconds)
                                .timestamp() * 1000.0)
        return PriceTick(self._instrument_id, timestamp_utc, float(tokens[1]),
                         float(tokens[2]))

    def get_prices(self):
        """Reads the file and returns a collection of prices.

        Returns:
            generator of PriceTick
        """
        if not self._prices_cache:
            first_line = True
            with open(self._file_name, 'r') as file:
                for line in file:
                    if first_line:
                        first_line = False
                        continue
                    elif line:
                        self._prices_cache.append(
                            self._create_price_tick(line))
        return self._prices_cache

    def __iter__(self):
        return iter(self.get_prices())
