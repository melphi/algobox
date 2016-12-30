from enum import Enum


class Ohlc(object):
    """Open High Low Close."""

    __slots__ = ['open', 'high', 'low', 'close']

    def __init__(self, open, high, low, close):
        self.open = open
        self.high = high
        self.low = low
        self.close = close

    def __str__(self):
        return '%s(open=%r, high=%r, low=%r, close=%r)' % \
               (self.__class__.__name__, self.open, self.high, self.low,
                self.close)


class OhlcBar(object):
    """Open High Low Close."""

    __slots__ = ['open', 'high', 'low', 'close', 'timestamp_utc']

    def __init__(self, open, high, low, close, timestamp_utc):
        self.open = open
        self.high = high
        self.low = low
        self.close = close
        self.timestamp_utc = timestamp_utc


class PriceTick(object):
    """Price tick."""

    __slots__ = ['instrument', 'time', 'ask', 'bid']

    def __init__(self, instrument, time, ask, bid):
        self.instrument = instrument
        self.time = time
        self.ask = ask
        self.bid = bid

    def __str__(self):
        return '%s(instrument=%r, time=%r, ask=%r, bid=%r)' % \
               (self.__class__.__name__, self.instrument,
                self.time, self.ask, self.bid)

    def __repr__(self):
        return self.__str__()


class PriceBar(object):
    """Price bar."""
    __slots__ = ['instrument_id', 'time_frame', 'timestamp_utc', 'open_ask',
                 'open_bid', 'high_ask', 'high_bid', 'low_ask', 'low_bid',
                 'close_ask', 'close_bid']

    def __init__(self, instrument_id, time_frame, timestamp_utc, open_ask,
                 open_bid, high_ask, high_bid, low_ask, low_bid,
                 close_ask, close_bid):
        self.instrument_id = instrument_id
        self.time_frame = time_frame
        self.timestamp_utc = timestamp_utc
        self.open_ask = open_ask
        self.open_bid = open_bid
        self.high_ask = high_ask
        self.high_bid = high_bid
        self.low_ask = low_ask
        self.low_bid = low_bid
        self.close_ask = close_ask
        self.close_bid = close_bid

    def __eq__(self, other):
        if not isinstance(other, self.__class__):
            return False
        for slot in self.__slots__:
            if self.__getattribute__(slot) != other.__getattribute__(slot):
                return False
        return True

    def __str__(self):
        return '%s(instrument_id=%r, time_frame=%r, timestamp_utc=%r, ' \
               'open_ask=%r, open_bid=%r, high_ask=%r, high_bid=%r,' \
               'low_ask=%r, low_bid=%r, close_ask=%r, close_bid=%r)' % \
               (self.__class__.__name__, self.instrument_id, self.time_frame,
                self.timestamp_utc, self.open_ask, self.open_bid,
                self.high_ask, self.high_bid, self.low_ask, self.low_bid,
                self.close_ask, self.close_bid)

    def __repr__(self):
        return self.__str__()


class StandardTimeFrame(Enum):
    """Common time frames."""
    M5 = 'M5'
    M15 = 'M15'
    D1 = 'D1'

    @staticmethod
    def from_text(text):
        for enum in StandardTimeFrame:
            if enum.value is text:
                return enum
        raise ValueError('Unsupported value [%s].' % text)

    @property
    def duration(self):
        """Returns the duration of the time frame in milliseconds or exception
        if conversion is not applicable.

        Returns:
             int: The duration in milliseconds

        Raises:
            ValueError: The time frame con not be converted to milliseconds.
        """
        if self is StandardTimeFrame.M5:
            return 5 * 60 * 1000
        elif self is StandardTimeFrame.M15:
            return 15 * 60 * 1000
        else:
            ValueError('Time frame conversion in milliseconds not supported')
