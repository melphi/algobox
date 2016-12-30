from algobox.analysis.indicator import OhlcIndicators
from algobox.price import StandardTimeFrame
from algobox.price.pricefactory import PriceFactory
from algobox.util.preconditions import Preconditions
from datetime import datetime
from matplotlib.ticker import Formatter

import numpy as np
import matplotlib.pyplot as plt


class _IntradayTimestampFormatter(Formatter):
    """Labels formatter."""

    def __call__(self, x, pos=None):
        time = datetime.fromtimestamp(x / 1000000000.0)
        return '%02d:%02d' % (time.hour, time.minute)


class _BasePlotBuilder(object):
    def __init__(self):
        self._horizontal_lines = []

    def with_horizontal_line(
            self, *, y, color='b', style='--', draw_limits=None):
        """Adds an horizontal line.
        Args:
            y (float): The y value.
            color (str): Color code, default b.
            style (str): Style, default --.
            draw_limits ((float, float)): The optional limits, the line will
                not be plotted if beyond the limits.

        Returns:
            algobox.plot._BasePlotBuilder: The builder.
        """
        if not draw_limits or draw_limits[0] <= y <= draw_limits[1]:
            self._horizontal_lines.append(
                {'y': y, 'color': color, 'linestyle': style})
        return self

    def build(self, axes):
        """Plots the chart on the given axes.

        Args:
            axes (matplotlib.axes.Axes): The axes to plot to.
        """
        for line in self._horizontal_lines:
            axes.axhline(y=line['y'], color=line['color'],
                         linestyle=line['linestyle'])


class _CandlestickPlotBuilder(_BasePlotBuilder):
    """Builds a new candlestick plot."""

    def __init__(self, ohlc_matrix):
        """
        Args:
            ohlc_matrix (numpy.ndarray): The ohlc matrix.
        """
        super().__init__()
        self._ohlc_matrix = ohlc_matrix
        self._ohlc_patterns = []

    def _get_ohcl_patterns(self):
        """Returns the positional list of patterns found."""
        if self._ohlc_patterns:
            opens = self._ohlc_matrix['open'].values
            highs = self._ohlc_matrix['high'].values
            lows = self._ohlc_matrix['low'].values
            closes = self._ohlc_matrix['close'].values
            return OhlcIndicators.get_ohlc_patterns(
                opens=opens, highs=highs, lows=lows, closes=closes,
                patterns=self._ohlc_patterns)
        else:
            return []

    def with_ohlc_patterns(self, patterns):
        """Prints candlestick patterns.

        Args:
            patterns (iterable of algobox.analysis.indicator.OhlcPattern):
                The list of prices ticks.

        Returns:
            algobox.plot._CandlestickPlotBuilder: The builder
        """
        self._ohlc_patterns = list(patterns)
        return self

    def build(self, axes):
        super().build(axes)
        max = self._ohlc_matrix.index.max().value
        min = self._ohlc_matrix.index.min().value
        bar_width = (max - min) / self._ohlc_matrix.shape[0]
        patterns = self._get_ohcl_patterns()
        counter = 0
        for date, ohlc in self._ohlc_matrix.T.iteritems():
            bar_center_x = date.value + (bar_width / 2.0)
            color = 'r' if ohlc.open > ohlc.close else 'g'
            rectangle = plt.Rectangle((date.value, ohlc.open), bar_width,
                                      ohlc.close - ohlc.open, color=color)
            tail = plt.Rectangle((bar_center_x, ohlc.low), 1,
                                 ohlc.high - ohlc.low, color=color)
            axes.add_patch(rectangle)
            axes.add_patch(tail)
            if patterns and patterns[counter]:
                label = "\n".join([x.value for x in patterns[counter]])
                axes.annotate(label, xy=(bar_center_x, ohlc.high + 1),
                              xytext=(bar_center_x, ohlc.high + 8),
                              arrowprops=dict(facecolor='b', headwidth=4))
            counter += 1
        axes.xaxis.set_major_formatter(_IntradayTimestampFormatter())
        axes.autoscale_view()


class _HistogramPlotBuilder(_BasePlotBuilder):
    """Builds a frequency histogram."""

    def __init__(self, prices):
        """
        Args:
            prices (numpy.ndarray): The matrix of prices [timestamp, price]
        """
        super().__init__()
        self._prices = prices[:, 1]

    def build(self, axes):
        super().build(axes)
        axes.get_xaxis().set_visible(False)
        axes.hist(self._prices, bins=60, orientation='horizontal')
        axes.autoscale_view()


class _LinePlotBuilder(_BasePlotBuilder):
    """Builds a line plot."""

    def __init__(self, prices):
        """
        Args:
            prices (numpy.ndarray): The matrix of prices [timestamp, price]
        """
        super().__init__()
        self._prices = prices[:, 1]

    def build(self, axes):
        super().build(axes)
        axes.get_xaxis().set_visible(False)
        axes.plot(self._prices)
        axes.autoscale_view()


class OverviewPlotBuilder(object):
    def __init__(self):
        self._prices = None
        self._ohlc_patterns = []
        self._opening_range_bar = None
        self._previous_day_bar = None
        self._draw_limits = None
        self._titles = None

    def _create_candlestick(self, time_frame):
        """
        Args:
            time_frame (algobox.price.StandardTimeFrame)
        Returns:
            _CandlestickPlotBuilder
        """
        ohlc_matrix = PriceFactory.create_ohlc_matrix(
            self._prices, time_frame=time_frame)
        return _CandlestickPlotBuilder(ohlc_matrix) \
            .with_ohlc_patterns(self._ohlc_patterns)

    def with_titles(self, titles):
        """
        Args:
            titles (list of str)
        Returns:
            algobox.plot.OverviewPlotBuilder
        """
        assert type(titles) is list and len(titles) == 2, \
            'Titles should be a list of 2 values.'
        self._titles = titles
        return self

    def with_prices(self, prices):
        """Matrix of price values.

        Args:
            prices (numpy.ndarray): The matrix of price values with two float
                columns timestamp_utc and price_ask.

        Returns:
            algobox.plot.OverviewPlotBuilder
        """
        self._prices = Preconditions.check_prices_array(prices)
        self._draw_limits = (prices[:, 1].min() - 15.0,
                             prices[:, 1].max() + 15.0)
        return self

    def with_ohlc_patterns(self, patterns=None):
        """Mark the candlestick patterns.

        Args:
            patterns (list of algobox.analysis.indicator.OhlcPattern):
                List of patterns to be included.

        Returns:
            algobox.plot.OverviewPlotBuilder
        """
        self._ohlc_patterns = list(patterns) if patterns else list()
        return self

    def with_opening_range_bar(self, opening_range_bar):
        """Plots the opening range bar.

        Args:
            opening_range_bar (algobox.client.generated.api.models.price_ohlc.PriceOhlc):
                The opening range bar

        Returns:
            algobox.plot.OverviewPlotBuilder
        """
        self._opening_range_bar = opening_range_bar
        return self

    def with_previous_day_bar(self, previous_day_bar):
        """Plots the previous day bar.

        Args:
            previous_day_bar (algobox.client.generated.api.models.price_ohlc.PriceOhlc)

        Returns:
            algobox.plot.OverviewPlotBuilder
        """
        self._previous_day_bar = previous_day_bar
        return self

    def build(self):
        """
        Returns:
            matplotlib.figure.Figure
        """
        fig, axes = plt.subplots(2, 2, sharey=True)
        if self._titles is not None:
            axes[0][0].set_title(self._titles[0])
            axes[0][1].set_title(self._titles[1])

        candlestick_5m = self._create_candlestick(StandardTimeFrame.M5)
        candlestick_15m = self._create_candlestick(StandardTimeFrame.M15)
        histogram = _HistogramPlotBuilder(self._prices)
        lines = _LinePlotBuilder(self._prices)

        # Add opening range bar to the charts if set.
        if self._opening_range_bar:
            candlestick_5m.with_horizontal_line(
                y=self._opening_range_bar.ask_open)
            candlestick_5m.with_horizontal_line(
                y=self._opening_range_bar.ask_high)
            candlestick_5m.with_horizontal_line(
                y=self._opening_range_bar.ask_low)
            candlestick_5m.with_horizontal_line(
                y=self._opening_range_bar.ask_close)
            candlestick_15m.with_horizontal_line(
                y=self._opening_range_bar.ask_open)
            candlestick_15m.with_horizontal_line(
                y=self._opening_range_bar.ask_high)
            candlestick_15m.with_horizontal_line(
                y=self._opening_range_bar.ask_low)
            candlestick_15m.with_horizontal_line(
                y=self._opening_range_bar.ask_close)
            lines.with_horizontal_line(y=self._opening_range_bar.ask_open)
            lines.with_horizontal_line(y=self._opening_range_bar.ask_high)
            lines.with_horizontal_line(y=self._opening_range_bar.ask_low)
            lines.with_horizontal_line(y=self._opening_range_bar.ask_close)

        # Add previous day bar to the charts if set and withing chart limits.
        if self._previous_day_bar:
            candlestick_5m.with_horizontal_line(
                y=self._previous_day_bar.ask_open, color='black',
                draw_limits=self._draw_limits)
            candlestick_5m.with_horizontal_line(
                y=self._previous_day_bar.ask_high, color='black',
                draw_limits=self._draw_limits)
            candlestick_5m.with_horizontal_line(
                y=self._previous_day_bar.ask_low, color='black',
                draw_limits=self._draw_limits)
            candlestick_5m.with_horizontal_line(
                y=self._previous_day_bar.ask_close, color='black',
                draw_limits=self._draw_limits)
            candlestick_15m.with_horizontal_line(
                y=self._previous_day_bar.ask_open, color='black',
                draw_limits=self._draw_limits)
            candlestick_15m.with_horizontal_line(
                y=self._previous_day_bar.ask_high, color='black',
                draw_limits=self._draw_limits)
            candlestick_15m.with_horizontal_line(
                y=self._previous_day_bar.ask_low, color='black',
                draw_limits=self._draw_limits)
            candlestick_15m.with_horizontal_line(
                y=self._previous_day_bar.ask_close, color='black',
                draw_limits=self._draw_limits)
            lines.with_horizontal_line(
                y=self._previous_day_bar.ask_open, color='black',
                draw_limits=self._draw_limits)
            lines.with_horizontal_line(
                y=self._previous_day_bar.ask_high, color='black',
                draw_limits=self._draw_limits)
            lines.with_horizontal_line(
                y=self._previous_day_bar.ask_low, color='black',
                draw_limits=self._draw_limits)
            lines.with_horizontal_line(
                y=self._previous_day_bar.ask_close, color='black',
                draw_limits=self._draw_limits)

        candlestick_15m.build(axes=axes[0][0])
        candlestick_5m.build(axes=axes[0][1])
        lines.build(axes[1][0])
        histogram.build(axes=axes[1][1])
        return fig
