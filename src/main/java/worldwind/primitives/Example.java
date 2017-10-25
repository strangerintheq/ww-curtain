package worldwind.primitives;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.Earth.BMNGWMSLayer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.Polyline;
import worldwind.common.ExampleBoilerplate;
import java.awt.*;
import java.util.*;

public class Example extends ExampleBoilerplate{
    public static void main(String[] args){
        LayerList layers = new Example().getModel().getLayers();
        RenderableLayer renderableLayer = new RenderableLayer();
        layers.add(new BMNGWMSLayer());
        layers.add(renderableLayer);
//        renderableLayer.addRenderable(createLine());
        renderableLayer.addRenderable(new ScreenSpaceLine(points()));
    }

    private static Polyline createLine() {
        Polyline polyline = new Polyline(points(), 0);
        polyline.setFollowTerrain(true);
        polyline.setClosed(true);
        polyline.setPathType(Polyline.LOXODROME);
        polyline.setColor(new Color(0, 255, 0));
        return polyline;
    }

    private static java.util.List<Position> points() {
        return Arrays.asList(
                Position.fromDegrees(60,30,0),
                Position.fromDegrees(50,30,0)
        );
    }
}
