package ar.com.fdvs.dj.core;

import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.DatasetPropertyExpression;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRQuery;
import net.sf.jasperreports.engine.JRScriptlet;
import net.sf.jasperreports.engine.JRScriptletException;
import net.sf.jasperreports.engine.JRSortField;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.fill.JRFillDataset;
import net.sf.jasperreports.engine.fill.JRFillField;
import net.sf.jasperreports.engine.fill.JRFillGroup;
import net.sf.jasperreports.engine.fill.JRFillParameter;
import net.sf.jasperreports.engine.fill.JRFillVariable;
import net.sf.jasperreports.engine.type.WhenResourceMissingTypeEnum;

/**
 * This class handles parameter passing to custom expressions in runtime (during report fill)
 *
 * @author mamana
 */
public class DJDefaultScriptlet extends JRDefaultScriptlet {

    public DJDefaultScriptlet() {
    }

    private static final Log logger = LogFactory.getLog(DJDefaultScriptlet.class);


    protected FieldMapWrapper fieldMapWrapper = new FieldMapWrapper();
    protected ParameterMapWrapper parameterMapWrapper = new ParameterMapWrapper();
    protected VariableMapWrapper variableMapWrapper = new VariableMapWrapper();

    public void setData(Map<String, JRFillParameter> parsm, Map<String,JRFillField> fldsm, Map<String,JRFillVariable> varsm, JRFillGroup[] grps) {
        final JRDataset dataset = new EmptyDataset(grps);
        final JRFillDataset fillDataset = new JRFillDataset(null, dataset, null);
        fillDataset.getParametersMap().putAll(parsm);
        fillDataset.getFieldsMap().putAll(fldsm);
        fillDataset.getVariablesMap().putAll(varsm);
        // leonel
        // super.setData(parsm, fldsm, varsm, grps);
        super.setData(fillDataset);
        putValuesInMap();
    }

    protected void putValuesInMap() {
        fieldMapWrapper.setMap(fieldsMap);
        parameterMapWrapper.setMap(parametersMap);
        variableMapWrapper.setMap(variablesMap);
    }

    public Map getCurrentFields() {
        return fieldMapWrapper;
    }

    public Map getPreviousFields() {
        return fieldMapWrapper.getPreviousValues();
    }

    public Map getCurrentParams() {
        return parameterMapWrapper;
    }

    public Map getCurrentVariables() {
        return variableMapWrapper;
    }

    @Override
    public void beforeReportInit() throws JRScriptletException {
        super.beforeReportInit();
        final JasperReport jr = (JasperReport) getParameterValue(JRParameter.JASPER_REPORT);
        variableMapWrapper.setReportName(jr.getName());
        parameterMapWrapper.setReportName(jr.getName());
    }

    public static class EmptyDataset implements JRDataset {

        JRGroup[] groups;

        public EmptyDataset(JRGroup[] groups)  {
            this.groups = groups;
        }

        @Override
        public boolean hasProperties() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public JRPropertiesMap getPropertiesMap() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public JRPropertiesHolder getParentProperties() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public UUID getUUID() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getName() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getScriptletClass() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public JRScriptlet[] getScriptlets() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public DatasetPropertyExpression[] getPropertyExpressions() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public JRParameter[] getParameters() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public JRQuery getQuery() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public JRField[] getFields() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public JRSortField[] getSortFields() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public JRVariable[] getVariables() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public JRGroup[] getGroups() {
            return groups;
        }

        @Override
        public boolean isMainDataset() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public String getResourceBundle() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public WhenResourceMissingTypeEnum getWhenResourceMissingType() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void setWhenResourceMissingType(WhenResourceMissingTypeEnum whenResourceMissingType) {
            // TODO Auto-generated method stub

        }

        @Override
        public JRExpression getFilterExpression() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Object clone() {
            throw new UnsupportedOperationException();
        }

    }
}
