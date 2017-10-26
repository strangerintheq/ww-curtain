package com.github.strangerintheq.worldwind.primitives;

import java.util.ArrayList;
import java.util.List;

import gov.nasa.worldwind.geom.Frustum;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.DrawContext;

class LineSegmentFactory {

    private final int pixelPerSegment;
    private List<List<Vec4>> segments = new ArrayList<List<Vec4>>();
    private List<Vec4> currentSegment = new ArrayList<Vec4>();

    LineSegmentFactory(int pixelPerSegment) {
        this.pixelPerSegment = pixelPerSegment;
    }

    void clear() {
        segments.clear();
        currentSegment.clear();
    }

    void addPoint(DrawContext dc, Position p) {
        Vec4 pos = getScreenSpacePointPosition(dc, p);
        if (pos == null) {
            pushSegment();
            return;
        }

        if (!currentSegment.isEmpty()) {
            Vec4 prev = currentSegment.get(currentSegment.size() - 1);
            double dist = prev.distanceTo2(pos);
            if (
//                    !point.isLast && TODO
                    dist < pixelPerSegment / 2) {
                return;
            }
        }
        currentSegment.add(pos);
    }

    void pushSegment() {
        if (currentSegment.size() >= 2) {
            segments.add(new ArrayList<Vec4>(currentSegment));
        }
        currentSegment.clear();
    }

    Vec4 getScreenSpacePointPosition(DrawContext dc, Position pt) {
        if (pt == null)
            return null;
        Globe globe = dc.getView().getGlobe();
        double alt = pt.elevation == 0 ? globe.getElevation(pt.latitude, pt.longitude) : pt.elevation;
        Vec4 cartesian = globe.computePointFromPosition(pt.latitude, pt.longitude, alt);
        Frustum frustum = dc.getView().getFrustumInModelCoordinates();
        if (!frustum.contains(cartesian))
            return null;
        return dc.getView().project(cartesian);
    }

    List<List<Vec4>> getSegments() {
        return segments;
    }
}
