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

import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.type.HorizontalImageAlignEnum;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;

/**
 * @author msimone
 *
 */
public abstract class HorizontalBandAlignment {

	/**
	 * To be used with AutoText class constants ALIGMENT_LEFT, ALIGMENT_CENTER and ALIGMENT_RIGHT
	 * @param aligment
	 * @return
	 */
	public static HorizontalBandAlignment buildAligment(byte aligment){
		if (aligment == RIGHT.getAlignment()) {
            return RIGHT;
        } else if (aligment == LEFT.getAlignment()) {
        } else if (aligment == CENTER.getAlignment()) {
            return CENTER;
        }

		return LEFT;
	}

	public static final HorizontalBandAlignment RIGHT = new HorizontalBandAlignment() {
		@Override
        public void align(int totalWidth, int offset, JRDesignBand band, JRDesignElement element) {
			final int width = totalWidth - element.getWidth() - offset;
			element.setX(width);
			band.addElement(element);
		}

		@Override
        public int getAlignment() {
			return HorizontalTextAlignEnum.RIGHT.ordinal();
		}
	};

	public static final HorizontalBandAlignment LEFT = new HorizontalBandAlignment() {
		@Override
        public void align(int totalWidth, int offset, JRDesignBand band, JRDesignElement element) {
			element.setX(element.getX() + offset);
			band.addElement(element);
		}

		@Override
        public int getAlignment() {
			return HorizontalImageAlignEnum.LEFT.ordinal();
		}
	};

	public static final HorizontalBandAlignment CENTER = new HorizontalBandAlignment() {
		@Override
        public void align(int totalWidth, int offset, JRDesignBand band, JRDesignElement element) {
			element.setX(((totalWidth/2) - (element.getWidth()/2)) + offset);
			band.addElement(element);
		}

		@Override
        public int getAlignment() {
			return HorizontalImageAlignEnum.CENTER.ordinal();
		}
	};

	public abstract int getAlignment();
	public abstract void align(int totalWidth, int offset, JRDesignBand band, JRDesignElement element);

}
