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

package ar.com.fdvs.dj.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Collection;

import ar.com.fdvs.dj.core.DJConstants;
import ar.com.fdvs.dj.core.DJDefaultScriptlet;
import ar.com.fdvs.dj.core.DJException;
import ar.com.fdvs.dj.domain.ColumnProperty;
import ar.com.fdvs.dj.domain.CustomExpression;
import ar.com.fdvs.dj.domain.DJCalculation;
import ar.com.fdvs.dj.domain.DJDataSource;
import ar.com.fdvs.dj.domain.DynamicJasperDesign;
import ar.com.fdvs.dj.domain.customexpression.DJSimpleExpression;
import ar.com.fdvs.dj.domain.entities.Subreport;
import ar.com.fdvs.dj.domain.entities.SubreportParameter;
import ar.com.fdvs.dj.domain.entities.conditionalStyle.ConditionStyleExpression;
import ar.com.fdvs.dj.domain.entities.conditionalStyle.ConditionalStyle;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JasperDesign;

public class ExpressionUtils {

    private static final String REPORT_PARAMETERS_MAP = "$P{REPORT_PARAMETERS_MAP}";

    /**
     * Returns an expression that points to a java.util.Map object with the parameters to
     * be used during the subreport fill time.
     * Posibilities are:<br>
     * - Use Partent report Map<br>
     * - Use a Map that is a parameter of the partents map<br>
     * - Use a property of the current row.
     *
     * @param sr
     * @return
     */
    public static JRDesignExpression getParameterExpression(Subreport sr) {
        final JRDesignExpression exp = new JRDesignExpression();
        if (sr.isUseParentReportParameters()) {
            exp.setText(REPORT_PARAMETERS_MAP);
            return exp;
        }

        if (sr.getParametersExpression() == null) {
            return null;
        }

        if (sr.getParametersMapOrigin() == DJConstants.SUBREPORT_PARAMETER_MAP_ORIGIN_PARAMETER) {
            exp.setText(REPORT_PARAMETERS_MAP + ".get( \"" + sr.getParametersExpression() + "\" )");
            return exp;
        }

        if (sr.getParametersMapOrigin() == DJConstants.SUBREPORT_PARAMETER_MAP_ORIGIN_FIELD) {
            exp.setText("$F{" + sr.getParametersExpression() + "}");
            return exp;
        }

        return null;
    }

    /**
     * Returns the expression string required
     *
     * @param ds
     * @return
     */
    public static JRDesignExpression getDataSourceExpression(DJDataSource ds) {
        final JRDesignExpression exp = new JRDesignExpression();

        final String dsType = getDataSourceTypeStr(ds.getDataSourceType());
        String expText = null;
        if (ds.getDataSourceOrigin() == DJConstants.DATA_SOURCE_ORIGIN_FIELD) {
            expText = dsType + "$F{" + ds.getDataSourceExpression() + "})";
        } else if ((ds.getDataSourceOrigin() == DJConstants.DATA_SOURCE_ORIGIN_PARAMETER) || (ds.getDataSourceOrigin() == DJConstants.DATA_SOURCE_TYPE_SQL_CONNECTION)) {
            expText = dsType + REPORT_PARAMETERS_MAP + ".get( \"" + ds.getDataSourceExpression() + "\" ) )";
        } else if (ds.getDataSourceOrigin() == DJConstants.DATA_SOURCE_ORIGIN_REPORT_DATASOURCE) {
            expText = "((" + JRDataSource.class.getName() + ") $P{REPORT_DATA_SOURCE})";
        } else if (ds.getDataSourceOrigin() == DJConstants.DATA_SOURCE_ORIGIN_INLINE) {
        	expText = ds.getDataSourceExpression();
        }

        exp.setText(expText);

        return exp;
    }

    public static JRDesignExpression getConnectionExpression(DJDataSource ds) {
        final JRDesignExpression exp = new JRDesignExpression();

        final String dsType = getDataSourceTypeStr(ds.getDataSourceType());
        final String expText = dsType + REPORT_PARAMETERS_MAP + ".get( \"" + ds.getDataSourceExpression() + "\" ) )";

        exp.setText(expText);

        return exp;
    }

    /**
     * Returns a JRDesignExpression that points to the main report connection
     *
     * @return
     */
    public static JRDesignExpression getReportConnectionExpression() {
        final JRDesignExpression connectionExpression = new JRDesignExpression();
        connectionExpression.setText("$P{" + JRParameter.REPORT_CONNECTION + "}");
        return connectionExpression;
    }

    protected static String getDataSourceTypeStr(int datasourceType) {
        //TODO Complete all other possible types
        String dsType = "(";
        switch (datasourceType) {
            case DJConstants.DATA_SOURCE_TYPE_COLLECTION:
                dsType = "new " + JRBeanCollectionDataSource.class.getName() + "((java.util.Collection)";
                break;
            case DJConstants.DATA_SOURCE_TYPE_ARRAY:
                dsType = "new " + JRBeanArrayDataSource.class.getName() + "((Object[])";
                break;
            case DJConstants.DATA_SOURCE_TYPE_RESULTSET:
                dsType = "new " + JRResultSetDataSource.class.getName() + "((" + ResultSet.class.getName() + ")";
                break;
            case DJConstants.DATA_SOURCE_TYPE_JRDATASOURCE:
                dsType = "((" + JRDataSource.class.getName() + ")";
                break;
            case DJConstants.DATA_SOURCE_TYPE_SQL_CONNECTION:
                dsType = "((" + Connection.class.getName() + ")";
                break;
            default:
                break;
        }
        return dsType;
    }

    public static JRDesignExpression createStringExpression(String text) {
        final JRDesignExpression exp = new JRDesignExpression();
        exp.setText(text);
        return exp;
    }

    public static JRDesignExpression createExpression(String text, Class clazz) {
        final JRDesignExpression exp = new JRDesignExpression();
        exp.setText(text);
        return exp;
    }

    public static JRDesignExpression createExpression(String text, String className) {
        final JRDesignExpression exp = new JRDesignExpression();
        exp.setText(text);
        return exp;
    }

    public static JRDesignExpression createExpression(JasperDesign jasperDesign, SubreportParameter sp) {
        final JRDesignExpression exp = new JRDesignExpression();
        String text;
        if (sp.getParameterOrigin() == DJConstants.SUBREPORT_PARAM_ORIGIN_FIELD) {
            text = "$F{" + sp.getExpression() + "}";
        } else if (sp.getParameterOrigin() == DJConstants.SUBREPORT_PARAM_ORIGIN_PARAMETER) {
            text = REPORT_PARAMETERS_MAP + ".get( \"" + sp.getExpression() + "\")";
        } else if (sp.getParameterOrigin() == DJConstants.SUBREPORT_PARAM_ORIGIN_VARIABLE) {
            text = "$V{" + sp.getExpression() + "}";
        } else { //CUSTOM
            text = sp.getExpression();
        }
        exp.setText(text);
        return exp;
    }

    public static JRDesignExpression createAndRegisterExpression(DynamicJasperDesign design, String name, CustomExpression expression) {
        LayoutUtils.registerCustomExpressionParameter(design, name, expression);
        return createExpression(name, expression, false);
    }

    /**
     * Use {@link #createAndRegisterExpression(DynamicJasperDesign, String, CustomExpression)}
     * This deprecated version may cause wrong field values when expression is executed in a group footer
     * @param name
     * @param expression
     * @return
     */
    @Deprecated
    public static JRDesignExpression createExpression(String name, CustomExpression expression) {
        return createExpression(name, expression, false);
    }

    /**
     *
     * @param name
     * @param expression
     * @param usePreviousFieldValues if true, the Map with field values passed to the CustomExpresion contains the previous
     *                               field value. This is needed when in group footer bands were at the time of executing the
     *                               CustomExpression the fields values already corresponds to the next group value.
     * @return
     */
    public static JRDesignExpression createExpression(String name, CustomExpression expression, boolean usePreviousFieldValues) {
        final String text = ExpressionUtils.createCustomExpressionInvocationText(expression, name, usePreviousFieldValues);
        return createExpression(text, expression.getClassName());
    }

    /**
     * @return
     */
    public static String getFieldsMapExpression(Collection columnsAndFields) {
        final StringBuilder fieldsMap = new StringBuilder("new  " + PropertiesMap.class.getName() + "()");
        for (final Object columnsAndField : columnsAndFields) {
            final ColumnProperty columnProperty = (ColumnProperty) columnsAndField;

            if (columnProperty != null) {
                final String propname = columnProperty.getProperty();
                fieldsMap.append(".with(\"").append(propname).append("\",$F{").append(propname).append("})");
            }
        }

        return fieldsMap.toString();
    }

    /**
     * Collection of JRVariable
     *
     * @param variables
     * @return
     */
    public static String getVariablesMapExpression(Collection variables) {
        final StringBuilder variablesMap = new StringBuilder("new  " + PropertiesMap.class.getName() + "()");
        for (final Object variable : variables) {
            final JRVariable jrvar = (JRVariable) variable;
            final String varname = jrvar.getName();
            variablesMap.append(".with(\"").append(varname).append("\",$V{").append(varname).append("})");
        }
        return variablesMap.toString();
    }


    public static String getParametersMapExpression() {
        return "new  " + PropertiesMap.class.getName() + "($P{" + DJConstants.CUSTOM_EXPRESSION__PARAMETERS_MAP + "} )";
    }


    public static String createParameterName(String preffix, Object obj) {
        final String name = obj.toString().substring(obj.toString().lastIndexOf(".") + 1).replaceAll("[\\$@]", "_");
        return preffix + name;
    }

    public static String createParameterName(String preffix, Object obj, String suffix) {
        return createParameterName(preffix, obj) + suffix;
    }


    /**
     * If you register a CustomExpression with the name "customExpName", then this will create the text needed
     * to invoke it in a JRDesignExpression
     *
     * @param customExpName
     * @param usePreviousFieldValues
     * @return
     */
    public static String createCustomExpressionInvocationText(CustomExpression customExpression, String customExpName, boolean usePreviousFieldValues) {
        String stringExpression;
        if (customExpression instanceof DJSimpleExpression) {
            final DJSimpleExpression varexp = (DJSimpleExpression) customExpression;
            String symbol;
            switch (varexp.getType()) {
                case DJSimpleExpression.TYPE_FIELD:
                    symbol = "F";
                    break;
                case DJSimpleExpression.TYPE_VARIABLE:
                    symbol = "V";
                    break;
                case DJSimpleExpression.TYPE_PARAMATER:
                    symbol = "P";
                    break;
                default:
                    throw new DJException("Invalid DJSimpleExpression, type must be FIELD, VARIABLE or PARAMETER");
            }
            stringExpression = "$" + symbol + "{" + varexp.getVariableName() + "}";

        } else {
            String fieldsMap = "((" + DJDefaultScriptlet.class.getName() + ")$P{REPORT_SCRIPTLET}).getCurrentFields()";
            if (usePreviousFieldValues) {
                fieldsMap = "((" + DJDefaultScriptlet.class.getName() + ")$P{REPORT_SCRIPTLET}).getPreviousFields()";
            }

            final String parametersMap = "((" + DJDefaultScriptlet.class.getName() + ")$P{REPORT_SCRIPTLET}).getCurrentParams()";
            final String variablesMap = "((" + DJDefaultScriptlet.class.getName() + ")$P{REPORT_SCRIPTLET}).getCurrentVariables()";

            stringExpression = "((" + CustomExpression.class.getName() + ")$P{REPORT_PARAMETERS_MAP}.get(\"" + customExpName + "\"))."
                    + CustomExpression.EVAL_METHOD_NAME + "( " + fieldsMap + ", " + variablesMap + ", " + parametersMap + " )";
        }

        return stringExpression;
    }

    /**
     * Same as regular, but instead of invoking directly $P{REPORT_SCRIPTLET}, it does through the $P{REPORT_PARAMETERS_MAP}
     *
     * @param customExpName
     * @return
     */
    public static String createCustomExpressionInvocationText2(String customExpName) {

        final String fieldsMap = getTextForFieldsFromScriptlet();
        final String parametersMap = getTextForParametersFromScriptlet();
        final String variablesMap = getTextForVariablesFromScriptlet();

//		String stringExpression = "((("+CustomExpression.class.getName()+")$P{"+customExpName+"})."
//				+CustomExpression.EVAL_METHOD_NAME+"( "+ fieldsMap +", " + variablesMap + ", " + parametersMap +" ))";

        return "((" + CustomExpression.class.getName() + ")$P{REPORT_PARAMETERS_MAP}.get(\"" + customExpName + "\"))."
                + CustomExpression.EVAL_METHOD_NAME + "( " + fieldsMap + ", " + variablesMap + ", " + parametersMap + " )";
    }

    public static String getTextForVariablesFromScriptlet() {
        return "((" + DJDefaultScriptlet.class.getName() + ")$P{REPORT_PARAMETERS_MAP}.get(\"REPORT_SCRIPTLET\")).getCurrentVariables()";
    }

    public static String getTextForParametersFromScriptlet() {
        return "((" + DJDefaultScriptlet.class.getName() + ")$P{REPORT_PARAMETERS_MAP}.get(\"REPORT_SCRIPTLET\")).getCurrentParams()";
    }

    public static String getTextForFieldsFromScriptlet() {
        return "((" + DJDefaultScriptlet.class.getName() + ")$P{REPORT_PARAMETERS_MAP}.get(\"REPORT_SCRIPTLET\")).getCurrentFields()";
    }

    public static String getValueClassNameForOperation(DJCalculation calc, ColumnProperty prop) {
        if ((calc == DJCalculation.COUNT) || (calc == DJCalculation.DISTINCT_COUNT)) {
            return Number.class.getName();
        } else {
            return prop.getValueClassName();
        }

    }

    public static String getInitialValueExpressionForOperation(DJCalculation calc, ColumnProperty prop) {
        if ((calc == DJCalculation.COUNT) || (calc == DJCalculation.DISTINCT_COUNT)) {
            return "new java.lang.Long(\"0\")";
        } else if (calc == DJCalculation.SUM) {
            return "new " + prop.getValueClassName() + "(\"0\")";
        } else {
            return null;
        }

    }

    public static JRDesignExpression getExpressionForConditionalStyle(ConditionalStyle condition, String columExpression) {
        final String fieldsMap = "((" + DJDefaultScriptlet.class.getName() + ")$P{REPORT_SCRIPTLET}).getCurrentFields()";
        final String parametersMap = "((" + DJDefaultScriptlet.class.getName() + ")$P{REPORT_SCRIPTLET}).getCurrentParams()";
        final String variablesMap = "((" + DJDefaultScriptlet.class.getName() + ")$P{REPORT_SCRIPTLET}).getCurrentVariables()";

        final String evalMethodParams = fieldsMap + ", " + variablesMap + ", " + parametersMap + ", " + columExpression;

        final String text = "((" + ConditionStyleExpression.class.getName() + ")$P{" + JRParameter.REPORT_PARAMETERS_MAP + "}.get(\"" + condition.getName() + "\"))." + CustomExpression.EVAL_METHOD_NAME + "(" + evalMethodParams + ")";
        final JRDesignExpression expression = new JRDesignExpression();
        expression.setText(text);
        return expression;
    }
}
