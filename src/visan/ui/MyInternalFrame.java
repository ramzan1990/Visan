package visan.ui;

import visan.Visan;

import java.awt.Component;
import javax.swing.JInternalFrame;

public class MyInternalFrame extends JInternalFrame {

    static int xOffset = 30, yOffset = 30, openFrameCount;
    Component c;

    public MyInternalFrame(String title, Component c) {
        super(title + " (" + Visan.io.getFileName() + ")",
                true, //resizable
                true, //closable
                true, //maximizable
                true);//iconifiable

        this.add(c);
        this.c = c;
        setSize(1100, 600);
        setLocation(xOffset * openFrameCount, yOffset * openFrameCount);
        openFrameCount++;
        if(openFrameCount==10){
        	openFrameCount=0;
        }
    }
}
