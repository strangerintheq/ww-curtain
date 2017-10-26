package worldwind.common;

import gov.nasa.worldwind.BasicModel;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;

import gov.nasa.worldwind.layers.Earth.BMNGWMSLayer;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.layers.StarsLayer;
import gov.nasa.worldwind.render.Renderable;
import worldwind.atmosphere.Atmosphere;

import javax.swing.*;
import java.awt.*;

public class ExampleBoilerplate extends WorldWindowGLCanvas {

    private RenderableLayer userLayer;

    public ExampleBoilerplate() {
        setPreferredSize(new Dimension(800, 600));
        setModel(new BasicModel());
        getModel().getLayers().clear();
        layer(new StarsLayer());
        layer(new Atmosphere());
        layer(new BMNGWMSLayer());
        jFrame();
    }

    private void jFrame() {
        JFrame frame = new JFrame();
        frame.setTitle("Толстый шар");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    public ExampleBoilerplate layer(Layer l) {
        getModel().getLayers().add(l);
        return this;
    }

    public ExampleBoilerplate renderable(Renderable renderable) {
        if (null == userLayer)
            layer(userLayer = new RenderableLayer());
        userLayer.addRenderable(renderable);
        return this;
    }
}
