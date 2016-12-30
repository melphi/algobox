from datetime import datetime

from algobox.analysis.plot import OverviewPlotBuilder
from algobox.client.generated.api.rest import ApiException


class OverviewWrapper(object):
    """Contains overview data and plot."""

    def __init__(self, plot, prices, opening_5min_bar, previous_day_bar):
        """
        Arguments:
            plot (matplotlib.figure.Figure): The plot object
            prices (numpy.ndarray): The prices
            opening_5min_bar (algobox.price.PriceBar)
            previous_day_bar (algobox.price.PriceBar)
        """
        self.plot = plot
        self.prices = prices
        self.opening_5min_bar = opening_5min_bar
        self.previous_day_bar = previous_day_bar


class QuickAnalysis(object):
    def __init__(self, algobox_client):
        """
        Args:
            algobox_client (algobox.client.AlgoboxClient): The algobox client
        """
        self._prices_client = algobox_client.prices_client
        self._instruments_client = algobox_client.instruments_client
        self._indicators_client = algobox_client.indicators_client

    def _get_ohlc_if_exists(self, instrument_id, from_timestamp, to_timestamp):
        """
        Args:
            instrument_id (str)
            from_timestamp (long)
            to_timestamp (long)
        Returns:
            algobox.price.PriceBar: The price bar or None
        """
        try:
            return self._indicators_client.get_ohlc(
                instrument_id=instrument_id,
                from_timestamp=from_timestamp,
                to_timestamp=to_timestamp)
        except ApiException:
            return None

    def day_overview(self, *, instrument_id, date):
        """Returns an overview (plot and data) of the day.

        Args:
            instrument_id (str): The instrument id.
            date (datetime): Any date of the day.

        Returns:
            algobox.analysis.OverviewWrapper: The overview object. Returns
                None if no data was found.
        """
        assert instrument_id
        assert type(date) == datetime
        timestamp_utc = int(date.timestamp() * 1000.0)
        market_hours = self._instruments_client.get_market_hours(
            instrument_id=instrument_id, timestamp=timestamp_utc)
        if market_hours is None or market_hours.market_open is None:
            return None
        prices = self._prices_client.get_price_ticks_ndarray(
            instrument_id, market_hours.market_open, market_hours.market_close)
        if prices is None or prices.size <= 0:
            return None
        opening_range_bar = self._get_ohlc_if_exists(
            instrument_id, market_hours.orb5min_open,
            market_hours.orb5min_close)
        previous_day_bar = self._get_ohlc_if_exists(
            instrument_id, market_hours.previous_market_open,
            market_hours.previous_market_close)
        plot = OverviewPlotBuilder().with_prices(prices) \
            .with_titles([instrument_id, date.strftime('%a %d-%m-%y')])\
            .with_opening_range_bar(opening_range_bar) \
            .with_previous_day_bar(previous_day_bar) \
            .build()
        return OverviewWrapper(prices=prices, plot=plot,
                               opening_5min_bar=opening_range_bar,
                               previous_day_bar=previous_day_bar)
