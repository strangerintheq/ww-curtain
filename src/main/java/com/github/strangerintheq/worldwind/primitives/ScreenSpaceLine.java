package com.github.strangerintheq.worldwind.primitives;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import com.jogamp.common.nio.Buffers;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Renderable;


import com.github.strangerintheq.worldwind.common.FileUtil;
import com.github.strangerintheq.worldwind.common.ShaderProgram;

public class ScreenSpaceLine implements Renderable {

    private static int pixelPerSegment = 8;

    private LineProperties properties;
    private LineSegmentFactory segmentFactory;
    private LineTriangulator triangulator;
    private List<Position> points;
    private List<Position> positions;
    private List<Position> tessellated = new ArrayList<Position>();
    private long timestamp;
    private DrawContext dc;
    private static ShaderProgram program;

    public ScreenSpaceLine(List<Position> points) {
        this.points = points;
        this.segmentFactory = new LineSegmentFactory(pixelPerSegment);
        this.triangulator = new LineTriangulator();
        this.properties = new LineProperties();
    }

    public void render(DrawContext dc) {
        this.dc = dc;
        LineProperties props = this.properties;
        if (!props.enabled || props.opacity <= 0 || dc.isPickingMode() || points.size() < 2) {
            return;
        }
        if (tessellated.isEmpty() || expired()) {
            simplifyBoundaries();
            makeTessellatedPositionsIfNeeded();
        }
        segmentFactory.clear();
        for (Position pos : tessellated) {
            segmentFactory.addPoint(dc, pos);
        }
        segmentFactory.pushSegment();
        drawBucketGL();
    }

    boolean expired() {
        return System.currentTimeMillis() - timestamp > properties.expirationPeriod;
    }

    // добавляет в линию точки так, чтобы она повторяла кривизну поверхности
    void makeTessellatedPositionsIfNeeded() {
        tessellated.clear();
        timestamp = System.currentTimeMillis();
        Position posB, posA = positions.get(0);
        Vec4 ptB, ptA = blh2xyz(posA);
        for (int i = 1; i < positions.size(); i++) {
            posB = positions.get(i);
            ptB = blh2xyz(posB);
            double radius = dc.getView().getGlobe().getRadiusAt(posB);
            double radians = LatLon.greatCircleDistance(posA, posB).radians;
            double distance = radians * radius;
            double currentPosition = 0;
            if (tessellated.isEmpty())
                tessellated.add(posA); // only needed for first segment
            if (distance > pixelSizeAt(ptB) * pixelPerSegment) {
                Vec4 nextPt = ptA;
                while (currentPosition < distance) {
                    currentPosition += pixelSizeAt(nextPt) * pixelPerSegment;
                    Position nextPos = Position.interpolateGreatCircle(currentPosition/distance, posA, posB);
                    tessellated.add(nextPos);
                    nextPt = blh2xyz(nextPos);
                }
            } else {
                tessellated.add(posB); // close if no tessellation needed
            }
//            tessellated.get(tessellated.size() - 1).isLast = true; TODO
            posA = posB;
        }
    }

    double pixelSizeAt(Vec4 pt) {
        double d = dc.getView().getCurrentEyePoint().distanceTo3(pt);
        return dc.getView().computePixelSizeAtDistance(d);
    }

    void simplifyBoundaries() {
        if (!this.properties.simplify) {
            this.positions = new ArrayList<Position>(points);
            return;
        }
//        var pts = this.points.map(function(pt) {
//            return {x: pt.latitude, y: pt.longitude, z: pt.altitude}
//        });
//        var p1 = blh2xyz(dc, this.points[0], [0,0,0]);
//        var p2 = blh2xyz(dc, this.points[Math.floor(this.points.length/2)], [0,0,0]);
//        var d1 = dc.navigatorState.eyePoint.distanceTo(p1);
//        var d2 = dc.navigatorState.eyePoint.distanceTo(p2);
//        var tolerance = Math.min(d1, d2)/1e8;
//        var result = SimplifyLine(pts, tolerance);
//        this.positions = result.map(function(pt) {
//            return new Position(pt.x, pt.y, pt.z);
//        });
    }

    Vec4 blh2xyz(Position blh) {
        return dc.computePointFromPosition(blh, WorldWind.RELATIVE_TO_GROUND);
    }

    void drawBucketGL() {
        beginDrawing();
        try {
            renderBucket();
        } finally {
            endDrawing();
        }
    }

    void beginDrawing() {
        GL2 gl = dc.getGL().getGL2();
        if (null == program)
            program = new ShaderProgram(gl, source("vertex"), source("fragment"));
        gl.glDisable(GL2.GL_CULL_FACE);
        gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA);
        if (this.properties.depth)
            gl.glDepthMask(false);
    }

    private String source(String type) {
        return FileUtil.readText(String.format("shaders/primitives/line.%s.glsl", type));
    }

    void endDrawing() {
        GL2 gl = dc.getGL().getGL2();
        gl.glEnable(GL2.GL_CULL_FACE);
        if (this.properties.depth)
            gl.glDepthMask(true);
    }

    void renderBucket() {
        GL2 gl = dc.getGL().getGL2();
        double w = dc.getView().getViewport().width;
        double h = dc.getView().getViewport().height;
        try {
            program.useProgram();
            int vertexCount = bindVertexBufferData();
            if (vertexCount == 0)
                return;
            program.setFloat("aspectRatio", w/h);
            program.setFloat("width", properties.width/w);
            program.setColor("color", properties.color);
            gl.glDrawArrays(GL2.GL_TRIANGLES, 0, vertexCount);
        } finally {
            program.disable();
        }
    }

    int bindVertexBufferData() {
        GL2 gl = dc.getGLContext().getGL().getGL2();
        List<Vec4> points = triangulator.convertLineBucketToSetOfTriangles(
                dc, segmentFactory.getSegments(), properties);
        if (points.isEmpty())
            return 0;
        FloatBuffer buffer = Buffers.newDirectFloatBuffer(points.size() * 4);
        for (Vec4 point : points) {
            buffer.put((float)point.x);
            buffer.put((float)point.y);
            buffer.put((float)point.z);
            buffer.put((float)point.w);
        }
        gl.glVertexPointer(4, GL2.GL_FLOAT, 0, buffer.rewind());
        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        return points.size();
    }
}
