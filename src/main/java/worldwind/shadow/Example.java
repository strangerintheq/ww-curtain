package worldwind.shadow;

import gov.nasa.worldwind.layers.Earth.BMNGWMSLayer;
import worldwind.common.ExampleBoilerplate;

public class Example extends ExampleBoilerplate {

    public static void main(String[] args) {
        ShadowLayer shadowLayer = new ShadowLayer(new BMNGWMSLayer());
        new Example().getModel().getLayers().add(shadowLayer);
    }
}
