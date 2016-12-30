import numpy as np
import pandas as pd

from algobox.price import StandardTimeFrame


class PriceFactory(object):
    @staticmethod
    def create_ohlc_matrix(prices, time_frame):
        """Creates ohlc matrix.

        Args:
            prices (numpy.ndarray): Matrix of prices
            time_frame (algobox.price.StandardTimeFrame): Timeframe
        """
        assert type(prices) == np.ndarray
        if time_frame == StandardTimeFrame.M15:
            rule = '15min'
        elif time_frame == StandardTimeFrame.M5:
            rule = '5min'
        else:
            raise ValueError('Unsupported time frame [%r].' % time_frame)
        index = pd.to_datetime(prices[:, 0], unit="ms")
        series = pd.Series(index=index, data=prices[:, 1])
        return series.resample(rule).ohlc()

    @staticmethod
    def create_prices_ndarray(prices):
        """Creates a ndarray with the price data. The ndarray contains the
         columns time, ask, bid.

         Args:
            prices (list of algobox.price.PriceTick): The price ticks.
        """
        values = [[price.time, price.ask, price.bid]
                  for price in prices]
        return np.array(values) if values else None
