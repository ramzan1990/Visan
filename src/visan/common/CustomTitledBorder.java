package visan.common;

import java.awt.*;

import javax.swing.border.AbstractBorder;

import visan.ui.CustomLAF;

public class CustomTitledBorder extends AbstractBorder {

    private final String title;
    //private final Color col1 = new Color(63, 63, 63);
    private final Color col2 = new Color(65, 67, 69);
    private final Color col3 = new Color(205, 205, 205);
    private final Color col4 = new Color(83, 83, 83);
    private final int gap = 16;
    private final int margin = 7;
    private final int topGap = 5;

    public CustomTitledBorder(String title) {
        this.title = title;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        super.paintBorder(c, g, x, y, width, height);
        Graphics2D g2d = null;
        if (g instanceof Graphics2D) {
            g2d = (Graphics2D) g;
            
            x+=margin;
            y+=margin;
            width-=2*margin;
            height-=2*margin;
            //top
            g2d.setColor(col2);
            g2d.drawLine(x, y+topGap, x + width, y+topGap);
            //g2d.setColor(col2);
          //  g2d.drawLine(x, y + 1+topGap, x + width, y + 1+topGap);
            //bottom
            // g2d.setColor(col1);
            //g2d.drawLine(x, y+height-1, x + width, y+height-1);
            g2d.setColor(col2);
            g2d.drawLine(x, y+height, x + width, y+height);
            
            //left
            g2d.setColor(col2);
            g2d.drawLine(x, y+1+topGap, x, y+height);
            
            //right
            g2d.setColor(col2);
            g2d.drawLine(x+width, y+1+topGap, x+width, y+height);
            
            g2d.setFont(CustomLAF.defaultFont);
            FontMetrics fm = g2d.getFontMetrics();
            g2d.setColor(col4);
            g2d.fillRect(x+13, y, fm.stringWidth("Data Transformation")+8, 10);
            
            
            g2d.setColor(col3);
            g2d.drawString("Data Transformation", x+18, y + 10);
            
            
        }
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return (getBorderInsets(c, new Insets(gap+topGap, gap, gap, gap)));
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.top = insets.right = insets.bottom = gap;
        insets.top=gap+topGap;
        return insets;
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }
}
