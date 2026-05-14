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
import ar.com.fdvs.dj.domain.chart.dataset.XYDataset;
import ar.com.fdvs.dj.domain.chart.plot.DJAxisFormat;
import ar.com.fdvs.dj.domain.chart.plot.ScatterPlot;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn;
import ar.com.fdvs.dj.domain.hyperlink.LiteralExpression;
import net.sf.jasperreports.charts.type.ChartTypeEnum;
import net.sf.jasperreports.charts.type.PlotOrientationEnum;

public class DJScatterChartBuilder extends AbstractChartBuilder<DJScatterChartBuilder> {
	//chart
	/**
	 * Sets the chart data operation (DJChart.CALCULATION_COUNT or DJChart.CALCULATION_SUM).
	 *
	 * @param operation the chart data operation
	 **/
	@Override
    public DJScatterChartBuilder setOperation(byte operation) {
		chart.setOperation(operation);
		return this;
	}

	/**
	 * Sets the hyperlink.
	 *
	 * @param link the hyperlink
	 **/
	@Override
    public DJScatterChartBuilder setLink(DJHyperLink link) {
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
    public DJScatterChartBuilder setBackColor(Color backColor) {
		chart.getOptions().setBackColor(backColor);
		return this;
	}

	/**
	 * Sets the chart height.
	 *
	 * @param height the chart height
	 **/
	@Override
    public DJScatterChartBuilder setHeight(int height) {
		chart.getOptions().setHeight(height);
		return this;
	}

	/**
	 * Sets the chart width.
	 *
	 * @param width the chart width
	 **/
	@Override
    public DJScatterChartBuilder setWidth(int width) {
		chart.getOptions().setWidth(width);
		return this;
	}

	/**
	 * Sets the centered.
	 *
	 * @param centered the centered
	 **/
	@Override
    public DJScatterChartBuilder setCentered(boolean centered) {
		chart.getOptions().setCentered(centered);
		return this;
	}

	/**
	 * Sets the position (DJChartOptions.POSITION_FOOTER or DJChartOptions.POSITION_HEADER).
	 *
	 * @param position the position
	 **/
	@Override
    public DJScatterChartBuilder setPosition(byte position) {
		chart.getOptions().setPosition(position);
		return this;
	}

	/**
	 * Sets the y position.
	 *
	 * @param y the y position
	 **/
	@Override
    public DJScatterChartBuilder setY(int y) {
		chart.getOptions().setY(y);
		return this;
	}

	/**
	 * Sets the x position.
	 *
	 **/
	@Override
    public DJScatterChartBuilder setX(int x) {
		chart.getOptions().setX(x);
		return this;
	}

	/**
	 * Sets the legend visibility.
	 *
	 * @param showLegend the legend visibility
	 **/
	@Override
    public DJScatterChartBuilder setShowLegend(boolean showLegend) {
		chart.getOptions().setShowLegend(showLegend);
		return this;
	}

	/**
	 * Sets the title color.
	 *
	 * @param titleColor the title color
	 **/
	@Override
    public DJScatterChartBuilder setTitleColor(Color titleColor) {
		chart.getOptions().setTitleColor(titleColor);
		return this;
	}

	/**
	 * Sets the subtitle color.
	 *
	 * @param subtitleColor the subtitle color
	 **/
	@Override
    public DJScatterChartBuilder setSubtitleColor(Color subtitleColor) {
		chart.getOptions().setSubtitleColor(subtitleColor);
		return this;
	}

	/**
	 * Sets the legend color.
	 *
	 * @param legendColor the legend color
	 **/
	@Override
    public DJScatterChartBuilder setLegendColor(Color legendColor) {
		chart.getOptions().setLegendColor(legendColor);
		return this;
	}

	/**
	 * Sets the legend background color.
	 *
	 * @param legendBackgroundColor the legend background color
	 **/
	@Override
    public DJScatterChartBuilder setLegendBackgroundColor(Color legendBackgroundColor) {
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
    public DJScatterChartBuilder setTheme(String theme) {
		chart.getOptions().setTheme(theme);
		return this;
	}

	/**
	 * Sets the title font.
	 *
	 * @param titleFont the title font
	 **/
	@Override
    public DJScatterChartBuilder setTitleFont(Font titleFont) {
		chart.getOptions().setTitleFont(titleFont);
		return this;
	}

	/**
	 * Sets the subtitle font.
	 *
	 * @param subtitleFont the subtitle font
	 **/
	@Override
    public DJScatterChartBuilder setSubtitleFont(Font subtitleFont) {
		chart.getOptions().setSubtitleFont(subtitleFont);
		return this;
	}

	/**
	 * Sets the legend font.
	 *
	 * @param legendFont the legend font
	 **/
	@Override
    public DJScatterChartBuilder setLegendFont(Font legendFont) {
		chart.getOptions().setLegendFont(legendFont);
		return this;
	}

	/**
	 * Sets the legend position (DJChartOptions.EDGE_TOP, DJChartOptions.EDGE_BOTTOM, DJChartOptions.EDGE_LEFT, DJChartOptions.EDGE_RIGHT).
	 *
	 * @param legendPosition the legend position
	 **/
	@Override
    public DJScatterChartBuilder setLegendPosition(int legendPosition) {
		chart.getOptions().setLegendPosition(legendPosition);
		return this;
	}

	/**
	 * Sets the title position (DJChartOptions.EDGE_TOP, DJChartOptions.EDGE_BOTTOM, DJChartOptions.EDGE_LEFT, DJChartOptions.EDGE_RIGHT).
	 *
	 * @param titlePosition the title position
	 **/
	@Override
    public DJScatterChartBuilder setTitlePosition(int titlePosition) {
		chart.getOptions().setTitlePosition(titlePosition);
		return this;
	}

	/**
	 * Sets the title.
	 *
	 * @param title the title
	 **/
	@Override
    public DJScatterChartBuilder setTitle(String title) {
		chart.getOptions().setTitleExpression(new LiteralExpression(title));
		return this;
	}

	/**
	 * Sets the title expression.
	 *
	 * @param titleExpression the title expression
	 **/
	@Override
    public DJScatterChartBuilder setTitle(StringExpression titleExpression) {
		chart.getOptions().setTitleExpression(titleExpression);
		return this;
	}

	/**
	 * Sets the subtitle.
	 *
	 * @param subtitle the subtitle
	 **/
	@Override
    public DJScatterChartBuilder setSubtitle(String subtitle) {
		chart.getOptions().setSubtitleExpression(new LiteralExpression(subtitle));
		return this;
	}

	/**
	 * Sets the subtitle expression.
	 *
	 * @param subtitleExpression the subtitle expression
	 **/
	@Override
    public DJScatterChartBuilder setSubtitle(StringExpression subtitleExpression) {
		chart.getOptions().setSubtitleExpression(subtitleExpression);
		return this;
	}

	/**
	 * Sets the line style (DJChartOptions.LINE_STYLE_SOLID, DJChartOptions.LINE_STYLE_DASHED, DJChartOptions.LINE_STYLE_DOTTED, DJChartOptions.LINE_STYLE_DOUBLE).
	 *
	 * @param lineStyle one of the line style constants in DJChartOptions class
	 */
	@Override
    public DJScatterChartBuilder setLineStyle(int lineStyle) {
		chart.getOptions().setLineStyle(lineStyle);
		return this;
	}

	/**
	 * Sets the line width.
	 *
	 * @param lineWidth the line width
	 **/
	@Override
    public DJScatterChartBuilder setLineWidth(float lineWidth) {
		chart.getOptions().setLineWidth(lineWidth);
		return this;
	}

	/**
	 * Sets the line color.
	 *
	 * @param lineColor the line color
	 **/
	@Override
    public DJScatterChartBuilder setLineColor(Color lineColor) {
		chart.getOptions().setLineColor(lineColor);
		return this;
	}

	/**
	 * Sets the padding.
	 *
	 * @param padding the padding
	 **/
	@Override
    public DJScatterChartBuilder setPadding(int padding) {
		chart.getOptions().setPadding(padding);
		return this;
	}

	/**
	 * Sets a user specified chart customizer class name.
 	 */
	@Override
    public DJScatterChartBuilder setCustomizerClass(String customizerClass) {
		chart.getOptions().setCustomizerClass(customizerClass);
		return this;
	}

	//dataset
	/**
	 * Allows AbstractChartBuilder to set the key column
	 */
	@Override
    protected DJScatterChartBuilder setCategory(PropertyColumn xValue) {
		setXValue(xValue);
		return this;
	}

	/**
	 * Sets the x value column.
	 *
	 * @param xValue the x value column
	 **/
	public DJScatterChartBuilder setXValue(PropertyColumn xValue) {
		getDataset().setXValue(xValue);
		return this;
	}

	/**
	 * Adds the specified serie column to the dataset.
	 *
	 * @param column the serie column
	 **/
	@Override
    public DJScatterChartBuilder addSerie(AbstractColumn column) {
		getDataset().addSerie(column);
		return this;
	}

	/**
	 * Adds the specified serie column to the dataset with custom label.
	 *
	 * @param column the serie column
	 * @param label column the custom label
	 **/
	public DJScatterChartBuilder addSerie(AbstractColumn column, String label) {
		getDataset().addSerie(column, label);
		return this;
	}

	/**
	 * Adds the specified serie column to the dataset with custom label.
	 *
	 * @param column the serie column
	 **/
	public DJScatterChartBuilder addSerie(AbstractColumn column, StringExpression labelExpression) {
		getDataset().addSerie(column, labelExpression);
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
	public DJScatterChartBuilder setLabelRotation(double labelRotation) {
		getPlot().setLabelRotation(labelRotation);
		return this;
	}

	/**
	 * Sets the plot orientation (PlotOrientationEnum.HORIZONTAL or PlotOrientationEnum.VERTICAL).
	 *
	 * @param orientation the plot orientation
	 **/
	public DJScatterChartBuilder setOrientation(PlotOrientationEnum orientation) {
		getPlot().setOrientation(orientation);
		return this;
	}

	/**
	 * Adds the specified series color to the plot.
	 *
	 * @param color the series color
	 **/
	@Override
    public DJScatterChartBuilder addSeriesColor(Color color) {
		getPlot().addSeriesColor(color);
		return this;
	}

	/**
	 * Set the specified series colors to the plot.
	 *
	 * @param seriesColors the series colors
	 **/
	@Override
    public DJScatterChartBuilder setSeriesColors(List<Color> seriesColors) {
		getPlot().setSeriesColors(seriesColors);
		return this;
	}

	//scatter plot
	/**
	 * Sets the x axis format.
	 *
	 * @param xAxisFormat the x axis format
	 **/
	public DJScatterChartBuilder setXAxisFormat(DJAxisFormat xAxisFormat) {
		getPlot().setXAxisFormat(xAxisFormat);
		return this;
	}

	/**
	 * Sets the y axis format.
	 *
	 * @param yAxisFormat the y axis format
	 **/
	public DJScatterChartBuilder setYAxisFormat(DJAxisFormat yAxisFormat) {
		getPlot().setYAxisFormat(yAxisFormat);
		return this;
	}

	/**
	 * Sets the shapes visibility.
	 *
	 * @param showShapes the shapes visibility
	 **/
	public DJScatterChartBuilder setShowShapes(boolean showShapes) {
		getPlot().setShowShapes(showShapes);
		return this;
	}

	/**
	 * Sets the lines visibility.
	 *
	 * @param showLines the lines visibility
	 **/
	public DJScatterChartBuilder setShowLines(boolean showLines) {
		getPlot().setShowLines(showLines);
		return this;
	}

	@Override
    protected XYDataset getDataset() {
		return (XYDataset) chart.getDataset();
	}

	@Override
    protected ScatterPlot getPlot() {
		return (ScatterPlot) chart.getPlot();
	}

	@Override
    protected ChartTypeEnum getChartType() {
		return ChartTypeEnum.SCATTER;
	}
}
