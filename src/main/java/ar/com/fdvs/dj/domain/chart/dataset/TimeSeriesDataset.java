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

package ar.com.fdvs.dj.domain.chart.dataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.com.fdvs.dj.domain.DynamicJasperDesign;
import ar.com.fdvs.dj.domain.StringExpression;
import ar.com.fdvs.dj.domain.entities.Entity;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn;
import ar.com.fdvs.dj.domain.hyperlink.LiteralExpression;
import ar.com.fdvs.dj.util.ExpressionUtils;
import net.sf.jasperreports.charts.design.JRDesignChartDataset;
import net.sf.jasperreports.charts.design.JRDesignTimeSeries;
import net.sf.jasperreports.charts.design.JRDesignTimeSeriesDataset;
import net.sf.jasperreports.charts.type.TimePeriodEnum;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignGroup;
import net.sf.jasperreports.engine.design.JRDesignVariable;

public class TimeSeriesDataset extends AbstractDataset {
	private static final long serialVersionUID = Entity.SERIAL_VERSION_UID;

	private PropertyColumn timePeriod = null;
	private final List<AbstractColumn> series = new ArrayList<>();
	private final Map<AbstractColumn, StringExpression> seriesLabels = new HashMap<>();
	private TimePeriodEnum timePeriodValue = TimePeriodEnum.DAY;

	/**
	 * Sets the time period column.
	 *
	 * @param timePeriod the time period column
	 **/
	public void setTimePeriod(PropertyColumn timePeriod) {
		this.timePeriod = timePeriod;
	}

	/**
	 * Returns the time period column.
	 *
	 * @return	the time period column
	 **/
	public PropertyColumn getTimePeriod() {
		return timePeriod;
	}

	/**
	 * Adds the specified serie column to the dataset.
	 *
	 * @param column the serie column
	 **/
	@Override
    public void addSerie(AbstractColumn column) {
		series.add(column);
	}

	/**
	 * Adds the specified serie column to the dataset with custom label.
	 *
	 * @param column the serie column
	 * @param label column the custom label
	 **/
	public void addSerie(AbstractColumn column, String label) {
		addSerie(column, new LiteralExpression(label));
	}

	/**
	 * Adds the specified serie column to the dataset with custom label expression.
	 *
	 * @param column the serie column
	 * @param labelExpression column the custom label expression
	 **/
	public void addSerie(AbstractColumn column, StringExpression labelExpression) {
		series.add(column);
		seriesLabels.put(column, labelExpression);
	}

	/**
	 * Removes the specified serie column from the dataset.
	 *
	 * @param column the serie column
	 **/
	public void removeSerie(AbstractColumn column) {
		series.remove(column);
		seriesLabels.remove(column);
	}

	/**
	 * Removes all defined series.
	 */
	public void clearSeries() {
		series.clear();
		seriesLabels.clear();
	}

	/**
	 * Returns a list of all the defined series.  Every entry in the list is of type AbstractColumn.
	 * If there are no defined series this method will return an empty list, not null.
	 *
	 * @return	the list of series
	 **/
	public List getSeries()	{
		return series;
	}

	/**
	 * Sets the time period class (YEAR, QUARTER, MONTH, WEEK, DAY, HOUR, MINUTE, SECOND, MILLISECOND).
	 * Default DAY
	 *
	 * @param timePeriodClass one of the time period class constants in this class
	 */
	public void setTimePeriodValue(TimePeriodEnum timePeriodValue) {
		this.timePeriodValue = timePeriodValue;
	}

	/**
	 * Gets the time period class (YEAR, QUARTER, MONTH, WEEK, DAY, HOUR, MINUTE, SECOND, MILLISECOND).
	 *
	 * @return one of the time period class constants in this class
	 */
	public TimePeriodEnum getTimePeriodValue() {
		return timePeriodValue;
	}

	@Override
    public JRDesignChartDataset transform(DynamicJasperDesign design, String name, JRDesignGroup group, JRDesignGroup parentGroup, Map vars) {
		final JRDesignTimeSeriesDataset data = new JRDesignTimeSeriesDataset(null);
		if (timePeriodValue != null) {
            data.setTimePeriod(timePeriodValue);
        }

		for (final AbstractColumn sery : series) {
			final JRDesignTimeSeries serie = new JRDesignTimeSeries();

			//And use it as value for each bar
			final JRDesignExpression varExp = getExpressionFromVariable((JRDesignVariable) vars.get(sery));
			serie.setValueExpression(varExp);

			//The key for each bar
			final JRExpression exp2 = group.getExpression();

			JRDesignExpression exp3;
			if (seriesLabels.containsKey(sery)) {
				String safeName = sery.getName() != null ? sery.getName() : sery.getTitle();
				exp3 = ExpressionUtils.createAndRegisterExpression(design, "dataset_" + safeName + "_" + name, seriesLabels.get(sery));
			} else {
				exp3 = new JRDesignExpression();
				exp3.setText("\"" + sery.getTitle() + "\"");
			}

			serie.setTimePeriodExpression(exp2);

			serie.setLabelExpression(exp3);
			serie.setSeriesExpression(exp3);

			data.addTimeSeries(serie);
		}

		setResetStyle(data, group, parentGroup);

		return data;
	}

	@Override
    public List getColumns() {
		return series;
	}

	@Override
    public PropertyColumn getColumnsGroup() {
		return timePeriod;
	}
}
