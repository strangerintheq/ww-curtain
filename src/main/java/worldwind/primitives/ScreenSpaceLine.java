package worldwind.primitives;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Matrix;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Renderable;

public class ScreenSpaceLine implements Renderable {

    private static int pixelPerSegment = 8;
    private Matrix scratchMatrix;

    private PathProperties properties;
    private LineSegmentFactory segmentFactory;
    private LineTriangulator triangulator;
    private List<Position> points;
    private List<Position> positions;
    private List<Position> tessellated;
    private long timestamp;
    private DrawContext dc;

    public ScreenSpaceLine(List<Position> points) {
        this.points = points;
        this.segmentFactory = new LineSegmentFactory(pixelPerSegment);
        this.triangulator = new LineTriangulator();
        this.scratchMatrix = new Matrix(1);
        this.properties = new PathProperties();;
    }

    public void render(DrawContext dc) {
        this.dc = dc;
        doRender();
        this.dc = null;
    }

    void doRender() {
        PathProperties props = this.properties;
        if (!props.enabled || props.opacity <= 0 || dc.isPickingMode() || points.size() < 2) {
            return;
        }
        if (tessellated == null || expired()) {
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
        if (!expired())
            return;
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
        var gl = dc.currentGlContext;
        dc.findAndBindProgram(ScreenSpaceLineProgram);
        var program = dc.currentProgram;
        gl.bindBuffer(gl.ARRAY_BUFFER, dc.unitQuadBuffer());
        gl.enableVertexAttribArray(program.vertexPointLocation);
        gl.disable(gl.CULL_FACE);
        gl.blendFunc(gl.ONE, gl.ONE_MINUS_SRC_ALPHA);
        this.depth && gl.depthMask(false);
    }

    void endDrawing() {
        var gl = dc.currentGlContext;
        gl.disableVertexAttribArray(dc.currentProgram.vertexPointLocation);
        gl.bindBuffer(gl.ARRAY_BUFFER, null);
        gl.enable(gl.CULL_FACE);
        this.depth && gl.depthMask(true);
    }

    void renderBucket() {
        var gl = dc.currentGlContext;
        var program = dc.currentProgram;

        var imageTransform = Matrix.fromIdentity();

        var w = dc.navigatorState.viewport.width;
        var h = dc.navigatorState.viewport.height;

        imageTransform.setScale(w, h, 1);

        var vertexCount = bindVertexBufferData(dc, bucket);
        if (!vertexCount) return;
        gl.vertexAttribPointer(program.vertexPointLocation, 4, gl.FLOAT, false, 0, 0);

        scratchMatrix.copy(dc.screenProjection);
        scratchMatrix.multiplyMatrix(imageTransform);

        program.loadModelviewProjection(gl, scratchMatrix);
        program.loadColor(gl, bucket.color);
        program.loadOpacity(gl, bucket.opacity);
        program.loadAspectRatio(gl, w/h);
        program.loadWidth(gl, bucket.width/w/2);
        gl.drawArrays(gl.TRIANGLES, 0, vertexCount);

        // debug
        // program.loadColor(gl, Color.WHITE);
        // gl.drawArrays(gl.LINES, 0, vertexCount);
    }

    void bindVertexBufferData() {
        GL2 gl = dc.getGLContext().getGL().getGL2();
        List<Vec4> points = triangulator.convertLineBucketToSetOfTriangles(segmentFactory.getSegments());
//        var pointsArray = new Float32Array(points.length * 4);
//        var index = 0;
//        points.forEach(function(pt) {
//            pointsArray[index] = pt[0];     // x
//            pointsArray[index + 1] = pt[1]; // y
//            pointsArray[index + 2] = pt[2]; // angle
//            pointsArray[index + 3] = pt[3]; // amount
//            index += 4;
//        });
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, gl.glcreateBuffer());
        gl.bufferData(gl.ARRAY_BUFFER, pointsArray, gl.STATIC_DRAW);
        return points.length;
    }
}
