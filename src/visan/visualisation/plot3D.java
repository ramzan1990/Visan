package visan.visualisation;

import visan.common.ClassesCollection;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.mouse.AbstractChartMouseSelector;
import org.jzy3d.chart.controllers.mouse.ChartMouseController;
import org.jzy3d.chart.controllers.mouse.interactives.ScatterMouseSelector;
import org.jzy3d.chart.controllers.thread.ChartThreadController;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.interactive.InteractiveScatter;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.Renderer2d;

/**
 *
 * @author Ramzan
 */
public class plot3D {

    private final Color[] allColors = {Color.BLUE, Color.RED, Color.CYAN, Color.MAGENTA, Color.YELLOW};
    private String message = "Use w and s to control transparency";
    private boolean selectionMode;
    private ChartMouseController mouseCamera;
    private AbstractChartMouseSelector mouseSelection;
    private ChartThreadController thread;
    public Chart chart;
    public InteractiveScatter scatter;
    public int classes[];
    public ArrayList<Color> col;

    public plot3D(final ClassesCollection data, final int[] classes, int[] features, boolean listener) {
        col = new ArrayList<Color>();
        this.classes = classes;
        int size = 0;
        int counter = 0;
        for (int i = 0; i < classes.length; i++) {
            size += data.getTrainingClassSize(classes[i]);
        }
        Coord3d[] points = new Coord3d[size];
        Color[] colors = new Color[size];
        for (int i = 0; i < classes.length; i++) {
            Color color;
            if (i < allColors.length) {
                color = allColors[i];
            } else {
                color = Color.random();
            }
            color.a = 1f;
            col.add(color);
            for (int j = 0; j < data.getTrainingClassSize(classes[i]); j++) {
                float x = (float) data.trainingObject(classes[i], j)[features[0]];
                float y = (float) data.trainingObject(classes[i], j)[features[1]];
                float z = (float) data.trainingObject(classes[i], j)[features[2]];
                points[counter] = new Coord3d(x, y, z);
                colors[counter] = color;
                counter++;
            }
        }
        scatter = new InteractiveScatter(points, colors);
        chart = new Chart(Quality.Nicest, "awt");
        chart.getScene().add(scatter);
        mouseCamera = new ChartMouseController();
        mouseSelection = new ScatterMouseSelector(scatter);
        mouseSelection.attachChart(chart);
        thread = new ChartThreadController();
        mouseCamera.addSlaveThreadController(thread);
        chart.addController(thread);
        chart.getAxeLayout().setXAxeLabel(data.features.get(features[0]));
        chart.getAxeLayout().setYAxeLabel(data.features.get(features[1]));
        chart.getAxeLayout().setZAxeLabel(data.features.get(features[2]));
        chart.getCanvas().addKeyListener(new KeyListener() {

            public void keyPressed(KeyEvent e) {
            }

            public void keyReleased(KeyEvent e) {
            }

            public void keyTyped(KeyEvent e) {
                switch (e.getKeyChar()) {
                    case 'w':
                        if (col.get(0).a < 1f) {
                            for (int i = 0; i < col.size(); i++) {
                                col.get(i).a += 0.1f;
                            }
                        }
                        break;
                    case 's':
                        if (col.get(0).a > 0.1f) {
                            for (int i = 0; i < col.size(); i++) {
                                col.get(i).a -= 0.1f;
                            }
                        }
                        break;
                    default:
                        break;
                }
                chart.updateProjectionsAndRender();

            }
        });

        if (listener) {
            chart.getCanvas().addKeyListener(new KeyListener() {

                public void keyPressed(KeyEvent e) {
                }

                public void keyReleased(KeyEvent e) {
                    if (!selectionMode) {
                        return;
                    }
                    switch (e.getKeyChar()) {
                        case 'c':
                            chart.removeController(mouseCamera);
                            mouseSelection.attachChart(chart);
                            break;
                        default:
                            break;
                    }
                    holding = false;
                    message = "Selection mode (hold 'c' to control camera)";
                    chart.updateProjectionsAndRender();
                }

                public void keyTyped(KeyEvent e) {
                    if (!selectionMode) {
                        return;
                    }
                    if (!holding) {
                        switch (e.getKeyChar()) {
                            case 'c':
                                mouseSelection.releaseChart();
                                chart.addController(mouseCamera);
                                break;
                            default:
                                break;
                        }
                        holding = true;
                        message = "Rotation mode (release 'c' to make a selection)";
                        chart.updateProjectionsAndRender();
                    }
                }
                protected boolean holding;
            });
        }
        Renderer2d messageRenderer = new Renderer2d() {

            RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Font f = new Font("Arial", Font.PLAIN, 26);

            public void paint(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHints(rh);
                g.setFont(f);
                if (message != null) {
                    g2d.setColor(java.awt.Color.RED);
                    g2d.drawString(message, 10, 20);
                }
                for (int i = 0; i < classes.length; i++) {
                    g2d.setColor(col.get(i).awt());
                    g2d.drawString(data.getClassesNames()[classes[i]], 10 + i * 110, 40);
                }
            }
        };
        chart.addRenderer(messageRenderer);
        mouseSelection.releaseChart();
        chart.addController(mouseCamera);
        scatter.setHighlightColor(Color.RED);
    }

    public void setSelectionMode(boolean b) {
        selectionMode = b;
        if (!b) {
            message = "Use w and s to control transparency";
            mouseSelection.releaseChart();
            chart.addController(mouseCamera);
        } else {
            chart.removeController(mouseCamera);
            mouseSelection.attachChart(chart);
            message = "Selection mode (hold 'c' to control camera)";
        }
        chart.updateProjectionsAndRender();
    }

    public void pointSizeDec() {
        if (scatter.width > 0) {
            --scatter.width;
            chart.updateProjectionsAndRender();
        }
    }

    public void pointSizeInc() {
        if (scatter.width < 10) {
            ++scatter.width;
            chart.updateProjectionsAndRender();
        }
    }
}
