package visan;

import visan.main.GUIManager;
import visan.main.IOManager;
import visan.main.VisState;
import visan.ui.CustomLAF;
import visan.visualisation.DataComponent;

/**
 * @author Ramzan Umarov
 */
public class Visan {

    private static VisState s;

    public static GUIManager gm;
    public static IOManager io;

    public static void main(String[] args) {
        try {
            CustomLAF.change();
        } catch (Exception e) {
        }
        s = new VisState();
        s.projectName = "DefaultProject";
        s.projectPath = "DefaultProject/";
        gm = new GUIManager(s);
        io = new IOManager(s, gm);
        gm.show(io);
        DataComponent.setWhiteTheme();
        try {
            // processInput(new File("learning set.txt"), "-1", true);
            // readInTestingSet(new File("testing.txt"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setState(VisState s) {
        Visan.s = s;
        gm.setState(s);
    }
}
