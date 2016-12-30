from algobox.analysis import OverviewPlotBuilder
from algobox.util.preconditions import Preconditions
from algobox.util.dateutil import TimestampUtil


class Sampler(object):
    _CSV_HEADER = 'time,ask,bid\n'

    def __init__(self, prices, from_date, to_date):
        """
        Args:
            prices (numpy.ndarray): The array of prices.
            from_date (datetime.datetime)
            to_date (datetime.datetime)
        """
        Preconditions.check_prices_array(prices)
        from_timestamp = TimestampUtil.get_timestamp(from_date)
        to_timestamp = TimestampUtil.get_timestamp(to_date)
        assert to_timestamp > from_timestamp
        self._data = prices[
            (prices[:, 0] >= from_timestamp) * (prices[:, 0] <= to_timestamp)]

    def save(self, file_name):
        """
        Args:
            file_name (str): The file name where to save the data.
        """
        assert file_name
        if not self._data.size > 0:
            return None
        with open(file_name, 'w') as file:
            file.write(self._CSV_HEADER)
            for value in self._data:
                file.write('%d,%f,%f\n' % (value[0], value[1], value[2]))

    def plot(self):
        """
        Returns:
            matplotlib.figure.Figure
        """
        if not self._data.size > 0:
            return None
        return OverviewPlotBuilder().with_prices(self._data).build()
