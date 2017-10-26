package com.github.strangerintheq.examples;

import gov.nasa.worldwind.geom.Position;

import com.github.strangerintheq.worldwind.primitives.ScreenSpaceLine;

import java.util.*;
import java.util.List;

public class LineExample extends ExampleBoilerplate{

    LineExample() {
        renderable(new ScreenSpaceLine(getPoints()));
    }

    private List<Position> getPoints() {
        return Arrays.asList(
            Position.fromDegrees(60, 30, 0),
            Position.fromDegrees(50, 30, 0)
        );
    }

    public static void main(String[] args) {
        new LineExample();
    }
}
