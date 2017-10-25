package worldwind.common;

import gov.nasa.worldwind.BasicModel;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;

import gov.nasa.worldwind.layers.StarsLayer;
import worldwind.atmosphere.Atmosphere;

import javax.swing.*;
import java.awt.*;

public class ExampleBoilerplate extends WorldWindowGLCanvas{

    public ExampleBoilerplate() {
        setPreferredSize(new Dimension(800, 600));
        setModel(new BasicModel());
        getModel().getLayers().clear();
        getModel().getLayers().add(new StarsLayer());
        getModel().getLayers().add(new Atmosphere());
        JFrame frame = new JFrame();
        frame.setTitle("Толстый шар");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(this);
        frame.pack();
        frame.setVisible(true);
    }
}
