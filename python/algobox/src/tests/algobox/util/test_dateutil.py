from algobox.price import StandardTimeFrame
from algobox.util.dateutil import TimestampUtil
from datetime import datetime
from unittest import TestCase


class TestTimestampUtil(TestCase):
    _DEFAULT_TIMESTAMP_INFRA = int(
        datetime(2016, 1, 12, 9, 9, 1).timestamp() * 1000.0)
    _DEFAULT_TIMESTAMP_OPEN = int(
        datetime(2016, 1, 12, 9, 0, 0).timestamp() * 1000.0)
    _DEFAULT_TIMESTAMP_CLOSE = int(
        datetime(2016, 1, 12, 9, 14, 59, 999999).timestamp() * 1000.0)

    def test_should_get_frame_start_15m_infra_bar(self):
        expected = int(
            datetime(2016, 1, 12, 9, 0, 0).timestamp() * 1000.0)
        actual = TimestampUtil.get_frame_start(
            self._DEFAULT_TIMESTAMP_INFRA, StandardTimeFrame.M15)
        assert actual == expected

    def test_should_get_frame_start_15m_at_open(self):
        expected = int(
            datetime(2016, 1, 12, 9, 0, 0).timestamp() * 1000.0)
        actual = TimestampUtil.get_frame_start(
            self._DEFAULT_TIMESTAMP_OPEN, StandardTimeFrame.M15)
        assert actual == expected

    def test_should_get_frame_start_15m_at_close(self):
        expected = int(
            datetime(2016, 1, 12, 9, 0, 0).timestamp() * 1000.0)
        actual = TimestampUtil.get_frame_start(
            self._DEFAULT_TIMESTAMP_CLOSE, StandardTimeFrame.M15)
        assert actual == expected

    def test_should_get_frame_end_15m_infra_bar(self):
        expected = int(
            datetime(2016, 1, 12, 9, 14, 59, 999999).timestamp() * 1000.0)
        actual = TimestampUtil.get_frame_end(
            self._DEFAULT_TIMESTAMP_INFRA, StandardTimeFrame.M15)
        assert actual == expected

    def test_should_get_frame_end_15m_at_open(self):
        expected = int(
            datetime(2016, 1, 12, 9, 14, 59, 999999).timestamp() * 1000.0)
        actual = TimestampUtil.get_frame_end(
            self._DEFAULT_TIMESTAMP_OPEN, StandardTimeFrame.M15)
        assert actual == expected

    def test_should_get_frame_end_15m_at_close(self):
        expected = int(
            datetime(2016, 1, 12, 9, 14, 59, 999999).timestamp() * 1000.0)
        actual = TimestampUtil.get_frame_end(
            self._DEFAULT_TIMESTAMP_CLOSE, StandardTimeFrame.M15)
        assert actual == expected
