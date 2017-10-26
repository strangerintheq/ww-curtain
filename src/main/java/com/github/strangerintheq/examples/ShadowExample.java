package com.github.strangerintheq.examples;

import gov.nasa.worldwind.layers.Earth.BMNGWMSLayer;

import com.github.strangerintheq.worldwind.shadow.ShadowLayer;

public class ShadowExample extends ExampleBoilerplate {

    public static void main(String[] args) {
        ShadowLayer shadowLayer = new ShadowLayer(new BMNGWMSLayer());
        new ShadowExample().getModel().getLayers().add(shadowLayer);
    }
}
