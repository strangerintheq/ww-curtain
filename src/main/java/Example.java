import gov.nasa.worldwind.BasicModel;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.render.Polyline;
import gov.nasa.worldwind.render.ScreenImage;
import gov.nasa.worldwind.render.SurfaceImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Example {
    public static void main(String[] args) throws IOException {

        SurfaceImage surfaceImage = createSurfaceImage();
        Polyline polyline = createLine(surfaceImage);

        CurtainLayer layer = new CurtainLayer();
        layer.addRenderable(surfaceImage);
        layer.addRenderable(polyline);

        WorldWindowGLCanvas wwd = new WorldWindowGLCanvas();
        wwd.setPreferredSize(new Dimension(800, 600));
        wwd.setModel(new BasicModel());
        wwd.getModel().getLayers().add(layer);
        wwd.getModel().getLayers().add(new CurtainControlLayer());

        JFrame f = new JFrame();
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.getContentPane().add(wwd);
        f.pack();
        f.setVisible(true);
    }

    private static SurfaceImage createSurfaceImage() {
        return new SurfaceImage("3.png", Arrays.asList(
            LatLon.fromDegrees(20d, -115d),
            LatLon.fromDegrees(20d, -105d),
            LatLon.fromDegrees(32d, -102d),
            LatLon.fromDegrees(30d, -115d)
        ));
    }

    private static Polyline createLine(SurfaceImage surfaceImage) {
        Polyline polyline = new Polyline(surfaceImage.getCorners(), 0);
        polyline.setFollowTerrain(true);
        polyline.setClosed(true);
        polyline.setPathType(Polyline.RHUMB_LINE);
        polyline.setColor(new Color(0, 255, 0));
        return polyline;
    }
}
