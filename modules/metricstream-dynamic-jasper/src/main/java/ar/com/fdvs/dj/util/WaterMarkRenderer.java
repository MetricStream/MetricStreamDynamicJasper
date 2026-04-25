package ar.com.fdvs.dj.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import net.sf.jasperreports.engine.JRException;

/**
 * Created by dj on 01/12/15.
 * Took from: http://stackoverflow.com/questions/11665663/watermark-across-the-page-in-jasperreports
 */
@SuppressWarnings("deprecation")
public class WaterMarkRenderer {
    private boolean m_licenseTrial = false;

    public WaterMarkRenderer(boolean isLicenseTrial) {
        m_licenseTrial = isLicenseTrial;
    }

    //    @Override
    //    public byte getType()
    //    {
    //        // no idea what this does
    //        return JRren.SVG.getValue();
    //    }
    //
    //    @Override
    //    public byte getImageType()
    //    {
    //        // no idea what this does
    //        return (byte)ImageTypeEnum.UNKNOWN.ordinal();
    //    }
    //
    //    @Override
    //    public Dimension2D getDimension() throws JRException
    //    {
    //        // A4 in pixel: 595x842
    //        // this seems to override whatever is configured in jasperreports studio
    //        return new Dimension(595 - (2 * 40), 700);
    //    }
    //
    //    @Override
    //    public byte[] getImageData() throws JRException
    //    {
    //        // no idea what this does
    //        return new byte[0];
    //    }

    public void render(Graphics2D g2, Rectangle2D rectangle) throws JRException {
        if (m_licenseTrial) {
            final AffineTransform originalTransform = g2.getTransform();

            // just for debugging
            g2.setColor(Color.BLUE);
            g2.draw(rectangle);

            g2.translate(rectangle.getX() + 100, rectangle.getMaxY());
            g2.rotate((-55 * Math.PI) / 180);

            final Font font = new Font("Arial", Font.PLAIN, 120);
            final Shape shape = font.createGlyphVector(g2.getFontRenderContext(), "Trial License").getOutline();
            g2.setColor(new Color(255, 0, 0, 100));
            g2.setStroke(new BasicStroke(1));
            g2.draw(shape);

            g2.setTransform(originalTransform);
        }
    }

    public static BufferedImage rotateText(String text, Font font, int width, int height, int textAngle, Color textColor) {
        if (text == null) {
            throw new IllegalArgumentException("text must be not-null ");
        }
        if (text.isEmpty()) {
            throw new IllegalArgumentException("text is empty string");
        }
        if (text.trim().isEmpty()) {
            throw new IllegalArgumentException("text must contain at least one character that is not space");
        }

        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        // make whole image transparent
        for (int i = image.getWidth() - 1; i > -1; i--) {
            for (int j = image.getHeight() - 1; j > -1; j--) {
                if (image.getRGB(i, j) == new Color(255, 255, 255).getRGB()) {
                    image.setRGB(i, j, new Color(0, 0, 0, 0).getRGB());
                }
            }
        }

        final Graphics2D g = (Graphics2D) image.getGraphics();
        g.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                                               RenderingHints.VALUE_ANTIALIAS_ON));
        g.setFont(font);
        final FontMetrics fm = g.getFontMetrics();
        final double completeWidth = fm.stringWidth(text);

        final double caLinesCountD = completeWidth / width;
        int caLinesCount;
        if ((caLinesCountD % 1.0) > 0) {
            caLinesCount = (int) caLinesCountD + 1;
        } else {
            caLinesCount = (int) caLinesCountD;
        }

        final java.util.List<String> lines = new ArrayList<>(caLinesCount);

        if (caLinesCount == 1) {
            lines.add(text);
        } else {
            final int caSepIdx = text.length() / caLinesCount;
            for (int i = 0; i < text.length();) {
                final int nextCaSep = i + caSepIdx;
                if (text.length() > nextCaSep) {

                    final int sepIdx = text.substring(i, nextCaSep + 1).lastIndexOf(' ');
                    if (sepIdx == 0) {
                        i++;

                    } else if (sepIdx == -1) {
                        lines.add(text.substring(i, nextCaSep));
                        i += nextCaSep;
                    } else {
                        lines.add(text.substring(i, i + sepIdx));
                        i += sepIdx + 1;
                    }
                } else {
                    lines.add(text.substring(i));
                    break;
                }
            }
        }

        g.rotate((Math.PI / 180) * textAngle, width / 2, height / 2);
        final int textHeight = fm.getMaxAscent();

        int lineY = height / 2;
        // shift up for vertical centration
        final int halfLines = lines.size() / 2;
        double shiftLines = (lines.size() % 2) == 0 ? halfLines - 1 : halfLines - 0.5;
        shiftLines += 0.2;
        lineY -= textHeight * shiftLines;

        for (final String line : lines) {
            final int lineWidth = fm.stringWidth(line);
            final int lineX = (width / 2) - (lineWidth / 2);
            g.setColor(textColor);
            g.drawChars(line.toCharArray(), 0, line.length(), lineX, lineY);
            lineY += textHeight;
        }

        return image;
    }
}
