from numpy import dtype, ndarray


class Preconditions(object):
    @staticmethod
    def check_timestamp(timestamp_utc):
        """
        Args:
            timestamp_utc (long)

        Returns:
            long: The same timestamp passed as parameter.
        """
        assert timestamp_utc > 1000000000000, 'Invalid timestamp'
        return timestamp_utc

    @staticmethod
    def check_prices_array(prices):
        """
        Args:
            prices (numpy.ndarray): A prices array with 3 float columns
                (timestamp, ask, bid)

        Returns:
            numpy.ndarray: The same prices array passed as parameter.
        """
        assert isinstance(prices, ndarray)
        assert prices.shape[1] >= 3, 'Invalid shape'
        assert prices.dtype == dtype('float64')
        return prices
