package visan.ui;

import visan.Visan;

import javax.swing.*;
import java.util.ArrayList;

public class VFrame extends  JFrame{
    public VFrame(){
        try {
            ArrayList localArrayList = new ArrayList();
            localArrayList.add(new ImageIcon(Visan.class.getResource("ui/images/ic16.png")).getImage());
            localArrayList.add(new ImageIcon(Visan.class.getResource("ui/images/ic32.png")).getImage());
            localArrayList.add(new ImageIcon(Visan.class.getResource("ui/images/ic64.png")).getImage());
            localArrayList.add(new ImageIcon(Visan.class.getResource("ui/images/ic128.png")).getImage());
            this.setIconImages(localArrayList);
        } catch (Exception localException2) {
        }
    }
}
