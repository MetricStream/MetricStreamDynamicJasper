package ar.com.fdvs.dj.util;

import java.lang.reflect.Constructor;
import java.util.Map;

import org.apache.commons.beanutils2.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ar.com.fdvs.dj.core.DJException;
import ar.com.fdvs.dj.core.layout.LayoutManager;
import ar.com.fdvs.dj.core.registration.EntitiesRegistrationException;
import ar.com.fdvs.dj.domain.CustomExpression;
import ar.com.fdvs.dj.domain.DynamicJasperDesign;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPen;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JRDesignGraphicElement;
import net.sf.jasperreports.engine.design.JRDesignGroup;
import net.sf.jasperreports.engine.design.JRDesignParameter;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignStyle;
import net.sf.jasperreports.engine.type.LineStyleEnum;
import net.sf.jasperreports.engine.type.SplitTypeEnum;

public class LayoutUtils {

	static final Log log = LogFactory.getLog(LayoutUtils.class);

	/**
	 * Finds "Y" coordinate value in which more elements could be added in the band
	 * @param band
	 * @return
	 */
	public static int findVerticalOffset(JRDesignBand band) {
		int finalHeight = 0;
		if (band != null) {
			for (final JRChild jrChild : band.getChildren()) {
				final JRDesignElement element = (JRDesignElement) jrChild;
				final int currentHeight = element.getY() + element.getHeight();
				if (currentHeight > finalHeight) {
                    finalHeight = currentHeight;
                }
			}
		}
		return finalHeight;
	}

	/**
	 * Copy bands elements from source to dest band, also makes sure copied elements
	 * are placed below existing ones (Y offset is calculated)
	 * @param destBand
	 * @param sourceBand
	 */
	public static void copyBandElements(JRDesignBand destBand, JRBand sourceBand) {
		final int offset = findVerticalOffset(destBand);

		if (destBand == null) {
            throw new DJException("destination band cannot be null");
        }

		if (sourceBand == null) {
            return;
        }

		for (final JRChild jrChild : sourceBand.getChildren()) {
			final JRDesignElement element = (JRDesignElement) jrChild;
			JRDesignElement dest = null;
			try {
				if (element instanceof JRDesignGraphicElement) {
					final Constructor<? extends JRDesignElement> constructor = element.getClass().getConstructor(JRDefaultStyleProvider.class);
					final JRDesignStyle style = new JRDesignStyle();
					dest = constructor.newInstance(style.getDefaultStyleProvider());
				} else {
					dest = element.getClass().newInstance();
				}

				BeanUtils.copyProperties(dest, element);
				dest.setY(dest.getY() + offset);
			} catch (final Exception e) {
				log.error("Exception copying elements from band to band: " + e.getMessage(), e);
			}
			destBand.addElement(dest);
		}
	}

	/**
	 * Moves the elements contained in "band" in the Y axis "yOffset"
	 * @param yOffset
	 * @param band
	 */
	public static void moveBandsElemnts(int yOffset, JRDesignBand band) {
		if (band == null) {
            return;
        }

		for (final JRChild jrChild : band.getChildren()) {
			final JRDesignElement elem = (JRDesignElement) jrChild;
			elem.setY(elem.getY() + yOffset);
		}
	}

	public static void registerCustomExpressionParameter(DynamicJasperDesign design,String name, CustomExpression customExpression) {
		if (customExpression == null){
			return;
		}
		registerAndAddParameter(design, name, customExpression.getClass().getName(), customExpression);
	}

	public static void registerAndAddParameter(DynamicJasperDesign design,String name, String classname, Object value) {
		final JRDesignParameter dparam = new JRDesignParameter();
		dparam.setName(name);
		dparam.setValueClassName(classname);
		log.debug("Registering parameter parameter with name: " + name +", classname: " + classname);
		try {
			design.addParameter(dparam);
		} catch (final JRException e) {
			throw new EntitiesRegistrationException(e.getMessage(),e);
		}
		design.getParametersWithValues().put(name, value);
	}

	/**
	 * Returns the JRDesignGroup for the DJGroup passed
	 * @param jd
	 * @param layoutManager
	 * @param group
	 * @return
	 */
	public static JRDesignGroup getJRDesignGroup(DynamicJasperDesign jd, LayoutManager layoutManager, DJGroup group) {
		final Map references = layoutManager.getReferencesMap();
		for (final Object o : references.keySet()) {
			final String groupName = (String) o;
			final DJGroup djGroup = (DJGroup) references.get(groupName);
			if (group == djGroup) {
				return (JRDesignGroup) jd.getGroupsMap().get(groupName);
			}
		}
		return null;
	}

	public static JRDesignGroup findParentJRGroup(DJGroup djgroup, DynamicReport dr, DynamicJasperDesign djd, LayoutManager layoutManager) {
		JRDesignGroup registeredGroup;
		int gidx = dr.getColumnsGroups().indexOf(djgroup);
		if (gidx > 0) {
			gidx--;
			final DJGroup djParentGroup = dr.getColumnsGroups().get(gidx);
            registeredGroup = LayoutUtils.getJRDesignGroup(djd, layoutManager, djParentGroup);
		} else {
            registeredGroup = null;
        }
		return registeredGroup;
	}

	public static DJGroup findChildDJGroup(DJGroup djgroup, DynamicReport dr) {
		DJGroup child = null;
		int gidx = dr.getColumnsGroups().indexOf(djgroup);
		if ((gidx+1) < dr.getColumnsGroups().size()) {
			gidx++;
			child = dr.getColumnsGroups().get(gidx);
		}
		return child;
	}

	/**
	 * Returns the firs band from the section
	 * @param section
	 * @return
	 */
	public static JRDesignBand getBandFromSection(JRDesignSection section) {
		return (JRDesignBand) section.getBandsList().get(0);
	}

    /**
     * FIXME there are 3 types of SplitTypeEnum, we are not using the "stretch"
     * @param allowsplit
     * @return
     */
    public static SplitTypeEnum getSplitTypeFromBoolean( boolean allowsplit ){
        if (allowsplit) {
            return SplitTypeEnum.IMMEDIATE;
        }

        return SplitTypeEnum.PREVENT;
    }


    public static void convertBorderToPen(Border border, JRPen pen) {
        if (border == null) {
            return;
        }

        pen.setLineWidth(border.getWidth());
        pen.setLineStyle(LineStyleEnum.values()[border.getLineStyle()]);
        pen.setLineColor(border.getColor());

    }
}
