package visan.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.io.InputStream;
import javax.swing.Painter;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

/**
 *
 * @author Ramzan
 */
public class CustomLAF {

	public static final Color blackColor = new Color(75, 77, 79);
    public static final Color panelColor = new Color(83, 83, 83);
    public static final Color ActiveWindowColor = new Color(83, 83, 83);
    public static final Color InactiveWindowColor = new Color(63, 63, 63);
    public static final Color textAreaColor = new Color(66, 66, 66);
    public static final Color menuColor = new Color(205, 205, 205);
    public static final Color buttonColor = new Color(98, 98, 98);
    public static final Color buttonColorMouse = new Color(108, 108, 108);
    public static final Color buttonColorPressed = new Color(68, 68, 68);
    public static final Color bgColor = new Color(38, 38, 38);
    public static final Color scrollBarColor = new Color(126, 126, 126);
    public static final Color scrollBarArrowColor = new Color(200, 200, 200);
    public static final Color scrollBarBG = new Color(66, 66, 66);
    public static Font defaultFont = new Font("Arial", Font.PLAIN, 12);

    public static void change() {
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel() {

                @Override
                public UIDefaults getDefaults() {
                    UIDefaults ret = super.getDefaults();
                    try {
                        InputStream is = this.getClass().getResourceAsStream("images/1.ttf");
                        Font ttfBase = Font.createFont(Font.TRUETYPE_FONT, is);
                        defaultFont = ttfBase.deriveFont(Font.PLAIN, 12);
                    } catch (Exception ex) {
                    }
                     
                    ret.put("defaultFont",
                            defaultFont);
                    ret.put("Table.textForeground", Color.BLACK);

                    ret.put("nimbusBase", Color.BLACK);
                    ret.put("nimbusBlueGrey", panelColor);
                    ret.put("control", panelColor);
                    ret.put("text", menuColor);
                    ret.put("TextField.foreground", Color.BLACK);
                    ret.put("ToolTip.textForeground", Color.BLACK);
                    
                    ret.put("nimbusSelection", new Color(0, 154, 255));
                    ret.put("nimbusSelectionBackground",  new Color(0, 154, 255));
                    ret.put("List[Selected].textBackground", new Color(0, 154, 255));
                    ret.put("List[Selected].textForeground", Color.WHITE);
                    
                    ret.put("DesktopPane[Enabled].backgroundPainter", new FillPainter(bgColor));
                    ret.put("InternalFrame[Enabled+WindowFocused].backgroundPainter", new FillPainter(ActiveWindowColor));
                    ret.put("InternalFrame[Enabled].backgroundPainter", new FillPainter(InactiveWindowColor));
                    ret.put("InternalFrame.contentMargins", new Insets(1, 3, 3, 3));

                    //menu
                    ret.put("Menu.font", defaultFont);
                    ret.put("MenuBar:Menu[Enabled].textForeground", menuColor);
                    ret.put("MenuBar:Menu[Enabled+Selected].backgroundPainter", new FillPainter(new Color(0, 154, 255)));       
                    ret.put("RadioButtonMenuItem[Enabled].textForeground", menuColor);
                    ret.put("RadioButtonMenuItem[MouseOver].backgroundPainter", new FillPainter(new Color(0, 154, 255)));   
                    ret.put("RadioButtonMenuItem[MouseOver+Selected].backgroundPainter", new FillPainter(new Color(0, 154, 255))); 
                    ret.put("CheckBoxMenuItem[Enabled].textForeground", menuColor);
                    ret.put("CheckBoxMenuItem[MouseOver].backgroundPainter", new FillPainter(new Color(0, 154, 255))); 
                    ret.put("CheckBoxMenuItem[MouseOver+Selected].backgroundPainter", new FillPainter(new Color(0, 154, 255)));                     
                    ret.put("MenuItem[Enabled].textForeground", menuColor);
                    ret.put("MenuItem[MouseOver].backgroundPainter", new FillPainter(new Color(0, 154, 255)));   
                    ret.put("Menu[Enabled].textForeground", menuColor);
                    ret.put("Menu[Enabled+Selected].backgroundPainter", new FillPainter(new Color(0, 154, 255)));                   
                    ret.put("MenuBar[Enabled].backgroundPainter", new FillPainter(panelColor));
                    ret.put("MenuBar[Enabled].borderPainter", new FillPainter(panelColor));

                    //UIManager.put("MenuItem[Enabled].textForeground", menuColor);
                    // UIManager.put("CheckBoxMenuItem.foreground", menuColor);
                    // UIManager.put("CheckBoxMenuItem[Enabled+Selected].textForeground", menuColor);
                    // UIManager.put("CheckBoxMenuItem[Enabled].textForeground", menuColor);
                    ret.put("Panel.background", panelColor);

                    ret.put("TextArea.background", textAreaColor);

                    ret.put("List.background", textAreaColor);
                    //  ScrollPane.contentMargins

                    //ret.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\"[Enabled].backgroundPainter", new ClosePainter(0));
                    ret.put("ScrollBar:\"ScrollBar.button\"[Enabled].foregroundPainter", new ScrollPainter(scrollBarArrowColor, scrollBarBG));
                    ret.put("ScrollBar:\"ScrollBar.button\"[MouseOver].foregroundPainter", new ScrollPainter(scrollBarArrowColor, scrollBarBG));
                    ret.put("ScrollBar:\"ScrollBar.button\"[Pressed].foregroundPainter", new ScrollPainter(scrollBarArrowColor, scrollBarBG));
                    ret.put("ScrollBar:\"ScrollBar.button\".size", 15);
                    ret.put("ScrollBar.decrementButtonGap", 0);
                    ret.put("ScrollBar.incrementButtonGap", 0);

                    ret.put("ScrollBar:ScrollBarThumb[Enabled].backgroundPainter", new FillPainter(scrollBarColor));
                    ret.put("ScrollBar:ScrollBarThumb[MouseOver].backgroundPainter", new FillPainter(scrollBarColor));
                    ret.put("ScrollBar:ScrollBarThumb[Pressed].backgroundPainter", new FillPainter(scrollBarColor));
                    ret.put("ScrollBar:ScrollBarTrack[Enabled].backgroundPainter", new FillPainter(scrollBarBG));

                    ret.put("Button[Default+Focused+MouseOver].backgroundPainter", new ButtonPainter(buttonColorMouse, blackColor));
                    ret.put("Button[Default+Focused+Pressed].backgroundPainter", new ButtonPainter(buttonColorPressed, blackColor));
                    ret.put("Button[Default+Focused].backgroundPainter", new ButtonPainter(buttonColor, blackColor));
                    ret.put("Button[Default+MouseOver].backgroundPainter", new ButtonPainter(buttonColorMouse, blackColor));
                    ret.put("Button[Default+Pressed].backgroundPainter", new ButtonPainter(buttonColorPressed, blackColor));
                    ret.put("Button[Default].backgroundPainter", new ButtonPainter(buttonColor, blackColor));
                    ret.put("Button[Disabled].backgroundPainter", new ButtonPainter(buttonColor, blackColor));
                    // ret.put("Button[Disabled].textForeground", new ButtonPainter(buttonColor, blackColor));
                    ret.put("Button[Enabled].backgroundPainter", new ButtonPainter(buttonColor, blackColor));
                    ret.put("Button[Focused+MouseOver].backgroundPainter", new ButtonPainter(buttonColorMouse, blackColor));
                    ret.put("Button[Focused+Pressed].backgroundPainter", new ButtonPainter(buttonColorPressed, blackColor));
                    ret.put("Button[Focused+Pressed].backgroundPainter", new ButtonPainter(buttonColorPressed, blackColor));
                    ret.put("Button[Focused].backgroundPainter", new ButtonPainter(buttonColor, blackColor));
                    ret.put("Button[MouseOver].backgroundPainter", new ButtonPainter(buttonColorMouse, blackColor));
                    ret.put("Button[Pressed].backgroundPainter", new ButtonPainter(buttonColorPressed, blackColor));

                    ret.put("ToolBarSeparator[Enabled].backgroundPainter", new SeparatorPainter());
                    ret.put("ToolBar[Enabled].handleIconPainter", new EmptyPainter());
                    ret.put("ToolBarSeparator.contentMargins", new Insets(0, 0, 0, 0));

                    ret.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\"[Disabled].backgroundPainter", new ClosePainter(0));
                    ret.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\"[Enabled+WindowNotFocused].backgroundPainter", new ClosePainter(0));
                    ret.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\"[Enabled].backgroundPainter", new ClosePainter(0));
                    ret.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\"[MouseOver+WindowNotFocused].backgroundPainter", new ClosePainter(1));
                    ret.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\"[MouseOver].backgroundPainter", new ClosePainter(1));
                    ret.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\"[Pressed+WindowNotFocused].backgroundPainter", new ClosePainter(0));
                    ret.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\"[Pressed].backgroundPainter", new ClosePainter(0));

                    ret.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\"[Disabled].backgroundPainter", new IconifyPainter(0));
                    ret.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\"[Enabled+WindowNotFocused].backgroundPainter", new IconifyPainter(0));
                    ret.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\"[Enabled].backgroundPainter", new IconifyPainter(0));
                    ret.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\"[MouseOver+WindowNotFocused].backgroundPainter", new IconifyPainter(1));
                    ret.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\"[MouseOver].backgroundPainter", new IconifyPainter(1));
                    ret.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\"[Pressed+WindowNotFocused].backgroundPainter", new IconifyPainter(0));
                    ret.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\"[Pressed].backgroundPainter", new IconifyPainter(0));

                    ret.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[Disabled].backgroundPainter", new MaximizePainter(0));
                    ret.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[Enabled+WindowNotFocused].backgroundPainter", new MaximizePainter(0));
                    ret.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[Enabled].backgroundPainter", new MaximizePainter(0));
                    ret.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[MouseOver+WindowNotFocused].backgroundPainter", new MaximizePainter(1));
                    ret.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[MouseOver].backgroundPainter", new MaximizePainter(1));
                    ret.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[Pressed+WindowNotFocused].backgroundPainter", new MaximizePainter(0));
                    ret.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[Pressed].backgroundPainter", new MaximizePainter(0));
                    ret.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[MouseOver+WindowMaximized+WindowNotFocused].backgroundPainter", new MaximizePainter(0));
                    ret.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[MouseOver+WindowMaximized].backgroundPainter", new MaximizePainter(0));
                    ret.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[Pressed+WindowMaximized+WindowNotFocused].backgroundPainter", new MaximizePainter(0));
                    ret.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[Pressed+WindowMaximized].backgroundPainter", new MaximizePainter(0));
                    ret.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[Enabled+WindowMaximized].backgroundPainter", new MaximizePainter(0));
                    ret.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[MouseOver+Enabled+WindowMaximized].backgroundPainter", new MaximizePainter(1));

                    ret.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\".contentMargins", new Insets(-50, -50, -50, -50));

                    ret.put("RadioButton[Enabled].iconPainter", new RadioButtonPainter(0));
                    ret.put("RadioButton[Focused+MouseOver+Selected].iconPainter", new RadioButtonPainter(1));
                    ret.put("RadioButton[Focused+MouseOver].iconPainter", new RadioButtonPainter(0));
                    ret.put("RadioButton[Focused+Pressed+Selected].iconPainter", new RadioButtonPainter(1));
                    ret.put("RadioButton[Focused+Pressed].iconPainter", new RadioButtonPainter(0));
                    ret.put("RadioButton[Focused+Selected].iconPainter", new RadioButtonPainter(1));
                    ret.put("RadioButton[Focused].iconPainter", new RadioButtonPainter(0));
                    ret.put("RadioButton[MouseOver+Selected].iconPainter", new RadioButtonPainter(1));
                    ret.put("RadioButton[MouseOver].iconPainter", new RadioButtonPainter(0));
                    ret.put("RadioButton[Pressed+Selected].iconPainter", new RadioButtonPainter(1));
                    ret.put("RadioButton[Pressed].iconPainter", new RadioButtonPainter(0));
                    ret.put("RadioButton[Selected].iconPainter", new RadioButtonPainter(1));
                    return ret;
                }

            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}

class SeparatorPainter implements Painter {

    @Override
    public void paint(Graphics2D g, Object object, int width, int height) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setPaint(new Color(70, 71, 73));
        g.drawLine(0, 0, 2 * height, 0);
        g.setPaint(new Color(51, 53, 56));
        g.drawLine(0, 1, 2 * height, 1);
    }

}

class EmptyPainter implements Painter {

    @Override
    public void paint(Graphics2D g, Object object, int width, int height) {
    }

}

class ClosePainter implements Painter {

    private int i;

    public ClosePainter(int i) {
        this.i = i;
    }

    @Override
    public void paint(Graphics2D g, Object c, int w, int h) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int s = 5;
        if (i == 0) {
            g.setPaint(new Color(130, 130, 130));
        } else {
            g.setPaint(new Color(160, 160, 160));
        }
        g.drawLine(s, s, w - s, h - s);
        g.drawLine(s, h - s, w - s, s);
    }
}

class RadioButtonPainter implements Painter {

    private int i;

    public RadioButtonPainter(int i) {
        this.i = i;
    }

    @Override
    public void paint(Graphics2D g, Object c, int w, int h) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int s = 5;
        if (i == 0) {
        	g.setPaint(new Color(130, 133, 139));
             g.drawOval(4,4, 10,10);
        } else {
           
            g.setPaint(new Color(130, 133, 139));
             g.fillOval(4,4, 10,10);
        }
       

    }
}

class IconifyPainter implements Painter {

    private int i;

    public IconifyPainter(int i) {
        this.i = i;
    }

    @Override
    public void paint(Graphics2D g, Object c, int w, int h) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int s = 5;
        if (i == 0) {
            g.setPaint(new Color(130, 130, 130));
        } else {
            g.setPaint(new Color(160, 160, 160));
        }
        g.drawLine(s, h - s, w - s, h - s);
    }
}

class MaximizePainter implements Painter {

    private int i;

    public MaximizePainter(int i) {
        this.i = i;
    }

    @Override
    public void paint(Graphics2D g, Object c, int w, int h) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int s = 5;
        if (i == 0) {
            g.setPaint(new Color(130, 130, 130));
        } else {
            g.setPaint(new Color(160, 160, 160));
        }
        g.drawRect(s, s, w - 2 * s, h - 2 * s);
    }
}

class FillPainter implements Painter {

    Color color;

    public FillPainter(Color color) {
        this.color = color;
    }

    @Override
    public void paint(Graphics2D g, Object c, int w, int h) {
        g.setPaint(color);
        g.fillRect(0, 0, w, h);
    }
}

class ScrollPainter implements Painter {

    Color color;
    Color bg;
    
    public ScrollPainter(Color color, Color bg) {
        this.color = color;
        this.bg = bg; 
    }

    @Override
    public void paint(Graphics2D g, Object c, int w, int h) {
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (color != null) {
                g.setPaint(bg);
                g.fillRect(0, 0, w, h);
                g.setPaint(color);
                int[] xPoints = new int[]{w / 6 + w / 4, w / 6 + w / 2, w / 6 + w / 2};
                int[] yPoints = new int[]{h / 2, h / 4, 3 * h / 4};
                int nPoints = 3;
                g.fillPolygon(xPoints, yPoints, nPoints);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

class BorderPainter implements Painter {

    Color color;

    public BorderPainter(Color color) {
        this.color = color;
    }

    @Override
    public void paint(Graphics2D g, Object c, int w, int h) {
        if (color != null) {
            g.setPaint(color);
            g.drawRect(0, 0, w, h);
        }
    }
}

class ButtonPainter implements Painter {

    Color c1, c2;

    public ButtonPainter(Color c1, Color c2) {
        this.c1 = c1;
        this.c2 = c2;
    }

    @Override
    public void paint(Graphics2D g, Object c, int w, int h) {
        g.setPaint(c1);
        g.fillRect(0, 0, w, h);
        g.setPaint(c2);
        g.drawRect(0, 0, w, h);

    }
}
