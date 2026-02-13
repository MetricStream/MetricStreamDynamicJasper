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

package ar.com.fdvs.dj.core.registration;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ar.com.fdvs.dj.core.layout.LayoutManager;
import ar.com.fdvs.dj.domain.DJCalculation;
import ar.com.fdvs.dj.domain.DJValueFormatter;
import ar.com.fdvs.dj.domain.DynamicJasperDesign;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import ar.com.fdvs.dj.domain.entities.DJGroupVariable;
import ar.com.fdvs.dj.domain.entities.Entity;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.columns.ExpressionColumn;
import ar.com.fdvs.dj.domain.entities.columns.PercentageColumn;
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn;
import ar.com.fdvs.dj.util.LayoutUtils;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignGroup;
import net.sf.jasperreports.engine.design.JRDesignParameter;
import net.sf.jasperreports.engine.design.JRDesignVariable;
import net.sf.jasperreports.engine.type.CalculationEnum;
import net.sf.jasperreports.engine.type.ResetTypeEnum;

/**
 * Manager invoked to register variables for groups of columns. <br>
 * A ColumnsGroupVariable is read and transformed into a JRDesignVariable. <br>
 * <br>
 * @see DJGroup
 */
public class ColumnsGroupVariablesRegistrationManager extends AbstractEntityRegistrationManager {

	private static final Log log = LogFactory.getLog(ColumnsGroupVariablesRegistrationManager.class);

    /**
     * It can be "header" o "footer"
     */
	private final String type;
	private final String columnToGroupByProperty;

	public ColumnsGroupVariablesRegistrationManager(String type, String columnToGroupByProperty, DynamicJasperDesign jd,  DynamicReport dr, LayoutManager layoutManager) {
		super(jd,dr,layoutManager);
		this.type = type;
		this.columnToGroupByProperty = columnToGroupByProperty;
	}

	@Override
    protected void registerEntity(Entity entity) {
		final DJGroupVariable columnsGroupVariable = (DJGroupVariable) entity;
		try {
			final String name = getDjd().getName() +"_" + columnsGroupVariable.getColumnToApplyOperation().getGroupVariableName(type, columnToGroupByProperty);
			columnsGroupVariable.setName(name);
			if (columnsGroupVariable.getValueExpression() == null) {
				final JRDesignVariable jrVariable = (JRDesignVariable)transformEntity(entity);

				log.debug("registering group variable " + jrVariable.getName() + " (" + jrVariable.getValueClassName() + ")");
				getDjd().addVariable(jrVariable);

				registerValueFormatter( columnsGroupVariable, jrVariable.getName() );
			}
			else {
				registerCustomExpressionParameter(name + "_valueExpression", columnsGroupVariable.getValueExpression());
			}

			if (columnsGroupVariable.getPrintWhenExpression() != null){
				registerCustomExpressionParameter(name + "_printWhenExpression", columnsGroupVariable.getPrintWhenExpression());
			}
			if ((columnsGroupVariable.getLabel() != null) && (columnsGroupVariable.getLabel().getLabelExpression() != null)) {
				registerCustomExpressionParameter(name + "_labelExpression", columnsGroupVariable.getLabel().getLabelExpression());
			}

		} catch (final JRException e) {
			throw new EntitiesRegistrationException(e.getMessage(),e);
		}
	}

	/**
	 * Registers the parameter for the value formatter for the given variable and puts
	 * it's implementation in the parameters map.
	 * @param djVariable
	 * @param variableName
	 */
	protected void registerValueFormatter(DJGroupVariable djVariable, String variableName) {
		if ( djVariable.getValueFormatter() == null){
			return;
		}

		final JRDesignParameter dparam = new JRDesignParameter();
		dparam.setName(variableName + "_vf"); //value formater suffix
		dparam.setValueClassName(DJValueFormatter.class.getName());
		log.debug("Registering value formatter parameter for property " + dparam.getName() );
		try {
			getDjd().addParameter(dparam);
		} catch (final JRException e) {
			throw new EntitiesRegistrationException(e.getMessage(),e);
		}
		getDjd().getParametersWithValues().put(dparam.getName(), djVariable.getValueFormatter());

	}

	@Override
    protected Object transformEntity(Entity entity) {

		final DJGroupVariable groupVariable = (DJGroupVariable) entity;
		final AbstractColumn col = groupVariable.getColumnToApplyOperation();

        final String variableName =  groupVariable.getName();
        log.debug("transforming group variable " +variableName);
		final DJCalculation op = groupVariable.getOperation();

		final JRDesignExpression expression = new JRDesignExpression();

		//only variables from the last registered group are important now
		final List<JRGroup> groupsList = getDjd().getGroupsList();
		JRDesignGroup registeredGroup = (JRDesignGroup)groupsList.get(groupsList.size()-1);

		if ((col instanceof ExpressionColumn) && (((ExpressionColumn)col).getExpressionForCalculation() != null)){
			final ExpressionColumn expcol = (ExpressionColumn)col;
			expression.setText(expcol.getTextForExpressionForCalculartion());
		}
		else if (col instanceof PercentageColumn) {
			final PercentageColumn pcol = (PercentageColumn) col;
			expression.setText(pcol.getPercentageColumn().getTextForExpression());

			final DJGroup djgroup = groupVariable.getGroup();
			registeredGroup = LayoutUtils.findParentJRGroup(djgroup, getDynamicReport(), getDjd(), getLayoutManager());
		} else if (col.getTextFormatter() != null){
        	final PropertyColumn pcol = (PropertyColumn) col;
        	expression.setText("$F{" + pcol.getColumnProperty().getProperty() + "}");
        } else {
        	expression.setText(col.getTextForExpression());
        }

		final JRDesignVariable variable = new JRDesignVariable();
		variable.setExpression(expression);
		variable.setCalculation(CalculationEnum.values()[groupVariable.getOperation().getValue()]);
		variable.setName(variableName);

		variable.setResetType(ResetTypeEnum.GROUP );
		variable.setResetGroup(registeredGroup.getName());

		final String valueClassName = col.getVariableClassName(op);
		final String initialExpression = col.getInitialExpression(op);

//		if (DJCalculation.SYSTEM.equals(groupVariable.getOperation())){
//			variable.setValueClassName(Object.class.getName());
//		} else {
//		}
		variable.setValueClassName(valueClassName);

		if (initialExpression != null){
			final JRDesignExpression initialExp = new JRDesignExpression();
			initialExp.setText(initialExpression);
			variable.setInitialValueExpression(initialExp);
		}

		final Class<?> incrementerFactoryClass = groupVariable.getIncrementerFactoryClass();
		if (incrementerFactoryClass != null) {
			variable.setIncrementerFactoryClass(incrementerFactoryClass);
		}

		return variable;
	}

}
