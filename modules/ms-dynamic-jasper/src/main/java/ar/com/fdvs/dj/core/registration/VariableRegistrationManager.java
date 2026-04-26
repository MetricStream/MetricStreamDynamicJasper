package ar.com.fdvs.dj.core.registration;

import ar.com.fdvs.dj.core.DJException;
import ar.com.fdvs.dj.core.layout.LayoutManager;
import ar.com.fdvs.dj.domain.DynamicJasperDesign;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.constants.DJVariableIncrementType;
import ar.com.fdvs.dj.domain.constants.DJVariableResetType;
import ar.com.fdvs.dj.domain.entities.DJVariable;
import ar.com.fdvs.dj.domain.entities.Entity;
import ar.com.fdvs.dj.util.ExpressionUtils;
import ar.com.fdvs.dj.util.LayoutUtils;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignGroup;
import net.sf.jasperreports.engine.design.JRDesignVariable;
import net.sf.jasperreports.engine.type.CalculationEnum;
import net.sf.jasperreports.engine.type.IncrementTypeEnum;
import net.sf.jasperreports.engine.type.ResetTypeEnum;

public class VariableRegistrationManager extends
		AbstractEntityRegistrationManager {

	public VariableRegistrationManager(DynamicJasperDesign jd, DynamicReport dr, LayoutManager layoutManager) {
		super(jd,dr,layoutManager);
	}

	@Override
	protected void registerEntity(Entity entity) {
		try {
			final JRDesignVariable jrvar = (JRDesignVariable) transformEntity(entity);
			getDjd().addVariable(jrvar);
		} catch (final JRException e) {
			throw new DJException("Problem registering a DJVariable: " + e.getMessage(), e);
		}

	}

	@Override
	protected Object transformEntity(Entity entity) throws JRException {
		final DJVariable var = (DJVariable)entity;
		final JRDesignVariable jrvar = new JRDesignVariable();
		jrvar.setName(var.getName());
		jrvar.setValueClassName(var.getClassName());

		if (var.getCalculation() != null){
			jrvar.setCalculation(CalculationEnum.values()[var.getCalculation().getValue()]);
		}

		final String expressionParamName = var.getName() + "_expression";
		final JRDesignExpression expression = ExpressionUtils.createAndRegisterExpression(getDjd(), expressionParamName, var.getExpression());
		jrvar.setExpression(expression);

		if (var.getInitialValueExpression() != null){
			final String initialValueExpressionParamName = var.getName() + "_initalValueExpression";
			final JRDesignExpression initialValueExpression = ExpressionUtils.createAndRegisterExpression(getDjd(), initialValueExpressionParamName, var.getInitialValueExpression());
			jrvar.setExpression(initialValueExpression);
		}

		if (var.getResetType() != null){
			jrvar.setResetType(mapToResetTypeEnum(var.getResetType()));
		}

		if ((var.getResetGroup() != null) && DJVariableResetType.GROUP.equals(var.getResetType())){
			final JRDesignGroup jrgroup = LayoutUtils.getJRDesignGroup(getDjd(),getLayoutManager(), var.getResetGroup());
			jrvar.setResetGroup(jrgroup.getName());
		}

		if (var.getIncrementType() != null){
			jrvar.setIncrementType(mapToIncrementTypeEnum(var.getIncrementType()));
		}

		if ((var.getIncrementGroup() != null) && DJVariableIncrementType.GROUP.equals(var.getIncrementType())){
			final JRDesignGroup jrgroup = LayoutUtils.getJRDesignGroup(getDjd(),getLayoutManager(), var.getIncrementGroup());
			jrvar.setIncrementGroup(jrgroup.getName());
		}

		return jrvar;
	}

	private ResetTypeEnum mapToResetTypeEnum(DJVariableResetType djResetType) {
		return switch (djResetType.getValue()) {
            case 2 -> ResetTypeEnum.PAGE;
			case 3 -> ResetTypeEnum.COLUMN;
			case 4 -> ResetTypeEnum.GROUP;
			case 5 -> ResetTypeEnum.NONE;
			default -> ResetTypeEnum.REPORT;
		};
	}

	private IncrementTypeEnum mapToIncrementTypeEnum(DJVariableIncrementType djIncrementType) {
		return switch (djIncrementType.getValue()) {
			case 1 -> IncrementTypeEnum.REPORT;
			case 2 -> IncrementTypeEnum.PAGE;
			case 3 -> IncrementTypeEnum.COLUMN;
			case 4 -> IncrementTypeEnum.GROUP;
            default -> IncrementTypeEnum.NONE;
		};
	}

}
