package worldwind.primitives;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.Earth.BMNGWMSLayer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.Polyline;
import worldwind.common.ExampleBoilerplate;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Example extends ExampleBoilerplate{

    public Example() {
        renderable(new ScreenSpaceLine(getPoints()));
    }

    private List<Position> getPoints() {
        return Arrays.asList(
            Position.fromDegrees(60, 30, 0),
            Position.fromDegrees(50, 30, 0)
        );
    }

    public static void main(String[] args) {
        new Example();
    }

}
