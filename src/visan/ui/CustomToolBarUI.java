/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package visan.ui;

import java.awt.Image;
import java.awt.event.WindowListener;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalToolBarUI;

/**
 *
 * @author Umarov
 */
public class CustomToolBarUI extends MetalToolBarUI {
  public final static String FRAME_IMAGEICON = "ToolBar.frameImageIcon";

  protected JFrame createFloatingFrame(JToolBar toolbar) {
    JFrame frame = new JFrame(toolbar.getName());
    frame.setResizable(false);
    Icon icon = UIManager.getIcon(FRAME_IMAGEICON);
    if (icon instanceof ImageIcon) {
      Image iconImage = ((ImageIcon) icon).getImage();
      frame.setIconImage(iconImage);
    }
    WindowListener windowListener = createFrameListener();
    frame.addWindowListener(windowListener);
    return frame;
  }
}