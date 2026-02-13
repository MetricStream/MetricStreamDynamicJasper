/*
 * DynamicJasper: A library for creating reports dynamically by specifying
 * columns, groups, styles, etc. at runtime. It also saves a lot of development
 * time in many cases! (http://sourceforge.net/projects/dynamicjasper)
 *
 * Copyright (C) 2008  FDV Solutions (http://www.fdvsolutions.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 *
 * License as published by the Free Software Foundation; either
 *
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 *
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *
 */

package ar.com.fdvs.dj.domain.chart.builder;

import java.awt.Color;
import java.util.List;

import ar.com.fdvs.dj.domain.DJHyperLink;
import ar.com.fdvs.dj.domain.StringExpression;
import ar.com.fdvs.dj.domain.chart.dataset.TimeSeriesDataset;
import ar.com.fdvs.dj.domain.chart.plot.DJAxisFormat;
import ar.com.fdvs.dj.domain.chart.plot.TimeSeriesPlot;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn;
import ar.com.fdvs.dj.domain.hyperlink.LiteralExpression;
import net.sf.jasperreports.charts.type.ChartTypeEnum;
import net.sf.jasperreports.charts.type.PlotOrientationEnum;
import net.sf.jasperreports.charts.type.TimePeriodEnum;

public class DJTimeSeriesChartBuilder extends AbstractChartBuilder<DJTimeSeriesChartBuilder> {
	//chart
	/**
	 * Sets the chart data operation (DJChart.CALCULATION_COUNT or DJChart.CALCULATION_SUM).
	 *
	 * @param operation the chart data operation
	 **/
	@Override
    public DJTimeSeriesChartBuilder setOperation(byte operation) {
		chart.setOperation(operation);
		return this;
	}

	/**
	 * Sets the hyperlink.
	 *
	 * @param link the hyperlink
	 **/
	@Override
    public DJTimeSeriesChartBuilder setLink(DJHyperLink link) {
		chart.setLink(link);
		return this;
	}

	//chart options
	/**
	 * Sets the background color.
	 *
	 * @param backColor the background color
	 **/
	@Override
    public DJTimeSeriesChartBuilder setBackColor(Color backColor) {
		chart.getOptions().setBackColor(backColor);
		return this;
	}

	/**
	 * Sets the chart height.
	 *
	 * @param height the chart height
	 **/
	@Override
    public DJTimeSeriesChartBuilder setHeight(int height) {
		chart.getOptions().setHeight(height);
		return this;
	}

	/**
	 * Sets the chart width.
	 *
	 * @param width the chart width
	 **/
	@Override
    public DJTimeSeriesChartBuilder setWidth(int width) {
		chart.getOptions().setWidth(width);
		return this;
	}

	/**
	 * Sets the centered.
	 *
	 * @param centered the centered
	 **/
	@Override
    public DJTimeSeriesChartBuilder setCentered(boolean centered) {
		chart.getOptions().setCentered(centered);
		return this;
	}

	/**
	 * Sets the position (DJChartOptions.POSITION_FOOTER or DJChartOptions.POSITION_HEADER).
	 *
	 * @param position the position
	 **/
	@Override
    public DJTimeSeriesChartBuilder setPosition(byte position) {
		chart.getOptions().setPosition(position);
		return this;
	}

	/**
	 * Sets the y position.
	 *
	 * @param y the y position
	 **/
	@Override
    public DJTimeSeriesChartBuilder setY(int y) {
		chart.getOptions().setY(y);
		return this;
	}

	/**
	 * Sets the x position.
	 *
	 **/
	@Override
    public DJTimeSeriesChartBuilder setX(int x) {
		chart.getOptions().setX(x);
		return this;
	}

	/**
	 * Sets the legend visibility.
	 *
	 * @param showLegend the legend visibility
	 **/
	@Override
    public DJTimeSeriesChartBuilder setShowLegend(boolean showLegend) {
		chart.getOptions().setShowLegend(showLegend);
		return this;
	}

	/**
	 * Sets the title color.
	 *
	 * @param titleColor the title color
	 **/
	@Override
    public DJTimeSeriesChartBuilder setTitleColor(Color titleColor) {
		chart.getOptions().setTitleColor(titleColor);
		return this;
	}

	/**
	 * Sets the subtitle color.
	 *
	 * @param subtitleColor the subtitle color
	 **/
	@Override
    public DJTimeSeriesChartBuilder setSubtitleColor(Color subtitleColor) {
		chart.getOptions().setSubtitleColor(subtitleColor);
		return this;
	}

	/**
	 * Sets the legend color.
	 *
	 * @param legendColor the legend color
	 **/
	@Override
    public DJTimeSeriesChartBuilder setLegendColor(Color legendColor) {
		chart.getOptions().setLegendColor(legendColor);
		return this;
	}

	/**
	 * Sets the legend background color.
	 *
	 * @param legendBackgroundColor the legend background color
	 **/
	@Override
    public DJTimeSeriesChartBuilder setLegendBackgroundColor(Color legendBackgroundColor) {
		chart.getOptions().setLegendBackgroundColor(legendBackgroundColor);
		return this;
	}

	/**
	 * Sets the theme.
	 * Chart themes support to allow changing the overall appearance of charts generated with the build-in chart element
	 *
	 * @param theme the theme
	 **/
	@Override
    public DJTimeSeriesChartBuilder setTheme(String theme) {
		chart.getOptions().setTheme(theme);
		return this;
	}

	/**
	 * Sets the title font.
	 *
	 * @param titleFont the title font
	 **/
	@Override
    public DJTimeSeriesChartBuilder setTitleFont(Font titleFont) {
		chart.getOptions().setTitleFont(titleFont);
		return this;
	}

	/**
	 * Sets the subtitle font.
	 *
	 * @param subtitleFont the subtitle font
	 **/
	@Override
    public DJTimeSeriesChartBuilder setSubtitleFont(Font subtitleFont) {
		chart.getOptions().setSubtitleFont(subtitleFont);
		return this;
	}

	/**
	 * Sets the legend font.
	 *
	 * @param legendFont the legend font
	 **/
	@Override
    public DJTimeSeriesChartBuilder setLegendFont(Font legendFont) {
		chart.getOptions().setLegendFont(legendFont);
		return this;
	}

	/**
	 * Sets the legend position (DJChartOptions.EDGE_TOP, DJChartOptions.EDGE_BOTTOM, DJChartOptions.EDGE_LEFT, DJChartOptions.EDGE_RIGHT).
	 *
	 * @param legendPosition the legend position
	 **/
	@Override
    public DJTimeSeriesChartBuilder setLegendPosition(int legendPosition) {
		chart.getOptions().setLegendPosition(legendPosition);
		return this;
	}

	/**
	 * Sets the title position (DJChartOptions.EDGE_TOP, DJChartOptions.EDGE_BOTTOM, DJChartOptions.EDGE_LEFT, DJChartOptions.EDGE_RIGHT).
	 *
	 * @param titlePosition the title position
	 **/
	@Override
    public DJTimeSeriesChartBuilder setTitlePosition(int titlePosition) {
		chart.getOptions().setTitlePosition(titlePosition);
		return this;
	}

	/**
	 * Sets the title.
	 *
	 * @param title the title
	 **/
	@Override
    public DJTimeSeriesChartBuilder setTitle(String title) {
		chart.getOptions().setTitleExpression(new LiteralExpression(title));
		return this;
	}

	/**
	 * Sets the title expression.
	 *
	 * @param titleExpression the title expression
	 **/
	@Override
    public DJTimeSeriesChartBuilder setTitle(StringExpression titleExpression) {
		chart.getOptions().setTitleExpression(titleExpression);
		return this;
	}

	/**
	 * Sets the subtitle.
	 *
	 * @param subtitle the subtitle
	 **/
	@Override
    public DJTimeSeriesChartBuilder setSubtitle(String subtitle) {
		chart.getOptions().setSubtitleExpression(new LiteralExpression(subtitle));
		return this;
	}

	/**
	 * Sets the subtitle expression.
	 *
	 * @param subtitleExpression the subtitle expression
	 **/
	@Override
    public DJTimeSeriesChartBuilder setSubtitle(StringExpression subtitleExpression) {
		chart.getOptions().setSubtitleExpression(subtitleExpression);
		return this;
	}

	/**
	 * Sets the line style (DJChartOptions.LINE_STYLE_SOLID, DJChartOptions.LINE_STYLE_DASHED, DJChartOptions.LINE_STYLE_DOTTED, DJChartOptions.LINE_STYLE_DOUBLE).
	 *
	 * @param lineStyle one of the line style constants in DJChartOptions class
	 */
	@Override
    public DJTimeSeriesChartBuilder setLineStyle(int lineStyle) {
		chart.getOptions().setLineStyle(lineStyle);
		return this;
	}

	/**
	 * Sets the line width.
	 *
	 * @param lineWidth the line width
	 **/
	@Override
    public DJTimeSeriesChartBuilder setLineWidth(float lineWidth) {
		chart.getOptions().setLineWidth(lineWidth);
		return this;
	}

	/**
	 * Sets the line color.
	 *
	 * @param lineColor the line color
	 **/
	@Override
    public DJTimeSeriesChartBuilder setLineColor(Color lineColor) {
		chart.getOptions().setLineColor(lineColor);
		return this;
	}

	/**
	 * Sets the padding.
	 *
	 * @param padding the padding
	 **/
	@Override
    public DJTimeSeriesChartBuilder setPadding(int padding) {
		chart.getOptions().setPadding(padding);
		return this;
	}

	/**
	 * Sets a user specified chart customizer class name.
	 * @see net.sf.jasperreports.engine.JRChartCustomizer
 	 */
	@Override
    public DJTimeSeriesChartBuilder setCustomizerClass(String customizerClass) {
		chart.getOptions().setCustomizerClass(customizerClass);
		return this;
	}

	//dataset
	/**
	 * Allows AbstractChartBuilder to set the key column
	 */
	@Override
    protected DJTimeSeriesChartBuilder setCategory(PropertyColumn timePeriod) {
		setTimePeriod(timePeriod);
		return this;
	}

	/**
	 * Sets the time period column.
	 *
	 * @param timePeriod the time period column
	 **/
	public DJTimeSeriesChartBuilder setTimePeriod(PropertyColumn timePeriod) {
		getDataset().setTimePeriod(timePeriod);
		return this;
	}

	/**
	 * Adds the specified serie column to the dataset.
	 *
	 * @param column the serie column
	 **/
	@Override
    public DJTimeSeriesChartBuilder addSerie(AbstractColumn column) {
		getDataset().addSerie(column);
		return this;
	}

	/**
	 * Adds the specified serie column to the dataset with custom label.
	 *
	 * @param column the serie column
	 * @param label column the custom label
	 **/
	public DJTimeSeriesChartBuilder addSerie(AbstractColumn column, String label) {
		getDataset().addSerie(column, label);
		return this;
	}

	/**
	 * Adds the specified serie column to the dataset with custom label.
	 *
	 * @param column the serie column
	 **/
	public DJTimeSeriesChartBuilder addSerie(AbstractColumn column, StringExpression labelExpression) {
		getDataset().addSerie(column, labelExpression);
		return this;
	}

	/**
	 * Sets the time period class (YEAR, QUARTER, MONTH, WEEK, DAY, HOUR, MINUTE, SECOND, MILLISECOND).
	 * Default DAY
	 *
	 * @param timePeriodClass one of the time period class constants in TimeSeriesDataset class
	 */
	public DJTimeSeriesChartBuilder setTimePeriodValue(TimePeriodEnum timePeriodvalue) {
		getDataset().setTimePeriodValue(timePeriodvalue);
		return this;
	}

	//plot
	/**
	 * Sets the angle in degrees to rotate the data axis labels.  The range is -360 to 360.  A positive value angles
	 * the label so it reads downwards wile a negative value angles the label so it reads upwards.  Only charts that
	 * use a category based axis (such as line or bar charts) support label rotation.
	 *
	 * @param labelRotation the label rotation
	 **/
	public DJTimeSeriesChartBuilder setLabelRotation(double labelRotation) {
		getPlot().setLabelRotation(labelRotation);
		return this;
	}

	/**
	 * Sets the plot orientation (PlotOrientationEnum.HORIZONTAL or PlotOrientationEnum.VERTICAL).
	 *
	 * @param orientation the plot orientation
	 **/
	public DJTimeSeriesChartBuilder setOrientation(PlotOrientationEnum orientation) {
		getPlot().setOrientation(orientation);
		return this;
	}

	/**
	 * Adds the specified series color to the plot.
	 *
	 * @param color the series color
	 **/
	@Override
    public DJTimeSeriesChartBuilder addSeriesColor(Color color) {
		getPlot().addSeriesColor(color);
		return this;
	}

	/**
	 * Set the specified series colors to the plot.
	 *
	 * @param seriesColors the series colors
	 **/
	@Override
    public DJTimeSeriesChartBuilder setSeriesColors(List<Color> seriesColors) {
		getPlot().setSeriesColors(seriesColors);
		return this;
	}

	//time series plot
	/**
	 * Sets the time axis format.
	 *
	 * @param timeAxisFormat the time axis format
	 **/
	public DJTimeSeriesChartBuilder setTimeAxisFormat(DJAxisFormat timeAxisFormat) {
		getPlot().setTimeAxisFormat(timeAxisFormat);
		return this;
	}

	/**
	 * Sets the value axis format.
	 *
	 * @param valueAxisFormat the value axis format
	 **/
	public DJTimeSeriesChartBuilder setValueAxisFormat(DJAxisFormat valueAxisFormat) {
		getPlot().setValueAxisFormat(valueAxisFormat);
		return this;
	}

	/**
	 * Sets the shapes visibility.
	 *
	 * @param showShapes the shapes visibility
	 **/
	public DJTimeSeriesChartBuilder setShowShapes(boolean showShapes) {
		getPlot().setShowShapes(showShapes);
		return this;
	}

	/**
	 * Sets the lines visibility.
	 *
	 * @param showLines the lines visibility
	 **/
	public DJTimeSeriesChartBuilder setShowLines(boolean showLines) {
		getPlot().setShowLines(showLines);
		return this;
	}

	@Override
    protected TimeSeriesDataset getDataset() {
		return (TimeSeriesDataset) chart.getDataset();
	}

	@Override
    protected TimeSeriesPlot getPlot() {
		return (TimeSeriesPlot) chart.getPlot();
	}

	@Override
    protected ChartTypeEnum getChartType() {
		return ChartTypeEnum.TIMESERIES;
	}
}
