from algobox.price import Ohlc
from enum import Enum

import talib


class OhlcPattern(Enum):
    DOJI = 'DOJI'
    DRAGONFLY_DOJI = 'DRAGONFLY_DOJI'
    GRAVESTONE_DOJI = 'GRAVESTONE_DOJI'
    HAMMER = 'HAMMER'
    HANGING_MAN = 'HANGING_MAN'
    INVERTED_HAMMER = 'INVERTED_HAMMER'
    LONG_LEGGED_DOJI = 'LONG_LEGGED_DOJI'
    SHOOTING_STAR = 'SHOOTING_STAR'


class OhlcIndicators(object):
    _OHLC_PATTERNS_FUNCTIONS = {
        'DOJI': talib.CDLDOJI,
        'DRAGONFLY_DOJI': talib.CDLDRAGONFLYDOJI,
        'GRAVESTONE_DOJI': talib.CDLGRAVESTONEDOJI,
        'HAMMER': talib.CDLHAMMER,
        'HANGING_MAN': talib.CDLHANGINGMAN,
        'INVERTED_HAMMER': talib.CDLINVERTEDHAMMER,
        'LONG_LEGGED_DOJI': talib.CDLLONGLEGGEDDOJI,
        'SHOOTING_STAR': talib.CDLSHOOTINGSTAR}

    @staticmethod
    def get_ohlc_patterns(*, opens, highs, lows, closes, patterns=list()):
        """Returns the ohlc patterns if any. The result is a positional list,
        the position matches the candle position and the value is the
        candlestick pattern name if any, otherwise None.

        Args:
            opens (numpy.ndarray of float)
            highs (numpy.ndarray of float)
            lows (numpy.ndarray of float)
            closes (numpy.ndarray of float)
            patterns (list of algobox.analysis.indicator.OhlcPattern)

        Returns:
            list of str: A list containing the eventual pattern
                name for the candle of the positional index."""
        result = [[] for x in range(opens.size)]
        for pattern in patterns:
            function = OhlcIndicators._OHLC_PATTERNS_FUNCTIONS[pattern.value]
            items = function(opens, highs, lows, closes)
            for index, value in enumerate(items):
                if value:
                    result[index].append(pattern)
        return result

    @staticmethod
    def get_ohlc(prices):
        """Returns an OHLC from the given price values.

        Args:
            prices (numpy.ndarray): The price values.

        Returns:
            algobox.price.Ohlc
        """
        assert len(prices.shape) == 1, "One dimension only expected."
        return Ohlc(prices.item(0),
                    prices.max(),
                    prices.min(),
                    prices.item(prices.size - 1))
