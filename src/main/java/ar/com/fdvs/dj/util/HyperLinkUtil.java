package ar.com.fdvs.dj.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ar.com.fdvs.dj.domain.DJHyperLink;
import ar.com.fdvs.dj.domain.DynamicJasperDesign;
import ar.com.fdvs.dj.domain.StringExpression;
import net.sf.jasperreports.charts.design.JRDesignChart;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignImage;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.type.HyperlinkTypeEnum;

public class HyperLinkUtil {

	static final Log log = LogFactory.getLog(HyperLinkUtil.class);

    /**
     *
     * @param design
     * @param djlink
     * @param image
     * @param name
     */
	public static void applyHyperLinkToElement(DynamicJasperDesign design, DJHyperLink djlink, JRDesignImage image, String name) {
		final StringExpression hce = djlink.getExpression();

		final String text = ExpressionUtils.createCustomExpressionInvocationText(djlink.getExpression(), name, false);
		LayoutUtils.registerCustomExpressionParameter(design, name,hce);
		final JRDesignExpression hlpe = new JRDesignExpression();

		hlpe.setText(text);
		image.setHyperlinkReferenceExpression(hlpe);
		image.setHyperlinkType( HyperlinkTypeEnum.REFERENCE ); //FIXME Should this be a parameter in the future?

		if (djlink.getTooltip() != null){
			final StringExpression sExp = djlink.getTooltip();
			final String tooltipParameterName = "hyperlink_tooltip_" +name;
			final String tooltipText = ExpressionUtils.createCustomExpressionInvocationText(djlink.getExpression(), tooltipParameterName, false);
			LayoutUtils.registerCustomExpressionParameter(design, tooltipParameterName,sExp);
			final JRDesignExpression tooltipExp = new JRDesignExpression();
			tooltipExp.setText(tooltipText);

			image.setHyperlinkTooltipExpression(tooltipExp);
		}

	}

	/**
	 *  Creates necessary objects to make a textfield an hyperlink
	 * @param design
	 * @param djlink
	 * @param textField
	 * @param name
	 */
	public static void applyHyperLinkToElement(DynamicJasperDesign design, DJHyperLink djlink, JRDesignTextField textField, String name) {
//		DJHyperLink djlink = column.getLink();
		final StringExpression hce = djlink.getExpression();

//		String hceParameterName = "hyperlink_column_" +column.getTextForExpression().replaceAll("[\\$\\{\\}]", "_");
		final String text = ExpressionUtils.createCustomExpressionInvocationText2(name);
		LayoutUtils.registerCustomExpressionParameter(design,name,hce);
		final JRDesignExpression hlpe = new JRDesignExpression();

		hlpe.setText(text);
		textField.setHyperlinkReferenceExpression(hlpe);
		textField.setHyperlinkType( HyperlinkTypeEnum.REFERENCE ); //FIXME Should this be a parameter in the future?


		if (djlink.getTooltip() != null){
			final StringExpression sExp = djlink.getTooltip();
//			String tooltipParameterName = "hyperlink_tooltip_column_" +column.getTextForExpression().replaceAll("[\\$\\{\\}]", "_");
			final String tooltipParameterName = "tooltip_" + name;
			final String tooltipText = ExpressionUtils.createCustomExpressionInvocationText2(tooltipParameterName);
			LayoutUtils.registerCustomExpressionParameter(design,tooltipParameterName,sExp);
			final JRDesignExpression tooltipExp = new JRDesignExpression();
			tooltipExp.setText(tooltipText);

			textField.setHyperlinkTooltipExpression(tooltipExp);
		}
	}

	/**
	 *  Creates necessary objects to make a chart an hyperlink
     *
     * @param design
     * @param djlink
     * @param chart
     * @param name
     */
	public static void applyHyperLinkToElement(DynamicJasperDesign design, DJHyperLink djlink, JRDesignChart chart, String name) {
		final JRDesignExpression hlpe = ExpressionUtils.createAndRegisterExpression(design, name, djlink.getExpression());
		chart.setHyperlinkReferenceExpression(hlpe);
		chart.setHyperlinkType( HyperlinkTypeEnum.REFERENCE ); //FIXME Should this be a parameter in the future?

		if (djlink.getTooltip() != null){
			final JRDesignExpression tooltipExp = ExpressionUtils.createAndRegisterExpression(design, "tooltip_" + name, djlink.getTooltip());
			chart.setHyperlinkTooltipExpression(tooltipExp);
		}
	}
}
