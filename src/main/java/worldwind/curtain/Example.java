package worldwind.curtain;

import gov.nasa.worldwind.BasicModel;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.render.Polyline;
import gov.nasa.worldwind.render.SurfaceImage;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;

public class Example {
    public static void main(String[] args) throws IOException {
        SurfaceImage surfaceImage = createSurfaceImage();
        Polyline polyline = createLine(surfaceImage);

        CurtainControlLayer curtainControlLayer = new CurtainControlLayer();
        CurtainLayer curtainLayer = new CurtainLayer();
        curtainLayer.setController(curtainControlLayer);
        curtainLayer.addRenderable(surfaceImage);
        curtainLayer.addRenderable(polyline);

        WorldWindowGLCanvas wwd = new WorldWindowGLCanvas();
        wwd.addSelectListener(curtainControlLayer);

        wwd.setPreferredSize(new Dimension(800, 600));
        wwd.setModel(new BasicModel());
        wwd.getModel().getLayers().add(curtainControlLayer);
        wwd.getModel().getLayers().add(curtainLayer);

        JFrame frame = new JFrame();
        frame.setTitle("Толстый шар");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(wwd);
        frame.pack();
        frame.setVisible(true);
    }

    private static SurfaceImage createSurfaceImage() {
        return new SurfaceImage("images/nasaLogo.png", Arrays.asList(
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
