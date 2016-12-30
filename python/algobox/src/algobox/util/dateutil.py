from algobox.price import StandardTimeFrame
from algobox.util.preconditions import Preconditions
from datetime import datetime, timezone


def _get_seconds_in_time_frame(time_frame):
    """
    Args:
        time_frame (algobox.price.StandardTimeFrame): Time frame.

    Returns:
        int: Seconds in time frame.
    """
    if time_frame == StandardTimeFrame.M5:
        return 60 * 5
    elif time_frame == StandardTimeFrame.M15:
        return 60 * 15
    else:
        raise ValueError('Unsupported time frame [%s].' % time_frame)


class TimestampUtil(object):
    @staticmethod
    def get_begin_of_day(date):
        """
        Args:
            date (datetime): The date.

        Returns:
            int: The resulting timestamp.
        """
        return int(datetime(date.year, date.month, date.day,
                            tzinfo=timezone.utc).timestamp() * 1000.0)

    @staticmethod
    def get_end_of_day_from_timestamp(timestamp_utc):
        """
        Args:
            timestamp_utc (int): The timestamp in UTC

        Returns:
            int: The resulting timestamp.
        """

        date = datetime.utcfromtimestamp(timestamp_utc / 1000.0)
        return TimestampUtil.get_end_of_day(date)

    @staticmethod
    def get_end_of_day(date):
        """
        Args:
            date (datetime): The date

        Returns:
            int: The resulting timestamp.
        """
        return int(datetime(date.year, date.month, date.day, 23, 59, 59,
                            999999, tzinfo=timezone.utc).timestamp() * 1000.0)

    @staticmethod
    def get_timestamp_of(date, hours, minutes):
        """
        Args:
            date (datetime): The date
            hours (int): The hours to be set
            minutes (int): The minutes to be set

        Returns:
            int: The resulting timestamp.
        """
        return int(datetime(date.year, date.month, date.day, hours, minutes,
                            tzinfo=timezone.utc).timestamp() * 1000.0)

    @staticmethod
    def get_frame_start(timestamp_utc, time_frame):
        """
        Args:
            timestamp_utc (int): The timestamp.
            time_frame (algobox.price.StandardTimeFrame): The time frame.

        Returns:
            int: The frame opening timestamp.
        """
        Preconditions.check_timestamp(timestamp_utc)
        seconds_in_time_frame = _get_seconds_in_time_frame(time_frame)
        times = int(timestamp_utc / 1000.0 / seconds_in_time_frame)
        return int(seconds_in_time_frame * times * 1000.0)

    @staticmethod
    def get_frame_end(timestamp_utc, time_frame):
        """
        Args:
            timestamp_utc (int): The timestamp.
            time_frame (algobox.price.StandardTimeFrame): The time frame.

        Returns:
            int: The frame closing timestamp.
        """
        assert timestamp_utc > 0
        seconds_in_time_frame = _get_seconds_in_time_frame(time_frame)
        times = int(timestamp_utc / 1000.0 / seconds_in_time_frame) + 1
        return int(seconds_in_time_frame * times * 1000.0) - 1

    @staticmethod
    def get_timestamp(date):
        """
        Args:
            date (datetime)

        Returns:
            long: The timestamp in milliseconds.
        """
        return int(date.timestamp() * 1000.0)


class DateUtil(object):
    @staticmethod
    def is_friday(date):
        """
        Args:
            date (datetime): The date

        Returns:
            bool
        """
        return date.weekday() == 4

    @staticmethod
    def is_saturday(date):
        """
        Args:
            date (datetime): The date

        Returns:
            bool
        """
        return date.weekday() == 5

    @staticmethod
    def is_sunday(date):
        """
        Args:
            date (datetime): The date

        Returns:
            bool
        """
        return date.weekday() == 6
