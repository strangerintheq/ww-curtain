package worldwind.primitives;

import gov.nasa.worldwind.BasicModel;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.layers.Earth.BMNGWMSLayer;
import gov.nasa.worldwind.layers.StarsLayer;
import worldwind.shadow.ShadowLayer;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Example {
    public static void main(String[] args) throws IOException {

        WorldWindowGLCanvas wwd = new WorldWindowGLCanvas();
        wwd.setPreferredSize(new Dimension(800, 600));
        wwd.setModel(new BasicModel());
        wwd.getModel().getLayers().clear();

        wwd.getModel().getLayers().add(new StarsLayer());
        wwd.getModel().getLayers().add(new StarsLayer());
        wwd.getModel().getLayers().add(new ShadowLayer(new BMNGWMSLayer()));

        JFrame frame = new JFrame();
        frame.setTitle("Толстый шар");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(wwd);
        frame.pack();
        frame.setVisible(true);
    }

}
