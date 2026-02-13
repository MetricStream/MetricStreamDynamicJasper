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

package ar.com.fdvs.dj.core.layout;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ar.com.fdvs.dj.core.DJConstants;
import ar.com.fdvs.dj.core.DJDefaultScriptlet;
import ar.com.fdvs.dj.core.registration.EntitiesRegistrationException;
import ar.com.fdvs.dj.domain.DJCRosstabMeasurePrecalculatedTotalProvider;
import ar.com.fdvs.dj.domain.DJCrosstab;
import ar.com.fdvs.dj.domain.DJCrosstabColumn;
import ar.com.fdvs.dj.domain.DJCrosstabMeasure;
import ar.com.fdvs.dj.domain.DJCrosstabRow;
import ar.com.fdvs.dj.domain.DJValueFormatter;
import ar.com.fdvs.dj.domain.DynamicJasperDesign;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.entities.conditionalStyle.ConditionalStyle;
import ar.com.fdvs.dj.util.ExpressionUtils;
import ar.com.fdvs.dj.util.HyperLinkUtil;
import ar.com.fdvs.dj.util.LayoutUtils;
import ar.com.fdvs.dj.util.Utils;
import net.sf.jasperreports.crosstabs.design.JRDesignCellContents;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstab;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabBucket;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabCell;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabColumnGroup;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabDataset;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabMeasure;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabParameter;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabRowGroup;
import net.sf.jasperreports.crosstabs.fill.JRPercentageCalculatorFactory;
import net.sf.jasperreports.crosstabs.type.CrosstabPercentageEnum;
import net.sf.jasperreports.crosstabs.type.CrosstabTotalPositionEnum;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.design.JRDesignConditionalStyle;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignDatasetRun;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignStyle;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.CalculationEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.PositionTypeEnum;
import net.sf.jasperreports.engine.type.StretchTypeEnum;

public class Dj2JrCrosstabBuilder {

	private static final Log log = LogFactory.getLog(Dj2JrCrosstabBuilder.class);
	private static final Random random = new Random();

	private JasperDesign design;
	private JRDesignCrosstab jrcross;
	private DJCrosstab djcross;
	private DJCrosstabColumn[] cols;
	private DJCrosstabRow[] rows;
	private Color[][] colors;
	private AbstractLayoutManager layoutManager;

	public JRDesignCrosstab createCrosstab(DJCrosstab djcrosstab, AbstractLayoutManager layoutManager) {
		djcross = djcrosstab;
		this.layoutManager = layoutManager;
		design = layoutManager.getDesign();

		jrcross = new JRDesignCrosstab();

		jrcross.setPositionType( PositionTypeEnum.FIX_RELATIVE_TO_TOP );

		cols = djcrosstab.getColumns().toArray(new DJCrosstabColumn[]{});
		rows = djcrosstab.getRows().toArray(new DJCrosstabRow[]{});

		final JRDesignExpression mapExp = new JRDesignExpression();
		mapExp.setText("$P{REPORT_PARAMETERS_MAP}");
		jrcross.setParametersMapExpression(mapExp);

		final JRDesignCrosstabParameter crossParameter = new JRDesignCrosstabParameter();
		crossParameter.setName("REPORT_SCRIPTLET");
		crossParameter.setValueClassName(DJDefaultScriptlet.class.getName());
		final JRDesignExpression expression = new JRDesignExpression();
		expression.setText("$P{"+JRParameter.REPORT_PARAMETERS_MAP+"}.get(\"REPORT_SCRIPTLET\")");
		crossParameter.setExpression(expression);
		try {
			jrcross.addParameter(crossParameter);
		} catch (final JRException e) {
			e.printStackTrace();
		}

		initColors();

		/*
		  Set the size
		 */
		setCrosstabOptions();

		/*
		  Register COLUMNS
		 */
		registerColumns();

		/*
		  Register ROWS
		 */
		registerRows();

		/*
		  Measures
		 */
		registerMeasures();


		/*
		  Create CELLS
		 */
		createCells();


		/*
		  Create main header cell
		 */
		createMainHeaderCell();

		if (djcrosstab.getDatasource().getDataSourceOrigin() == DJConstants.DATA_SOURCE_ORIGIN_REPORT_DATASOURCE) {
			// register just the fields
			registerFields(djcrosstab);
		} else {
		/*
		  Register DATASET
		 */
			registerDataSet(djcrosstab);
		}

		return jrcross;
	}

	/**
	 * Register the fields used in the crosstab with the main datasource
	 * @param djcrosstab the crosstab
	 */
	private void registerFields(DJCrosstab djcrosstab) {
		// add fields to the main datasource
		for (final DJCrosstabRow rowGroup : djcrosstab.getRows()) {

			try {
				final JRDesignField field = new JRDesignField();
				field.setName(rowGroup.getProperty().getProperty());
				field.setValueClassName(rowGroup.getProperty().getValueClassName());
				design.addField(field);
			} catch (final JRException e) {
				log.error(e.getMessage(), e);
			}
		}

		for (final DJCrosstabColumn colGroup : djcrosstab.getColumns()) {

			try {
				final JRDesignField field = new JRDesignField();
				field.setName(colGroup.getProperty().getProperty());
				field.setValueClassName(colGroup.getProperty().getValueClassName());
				design.addField(field);
			} catch (final JRException e) {
				log.error(e.getMessage(), e);
			}
		}

		for (final DJCrosstabMeasure measure : djcrosstab.getMeasures()) {

			try {
				final JRDesignField field = new JRDesignField();
				field.setName(measure.getProperty().getProperty());
				field.setValueClassName(measure.getProperty().getValueClassName());
				design.addField(field);
			} catch (final JRException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * Sets the options contained in the DJCrosstab to the JRDesignCrosstab.
	 * Also fits the correct width
	 */
	private void setCrosstabOptions() {
		if (djcross.isUseFullWidth()){
			jrcross.setWidth(layoutManager.getReport().getOptions().getPrintableWidth());
		} else {
			jrcross.setWidth(djcross.getWidth());
		}
		jrcross.setHeight(djcross.getHeight());

		jrcross.setColumnBreakOffset(djcross.getColumnBreakOffset());

        jrcross.setIgnoreWidth(djcross.isIgnoreWidth());
	}

	private void createMainHeaderCell() {
		final JRDesignCellContents contents = new JRDesignCellContents();

		contents.setBackcolor(colors[colors.length-1][colors[0].length-1]);
		contents.setMode( ModeEnum.OPAQUE );

		jrcross.setHeaderCell(contents);

		final JRDesignTextField element = new JRDesignTextField();
		String text = "";
		int auxHeight = 0;
		int auxWidth = 0;

		if (djcross.isAutomaticTitle()) {
            text = createAutomaticMainHeaderTitle();
        } else if (djcross.getMainHeaderTitle() != null) {
            text = "\"" + djcross.getMainHeaderTitle() +  "\"";
        }

		for (final DJCrosstabColumn col : djcross.getColumns()) {
			auxHeight += col.getHeaderHeight();
		}
		for (final DJCrosstabRow row : djcross.getRows()) {
			auxWidth += row.getHeaderWidth();
		}

		final JRDesignExpression exp = ExpressionUtils.createStringExpression(text);
		element.setExpression(exp);

		element.setWidth(auxWidth);
		element.setHeight(auxHeight);
		// Será que "element.setStretchWithOverflow(true);" é a mesma coisa que
		element.setStretchType(StretchTypeEnum.CONTAINER_HEIGHT);

		if (djcross.getHeaderStyle() != null) {
            layoutManager.applyStyleToElement(djcross.getHeaderStyle(), element);
        }

		applyCellBorder(contents, true, true);
		contents.addElement(element);
	}

	/**
	 * @return
	 */
	private String createAutomaticMainHeaderTitle() {
		final StringBuilder text = new StringBuilder();
		for (final Iterator<DJCrosstabColumn> iterator = djcross.getColumns().iterator(); iterator.hasNext();) {
			final DJCrosstabColumn col = iterator.next();
			text.append(col.getTitle());
			if (iterator.hasNext()) {
                text.append(", ");
            }
		}

		text.append("\\nvs.\\n");
		for (final Iterator<DJCrosstabRow> iterator = djcross.getRows().iterator(); iterator.hasNext();) {
			final DJCrosstabRow row = iterator.next();
			text.append(row.getTitle());
			if (iterator.hasNext()) {
                text.append(", ");
            }
		}
		return "\"" + text.append("\"").toString();
	}

	private void initColors() {
		final CrossTabColorShema colorScheme = djcross.getCtColorScheme();
		if (colorScheme != null){
			colorScheme.create(cols.length, rows.length);
			colors = colorScheme.getColors();
		} else {
            colors = CrossTabColorShemaGenerator.createSchema(djcross.getColorScheme(), cols.length, rows.length);
        }

	}


	/**
	 * @param djcrosstab
	 */
	private void registerDataSet(DJCrosstab djcrosstab) {
		final JRDesignCrosstabDataset dataset = new JRDesignCrosstabDataset();
		dataset.setDataPreSorted(djcrosstab.getDatasource().isPreSorted());
		final JRDesignDatasetRun datasetRun = new JRDesignDatasetRun();
//		datasetRun.setDatasetName("sub1");
		final JRDesignExpression exp = ExpressionUtils.getDataSourceExpression(djcrosstab.getDatasource());
		datasetRun.setDataSourceExpression(exp);



		dataset.setDatasetRun(datasetRun);


		final JRDesignDataset jrDataset = new JRDesignDataset(false);
//		jrDataset.setName("sub1");

		for (int i =  rows.length-1; i >= 0; i--) {
			final DJCrosstabRow crosstabRow = rows[i];
			final JRDesignField field = new JRDesignField();
			field.setName(crosstabRow.getProperty().getProperty());
			field.setValueClassName(crosstabRow.getProperty().getValueClassName());
			try {
				jrDataset.addField(field);
			} catch (final JRException e) {
				log.error(e.getMessage(),e);
			}
		}
		for (int i = cols.length-1; i >= 0; i--) {
			final DJCrosstabColumn crosstabColumn = cols[i];
			final JRDesignField field = new JRDesignField();
			field.setName(crosstabColumn.getProperty().getProperty());
			field.setValueClassName(crosstabColumn.getProperty().getValueClassName());
			try {
				jrDataset.addField(field);
			} catch (final JRException e) {
				log.error(e.getMessage(),e);
			}
		}

		for (final DJCrosstabMeasure djmeasure : djcrosstab.getMeasures()) {
			final JRDesignField field = new JRDesignField();
			field.setName(djmeasure.getProperty().getProperty());
			field.setValueClassName(djmeasure.getProperty().getValueClassName());
			try {
				jrDataset.addField(field);
			} catch (final JRException e) {
				log.warn(e.getMessage() + " in crosstab, using old one.");
			}
		}

//		field.setName(djcrosstab.getMeasure(0).getProperty().getProperty());
//		field.setValueClassName(djcrosstab.getMeasure(0).getProperty().getValueClassName());
//		try {
//			jrDataset.addField(field);
//		} catch (JRException e) {
//			log.error(e.getMessage(),e);
//		}

		jrcross.setDataset(dataset);
		String dsName = "crosstabDataSource_" + Math.abs(random.nextLong());

		while (design.getDatasetMap().containsKey(dsName)){
			dsName = "crosstabDataSource_" + Math.abs(random.nextLong());
		}

		datasetRun.setDatasetName(dsName);
		jrDataset.setName(dsName);

		log.debug("Crosstab dataset name = " + dsName);
		try {
				if (!design.getDatasetMap().containsKey(jrDataset.getName())) {
                    design.addDataset(jrDataset);
                }
			} catch (final JRException e) {
				//Will never happen
				log.error(e.getMessage(),e);
			}
	}

	/**
	 * The way to create the cells is like this:<br><br>
	 *
	 * the result is a matrix of (cols+1)*(rows+1) cells.<br>
	 * Each cell has 2 properties that describes which row and column they belong (like coordinates).<br><br>
	 *
	 * null/null	| col(n)/null	| ...	| col(1)/null      <br>
	 * --------------------------------------------------      <br>
	 * null/row(n)	| col(n)/row(n)	| ...	| col(1)/row(n)    <br>
	 * --------------------------------------------------      <br>
	 * null/...		| col(n)/...	| ...	| col(1)/...       <br>
	 * --------------------------------------------------      <br>
	 * null/row(1)	| col(n)/row(1)	| ...	| col(1)/row(1)    <br>
	 *
	 *<br><br>
	 * you get this matrix with this two vectors<br>
	 * cols: {null, col(n), ..., col(1)}<br>
	 * rows: {null, row(n), ..., row(1)}<br>
	 *<br>
	 * where the col(n) is the outer most column, and row(n) is the outer most row in the crosstab<br><br>
	 *
	 * The cell with null/null is the inner most cell in the crosstab<br>
	 *
	 */
	protected void createCells() {
		final DJCrosstabColumn auxCol = new DJCrosstabColumn();
		final DJCrosstabRow auxRow = new DJCrosstabRow();
		try {
			BeanUtils.copyProperties(auxCol, djcross.getColumns().get(djcross.getColumns().size()-1));
			BeanUtils.copyProperties(auxRow, djcross.getRows().get(djcross.getRows().size()-1));
		} catch (final Exception e) {
			log.error(e.getMessage(),e); //must not happend
		}
		auxCol.setProperty(null);
		auxRow.setProperty(null);

		final List<DJCrosstabColumn> auxColsList = new ArrayList<>(djcross.getColumns());
		auxColsList.add(auxCol);
		final List<DJCrosstabRow> auxRowsList = new ArrayList<>(djcross.getRows());
		auxRowsList.add(auxRow);

		final DJCrosstabColumn[] auxCols = auxColsList.toArray(new DJCrosstabColumn[]{});
		final DJCrosstabRow[] auxRows = auxRowsList.toArray(new DJCrosstabRow[]{});


		for (int i = auxCols.length-1; i >= 0; i--) {
			for (int j =  auxRows.length-1; j >= 0; j--) {
				final DJCrosstabColumn crosstabColumn = auxCols[i];
				final DJCrosstabRow crosstabRow = auxRows[j];

				final JRDesignCrosstabCell cell = new JRDesignCrosstabCell();

				cell.setWidth(crosstabColumn.getWidth());
				cell.setHeight(crosstabRow.getHeight());

				final boolean isRowTotal = crosstabRow.getProperty() != null;
				final boolean isColumnTotal = crosstabColumn.getProperty() != null;

				if (isColumnTotal) {
                    cell.setColumnTotalGroup(crosstabColumn.getProperty().getProperty());
                }

				if (isRowTotal) {
                    cell.setRowTotalGroup(crosstabRow.getProperty().getProperty());
                }

				final JRDesignCellContents contents = new JRDesignCellContents();

				int measureIdx = 0;
				int yOffsetCounter = 0;
				final int measureHeight = crosstabRow.getHeight() / djcross.getVisibleMeasures().size();

				for (final Iterator<DJCrosstabMeasure> iterator = djcross.getMeasures().iterator(); iterator.hasNext(); measureIdx++, yOffsetCounter++) {
					final DJCrosstabMeasure djmeasure = iterator.next();
                    if (!djmeasure.getVisible()) {
                        yOffsetCounter--;
                        continue; //IMPORTANT! we need to keep the idx to match the index of the measure in the measure list
                    }

					final JRDesignTextField element = new JRDesignTextField();
					element.setWidth(crosstabColumn.getWidth());
					element.setHeight(measureHeight);
					element.setY(yOffsetCounter*measureHeight);


					final JRDesignExpression measureExp = new JRDesignExpression();

					final boolean isTotalCell = isRowTotal || isColumnTotal;

					final String measureValueClassName = djmeasure.getProperty().getValueClassName();

                    final String measureProperty = djmeasure.getMeasureIdentifier(measureIdx);
                    final String meausrePrefix = djmeasure.getMeasurePrefix(measureIdx);

					if (!isTotalCell){
						if (djmeasure.getValueFormatter()== null){
                            measureExp.setText("$V{"+ measureProperty +"}");
						} else {
                            measureExp.setText(djmeasure.getTextForValueFormatterExpression(measureProperty, djcross.getMeasures()));
						}
					} else if (djmeasure.getValueFormatter()== null){
                    	if (djmeasure.getPrecalculatedTotalProvider() == null) {
                    		measureExp.setText("$V{"+measureProperty+"}");
                    	} else {
                    		//call the precalculated value.
                    		setExpressionForPrecalculatedTotalValue(auxCols, auxRows, measureExp, djmeasure, crosstabColumn, crosstabRow, meausrePrefix);
                    	}
                    } else if (djmeasure.getPrecalculatedTotalProvider() == null) {
                    	//has value formatter, no total provider
                    	measureExp.setText(djmeasure.getTextForValueFormatterExpression(measureProperty, djcross.getMeasures()));

                    } else {
                    	//NO value formatter, call the precalculated value only
                    	setExpressionForPrecalculatedTotalValue(auxCols, auxRows, measureExp, djmeasure, crosstabColumn, crosstabRow, meausrePrefix);
                    }

					element.setExpression(measureExp);

					//measure
					if (!isRowTotal && !isColumnTotal && (djmeasure.getStyle() != null) ){
						//this is the inner most cell
						layoutManager.applyStyleToElement(djmeasure.getStyle() , element);
					}
					//row total only
					if (isRowTotal && !isColumnTotal) {
						Style style = getRowTotalStyle(crosstabRow);
						if (style == null) {
                            style = djmeasure.getStyle();
                        }
						if (style != null) {
                            layoutManager.applyStyleToElement(style, element);
                        }
					}
					//column total only
					if (isColumnTotal && !isRowTotal) {
						Style style = getColumnTotalStyle(crosstabColumn);
						if (style == null) {
                            style = djmeasure.getStyle();
                        }
						if (style != null) {
                            layoutManager.applyStyleToElement(style, element);
                        }
					}
					//row and column total
					if (isRowTotal && isColumnTotal) {
						Style style = getRowTotalStyle(crosstabRow);
						if (style == null) {
                            style = getColumnTotalStyle(crosstabColumn);
                        }
						if (style == null) {
                            style = djmeasure.getStyle();
                        }
						if (style != null) {
                            layoutManager.applyStyleToElement(style, element);
                        }
					}

					JRDesignStyle jrstyle = (JRDesignStyle) element.getStyle();
					//FIXME this is a hack
					if (jrstyle == null){
						if (log.isDebugEnabled()){
							log.warn("jrstyle is null in crosstab cell, this should have not happened.");
						}
						layoutManager.applyStyleToElement(null, element);
						jrstyle = (JRDesignStyle)element.getStyle();
						jrstyle.setBlankWhenNull(true);
					}

					final JRDesignStyle alternateStyle = Utils.cloneStyle(jrstyle);
	    			alternateStyle.setName(alternateStyle.getFontName() +"_for_column_" + djmeasure.getProperty().getProperty() + "_i" + i + "_j" + j);
	    			alternateStyle.getConditionalStyleList().clear();
	    			element.setStyle(alternateStyle);
	    			try {
	    				design.addStyle(alternateStyle);
	    			} catch (final JRException e) { /*e.printStackTrace(); //Already there, nothing to do **/}
	    			setUpConditionStyles(alternateStyle, djmeasure, measureExp.getText());

					if (djmeasure.getLink() != null){
						final String name = "cell_" + i + "_" +  j + "_ope" + djmeasure.getOperation().getValue();
						HyperLinkUtil.applyHyperLinkToElement((DynamicJasperDesign)design, djmeasure.getLink(), element, name);
					}

					contents.addElement(element);

				}

				contents.setMode( ModeEnum.OPAQUE );

				applyBackgroundColor(contents,crosstabRow,crosstabColumn,i,j);

				applyCellBorder(contents, false, false);

				cell.setContents(contents);


				try {
					jrcross.addCell(cell);
				} catch (final JRException e) {
					log.error(e.getMessage(),e);
				}

			}

		}
	}

	/**
	 * set proper expression text invoking the DJCRosstabMeasurePrecalculatedTotalProvider for the cell
	 * @param auxRows
	 * @param auxCols
	 * @param measureExp
	 * @param djmeasure
	 * @param crosstabColumn
	 * @param crosstabRow
	 * @param meausrePrefix
	 */
	private void setExpressionForPrecalculatedTotalValue(
			DJCrosstabColumn[] auxCols, DJCrosstabRow[] auxRows, JRDesignExpression measureExp, DJCrosstabMeasure djmeasure,
			DJCrosstabColumn crosstabColumn, DJCrosstabRow crosstabRow, String meausrePrefix) {

		final StringBuilder rowValuesExp = new StringBuilder("new Object[]{");
		final StringBuilder rowPropsExp = new StringBuilder("new String[]{");
		for (int i = 0; i < auxRows.length; i++) {
			if (auxRows[i].getProperty()== null) {
                continue;
            }
			rowValuesExp.append("$V{").append(auxRows[i].getProperty().getProperty()).append("}");
			rowPropsExp.append("\"").append(auxRows[i].getProperty().getProperty()).append("\"");
			if (((i+1)<auxRows.length) && (auxRows[i+1].getProperty()!= null)){
				rowValuesExp.append(", ");
				rowPropsExp.append(", ");
			}
		}
		rowValuesExp.append("}");
		rowPropsExp.append("}");

		final StringBuilder colValuesExp = new StringBuilder("new Object[]{");
		final StringBuilder colPropsExp = new StringBuilder("new String[]{");
		for (int i = 0; i < auxCols.length; i++) {
			if (auxCols[i].getProperty()== null) {
                continue;
            }
			colValuesExp.append("$V{").append(auxCols[i].getProperty().getProperty()).append("}");
			colPropsExp.append("\"").append(auxCols[i].getProperty().getProperty()).append("\"");
			if (((i+1)<auxCols.length) && (auxCols[i+1].getProperty()!= null)){
				colValuesExp.append(", ");
				colPropsExp.append(", ");
			}
		}
		colValuesExp.append("}");
		colPropsExp.append("}");

		final String measureProperty = meausrePrefix + djmeasure.getProperty().getProperty();
		final String expText = "(((" + DJCRosstabMeasurePrecalculatedTotalProvider.class.getName() + ")$P{crosstab-measure__" + measureProperty + "_totalProvider}).getValueFor( " + colPropsExp.append(", ").append(colValuesExp.append(", ").append(rowPropsExp.append(", ").append(rowValuesExp.append(" ))").toString()).toString()).toString()).toString();

		if (djmeasure.getValueFormatter() != null){

			final String fieldsMap = ExpressionUtils.getTextForFieldsFromScriptlet();
			final String parametersMap = ExpressionUtils.getTextForParametersFromScriptlet();
			final String variablesMap = ExpressionUtils.getTextForVariablesFromScriptlet();

			final String stringExpression = "((("+DJValueFormatter.class.getName()+")$P{crosstab-measure__"+measureProperty+"_vf}).evaluate( "
				+ "("+expText+"), " + fieldsMap +", " + variablesMap + ", " + parametersMap +" ))";

			measureExp.setText(stringExpression);
		} else {

//			String expText = "((("+DJCRosstabMeasurePrecalculatedTotalProvider.class.getName()+")$P{crosstab-measure__"+djmeasure.getProperty().getProperty()+"_totalProvider}).getValueFor( "
//			+ colPropsExp +", "
//			+ colValuesExp +", "
//			+ rowPropsExp
//			+ ", "
//			+ rowValuesExp
//			+" ))";
//
			log.debug("text for crosstab total provider is: " + expText);

			measureExp.setText(expText);
//			measureExp.setValueClassName(djmeasure.getValueFormatter().getClassName());
			final String valueClassNameForOperation = ExpressionUtils.getValueClassNameForOperation(djmeasure.getOperation(),djmeasure.getProperty());
		}

	}

	private void setUpConditionStyles(JRDesignStyle jrstyle, DJCrosstabMeasure djmeasure, String columExpression) {
		if (Utils.isEmpty(djmeasure.getConditionalStyles())) {
            return;
        }

		for (final ConditionalStyle condition : djmeasure.getConditionalStyles()) {
			final JRDesignExpression expression = ExpressionUtils.getExpressionForConditionalStyle(condition, columExpression);
			final JRDesignConditionalStyle condStyle = layoutManager.makeConditionalStyle( condition.getStyle());
			condStyle.setConditionExpression(expression);
			jrstyle.addConditionalStyle(condStyle);
		}
	}

	/**
	 * MOVED INSIDE ExpressionUtils
	 *
	protected JRDesignExpression getExpressionForConditionalStyle(ConditionalStyle condition, String columExpression) {
		String fieldsMap = "(("+DJDefaultScriptlet.class.getName() + ")$P{REPORT_SCRIPTLET}).getCurrentFields()";
		String parametersMap = "(("+DJDefaultScriptlet.class.getName() + ")$P{REPORT_SCRIPTLET}).getCurrentParams()";
		String variablesMap = "(("+DJDefaultScriptlet.class.getName() + ")$P{REPORT_SCRIPTLET}).getCurrentVariables()";

		String evalMethodParams =  fieldsMap +", " + variablesMap + ", " + parametersMap + ", " + columExpression;

		String text = "(("+ConditionStyleExpression.class.getName()+")$P{" + JRParameter.REPORT_PARAMETERS_MAP + "}.get(\""+condition.getName()+"\"))."+CustomExpression.EVAL_METHOD_NAME+"("+evalMethodParams+")";
		JRDesignExpression expression = new JRDesignExpression();
		expression.setValueClass(Boolean.class);
		expression.setText(text);
		return expression;
	}
	 */

	private Style getRowTotalStyle(DJCrosstabRow crosstabRow) {
		return crosstabRow.getTotalStyle() == null ? djcross.getRowTotalStyle(): crosstabRow.getTotalStyle();
	}

	private Style getColumnTotalStyle(DJCrosstabColumn crosstabColumnRow) {
		return crosstabColumnRow.getTotalStyle() == null ? djcross.getColumnTotalStyle(): crosstabColumnRow.getTotalStyle();
	}

	/**
	 * Aplies background coloring upon the matrix described in {@link Dj2JrCrosstabBuilder#createCells}}
	 * @param contents
	 * @param crosstabRow
	 * @param crosstabColumn
	 * @param i
	 * @param j
	 */
	private void applyBackgroundColor(JRDesignCellContents contents, DJCrosstabRow crosstabRow,
			DJCrosstabColumn crosstabColumn, int i,int j) {

		final Color color = colors[i][j];

		contents.setBackcolor(color);
	}

	private void registerMeasures() {
		int measureIdx = 0;
		for (final Iterator<DJCrosstabMeasure> iterator = djcross.getMeasures().iterator(); iterator.hasNext(); measureIdx++) {
			final DJCrosstabMeasure djmeasure = iterator.next();
			final String meausrePrefix = "idx" + measureIdx + "_";
			final JRDesignCrosstabMeasure measure = new JRDesignCrosstabMeasure();

			measure.setName(meausrePrefix + djmeasure.getProperty().getProperty()); //makes the measure.name unique in this crosstab
			measure.setCalculation(CalculationEnum.values()[djmeasure.getOperation().getValue()]);
			measure.setValueClassName(djmeasure.getProperty().getValueClassName());
			final JRDesignExpression valueExp = new JRDesignExpression();
			valueExp.setText("$F{"+djmeasure.getProperty().getProperty()+"}");
			measure.setValueExpression(valueExp);

			if (djmeasure.getIncrementerFactoryClassName() != null) {
				measure.setIncrementerFactoryClassName(djmeasure.getIncrementerFactoryClassName());
			}

			/*
			  PRUEBA PORCENTAGE
			 */
			if (djmeasure.getIsPercentage()) {

				Class valueCass;
				try {
					valueCass = Class.forName(djmeasure.getProperty().getValueClassName());
					measure.setPercentageCalculatorClassName(JRPercentageCalculatorFactory.getPercentageCalculator(null, valueCass).getClass().getName());
					measure.setPercentageType( CrosstabPercentageEnum.GRAND_TOTAL );
				} catch (final ClassNotFoundException e1) {
					e1.printStackTrace();
				}
			}

			if (djmeasure.getValueFormatter() != null){
				/*
				  No need to declare parameter in the report design, just in the crosstab, otherwise duplicated
				  parameter exception may ocurr if many crosstabs are introduced in the report.
				 */
//				JRDesignParameter dparam = new JRDesignParameter();
//				dparam.setName("crosstab-measure__" + measure.getName() + "_vf"); //value formater suffix
//				dparam.setValueClassName(DJValueFormatter.class.getName());

				final JRDesignCrosstabParameter crosstabParameter = new JRDesignCrosstabParameter();
				crosstabParameter.setName("crosstab-measure__" + measure.getName() + "_vf"); //value formater suffix
				crosstabParameter.setValueClassName(DJValueFormatter.class.getName());

				log.debug("Registering value formatter parameter for crosstab measure " + crosstabParameter.getName() );
				try {
//					design.addParameter(dparam);
					jrcross.addParameter(crosstabParameter);
				} catch (final JRException e) {
					throw new EntitiesRegistrationException(e.getMessage(),e);
				}
				((DynamicJasperDesign)design).getParametersWithValues().put(crosstabParameter.getName(), djmeasure.getValueFormatter());
			}

			if (djmeasure.getPrecalculatedTotalProvider() != null){
				/*
				  No need to declare parameter in the report design, just in the crosstab, otherwise duplicated
				  parameter exception may ocurr if many crosstabs are introduced in the report.
				 */

//				JRDesignParameter dparam = new JRDesignParameter();
//				dparam.setName("crosstab-measure__" + measure.getName() + "_totalProvider"); //value formater suffix
//				dparam.setValueClassName(DJCRosstabMeasurePrecalculatedTotalProvider.class.getName());

				final JRDesignCrosstabParameter crosstabParameter = new JRDesignCrosstabParameter();
				crosstabParameter.setName("crosstab-measure__" + measure.getName() + "_totalProvider"); //value formater suffix
				crosstabParameter.setValueClassName(DJCRosstabMeasurePrecalculatedTotalProvider.class.getName());

				log.debug("Registering crosstab total provider parameter for measure " + crosstabParameter.getName() );
				try {
//					design.addParameter(dparam);
					jrcross.addParameter(crosstabParameter);
				} catch (final JRException e) {
					throw new EntitiesRegistrationException(e.getMessage(),e);
				}
				((DynamicJasperDesign)design).getParametersWithValues().put(crosstabParameter.getName(), djmeasure.getPrecalculatedTotalProvider());
			}

			try {
				jrcross.addMeasure(measure);
			} catch (final JRException e) {
				log.error(e.getMessage(),e);
			}
		}
	}

	/**
	 * Register the Rowgroup buckets and places the header cells for the rows
	 */
	private void registerRows() {
		final DynamicJasperDesign djdesign = (DynamicJasperDesign) design;
		for (int i =  0; i < rows.length; i++) {
			final DJCrosstabRow crosstabRow = rows[i];

			final JRDesignCrosstabRowGroup ctRowGroup = new JRDesignCrosstabRowGroup();

			ctRowGroup.setWidth(crosstabRow.getHeaderWidth());

			ctRowGroup.setName(crosstabRow.getProperty().getProperty());

			final JRDesignCrosstabBucket rowBucket = new JRDesignCrosstabBucket();

			final Comparator comparator = crosstabRow.getComparator();
			if (comparator != null){
				final String comparatorParamName = ExpressionUtils.createParameterName("crosstab_column_comparator", comparator, crosstabRow.getProperty().getProperty());
				LayoutUtils.registerAndAddParameter(djdesign, comparatorParamName, Comparator.class.getName(), comparator);
				final JRDesignExpression comparatorExpression = ExpressionUtils.createExpression("$P{"+comparatorParamName+"}", Comparator.class);
				rowBucket.setComparatorExpression(comparatorExpression);
			}

            //New in JR 4.1+
            rowBucket.setValueClassName(crosstabRow.getProperty().getValueClassName());

			ctRowGroup.setBucket(rowBucket);

			final JRDesignExpression bucketExp = ExpressionUtils.createExpression("$F{"+crosstabRow.getProperty().getProperty()+"}", crosstabRow.getProperty().getValueClassName());
			rowBucket.setExpression(bucketExp);

			final JRDesignCellContents rowHeaderContents = new JRDesignCellContents();
			final JRDesignTextField rowTitle = new JRDesignTextField();

			final JRDesignExpression rowTitExp = new JRDesignExpression();
			rowTitExp.setText("$V{"+crosstabRow.getProperty().getProperty()+"}");

			rowTitle.setExpression(rowTitExp);
			rowTitle.setWidth(crosstabRow.getHeaderWidth());

			//The width can be the sum of the with of all the rows starting from the current one, up to the inner most one.
			final int auxHeight = getRowHeaderMaxHeight(crosstabRow);
//			int auxHeight = crosstabRow.getHeight(); //FIXME getRowHeaderMaxHeight() must be FIXED because it breaks when 1rs row shows total and 2nd doesn't
			rowTitle.setHeight(auxHeight);

			final Style headerstyle = crosstabRow.getHeaderStyle() == null ? djcross.getRowHeaderStyle(): crosstabRow.getHeaderStyle();

			if (headerstyle != null){
				layoutManager.applyStyleToElement(headerstyle, rowTitle);
				rowHeaderContents.setBackcolor(headerstyle.getBackgroundColor());
			}

			rowHeaderContents.addElement(rowTitle);
			rowHeaderContents.setMode( ModeEnum.OPAQUE );

			final boolean fullBorder = i <= 0; //Only outer most will have full border
			applyCellBorder(rowHeaderContents, false, fullBorder);

			ctRowGroup.setHeader(rowHeaderContents );

			if (crosstabRow.isShowTotals()) {
                createRowTotalHeader(ctRowGroup,crosstabRow,fullBorder);
            }


			try {
				jrcross.addRowGroup(ctRowGroup);
			} catch (final JRException e) {
				log.error(e.getMessage(),e);
			}

		}
	}

	/**
	 * @param crosstabRow
	 * @return
	 */
	private int getRowHeaderMaxHeight(DJCrosstabRow crosstabRow) {
		int auxHeight = crosstabRow.getHeight();
		boolean found = false;
		for (final DJCrosstabRow row : djcross.getRows()) {
			if (!row.equals(crosstabRow) && !found){
				continue;
			} else {
				found = true;
			}

			if (row.equals(crosstabRow)) {
                continue;
            }

			if (row.isShowTotals()) {
                auxHeight += row.getHeight();
            }

		}
		return auxHeight;
	}

	/**
	 * Registers the Columngroup Buckets and creates the header cell for the columns
	 */
	private void registerColumns() {
		final DynamicJasperDesign djdesign = (DynamicJasperDesign) design;
		for (int i = 0; i < cols.length; i++) {
			final DJCrosstabColumn crosstabColumn = cols[i];

			final JRDesignCrosstabColumnGroup ctColGroup = new JRDesignCrosstabColumnGroup();
			ctColGroup.setName(crosstabColumn.getProperty().getProperty());
			ctColGroup.setHeight(crosstabColumn.getHeaderHeight());

			final JRDesignCrosstabBucket bucket = new JRDesignCrosstabBucket();

			if (crosstabColumn.getComparator() != null){
				final String comparatorParamName = ExpressionUtils.createParameterName("crosstab_column_comparator", crosstabColumn.getComparator(), crosstabColumn.getProperty().getProperty());
				LayoutUtils.registerAndAddParameter(djdesign, comparatorParamName, Comparator.class.getName(), crosstabColumn.getComparator());
				final JRDesignExpression comparatorExpression = ExpressionUtils.createExpression("$P{"+comparatorParamName+"}", Comparator.class.getName());
				bucket.setComparatorExpression(comparatorExpression);
			}

            bucket.setValueClassName(crosstabColumn.getProperty().getValueClassName());

			final JRDesignExpression bucketExp = ExpressionUtils.createExpression("$F{"+crosstabColumn.getProperty().getProperty()+"}", crosstabColumn.getProperty().getValueClassName());
			bucket.setExpression(bucketExp);

			ctColGroup.setBucket(bucket);

			final JRDesignCellContents colHeaerContent = new JRDesignCellContents();
			final JRDesignTextField colTitle = new JRDesignTextField();

			final JRDesignExpression colTitleExp = new JRDesignExpression();
			colTitleExp.setText("$V{"+crosstabColumn.getProperty().getProperty()+"}");


			colTitle.setExpression(colTitleExp);
			colTitle.setWidth(crosstabColumn.getWidth());
			colTitle.setHeight(crosstabColumn.getHeaderHeight());

			//The height can be the sum of the heights of all the columns starting from the current one, up to the inner most one.
			final int auxWidth = calculateRowHeaderMaxWidth(crosstabColumn);
			colTitle.setWidth(auxWidth);

			final Style headerstyle = crosstabColumn.getHeaderStyle() == null ? djcross.getColumnHeaderStyle(): crosstabColumn.getHeaderStyle();

			if (headerstyle != null){
				layoutManager.applyStyleToElement(headerstyle,colTitle);
				colHeaerContent.setBackcolor(headerstyle.getBackgroundColor());
			}


			colHeaerContent.addElement(colTitle);
			colHeaerContent.setMode( ModeEnum.OPAQUE );

			final boolean fullBorder = i <= 0; //Only outer most will have full border
			applyCellBorder(colHeaerContent, fullBorder, false);

			ctColGroup.setHeader(colHeaerContent);

			if (crosstabColumn.isShowTotals()) {
                createColumTotalHeader(ctColGroup,crosstabColumn,fullBorder);
            }


			try {
				jrcross.addColumnGroup(ctColGroup);
			} catch (final JRException e) {
				log.error(e.getMessage(),e);
			}
		}
	}

	/**
	 * The max possible width can be calculated doing the sum of of the inner cells and its totals
	 * @param crosstabColumn
	 * @return
	 */
	private int calculateRowHeaderMaxWidth(DJCrosstabColumn crosstabColumn) {
		int auxWidth = 0;
		boolean firstTime = true;
		final List<DJCrosstabColumn> auxList = new ArrayList<>(djcross.getColumns());
		Collections.reverse(auxList);
		for (final DJCrosstabColumn col : auxList) {
			if (col.equals(crosstabColumn)){
				if (auxWidth == 0) {
                    auxWidth = col.getWidth();
                }
				break;
			}

			if (firstTime){
				auxWidth += col.getWidth();
				firstTime = false;
			}
			if (col.isShowTotals()) {
				auxWidth += col.getWidth();
			}
		}
		return auxWidth;
	}


	private void createRowTotalHeader(JRDesignCrosstabRowGroup ctRowGroup, DJCrosstabRow crosstabRow, boolean fullBorder) {
		final JRDesignCellContents totalHeaderContent = new JRDesignCellContents();
		ctRowGroup.setTotalHeader(totalHeaderContent);
		ctRowGroup.setTotalPosition( CrosstabTotalPositionEnum.END ); //FIXME the total can be at the end of a group or at the beginin


		final Style totalHeaderstyle = crosstabRow.getTotalHeaderStyle() == null ? djcross.getRowTotalheaderStyle(): crosstabRow.getTotalHeaderStyle();

		totalHeaderContent.setMode( ModeEnum.OPAQUE );

		final JRDesignTextField element = new JRDesignTextField();

		JRDesignExpression exp;
		if (crosstabRow.getTotalLegend() != null) {
			exp = ExpressionUtils.createExpression("\""+crosstabRow.getTotalLegend()+"\"",String.class);
		} else {
			exp = ExpressionUtils.createExpression("\"Total "+crosstabRow.getTitle()+"\"",String.class);
		}

		element.setExpression(exp);
		element.setHeight(crosstabRow.getHeight());

		if (totalHeaderstyle != null) {
			totalHeaderContent.setBackcolor(totalHeaderstyle.getBackgroundColor());
			layoutManager.applyStyleToElement(totalHeaderstyle, element);
		}

		//The width can be the sum of the width of all the rows starting from the current one, up to the inner most one.
		int auxWidth = 0;
		boolean found = false;
		for (final DJCrosstabRow row : djcross.getRows()) {
			if (!row.equals(crosstabRow) && !found){
				continue;
			} else {
				found = true;
			}
			auxWidth += row.getHeaderWidth();
		}
		element.setWidth(auxWidth);

		applyCellBorder(totalHeaderContent, false, fullBorder);

		totalHeaderContent.addElement(element);
	}

	/**
	 * @param cellContent
	 * @param topBorder if true, border settings is applied on all sides. if not, only in
	 * bottom and right sides.
	 */
	private void applyCellBorder(JRDesignCellContents cellContent, boolean topBorder, boolean leftBorder) {
        final Border cellBorder = djcross.getCellBorder();
        if ((cellBorder != null) && (cellBorder.getWidth() != 0f)){
			final int lineStyle = cellBorder.getLineStyle();

            //Bottom border
            LayoutUtils.convertBorderToPen(cellBorder, cellContent.getLineBox().getBottomPen());

            //Right border
            LayoutUtils.convertBorderToPen(cellBorder, cellContent.getLineBox().getRightPen());

			if (topBorder){
                LayoutUtils.convertBorderToPen(cellBorder, cellContent.getLineBox().getTopPen());
			}
			if (leftBorder){
                LayoutUtils.convertBorderToPen(cellBorder, cellContent.getLineBox().getLeftPen());
			}
		}
	}

	private void createColumTotalHeader(JRDesignCrosstabColumnGroup ctColGroup, DJCrosstabColumn crosstabColumn, boolean fullBorder) {
		final JRDesignCellContents totalHeaderContent = new JRDesignCellContents();
		ctColGroup.setTotalHeader(totalHeaderContent);
		ctColGroup.setTotalPosition( CrosstabTotalPositionEnum.END );

		final Style totalHeaderstyle = crosstabColumn.getTotalHeaderStyle() == null ? djcross.getColumnTotalheaderStyle(): crosstabColumn.getTotalHeaderStyle();

		totalHeaderContent.setMode( ModeEnum.OPAQUE );

		JRDesignExpression exp;
		if (crosstabColumn.getTotalLegend() != null) {
			exp = ExpressionUtils.createExpression("\""+crosstabColumn.getTotalLegend()+"\"",String.class);
		} else {
			exp = ExpressionUtils.createExpression("\"Total "+crosstabColumn.getTitle()+"\"",String.class);
		}
		final JRDesignTextField element = new JRDesignTextField();
		element.setExpression(exp);
		element.setWidth(crosstabColumn.getWidth());


		if (totalHeaderstyle != null) {
			layoutManager.applyStyleToElement(totalHeaderstyle, element);
			totalHeaderContent.setBackcolor(totalHeaderstyle.getBackgroundColor());
		}

		//The height can be the sum of the heights of all the columns starting from the current one, up to the inner most one.
		int auxWidth = 0;
		boolean found = false;
		for (final DJCrosstabColumn col : djcross.getColumns()) {
			if (!col.equals(crosstabColumn) && !found){
				continue;
			} else {
				found = true;
			}

			auxWidth += col.getHeaderHeight();
		}

		element.setHeight(auxWidth);

		applyCellBorder(totalHeaderContent, fullBorder, false);

		totalHeaderContent.addElement(element);
	}

}
