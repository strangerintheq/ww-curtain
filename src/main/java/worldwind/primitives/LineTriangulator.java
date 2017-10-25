package worldwind.primitives;

import java.util.ArrayList;
import java.util.List;

import gov.nasa.worldwind.geom.Vec4;

public class LineTriangulator {

    private List<Vec4> points = new ArrayList<Vec4>();
    private PathProperties props;

    List<Vec4> convertLineBucketToSetOfTriangles(List<List<Vec4>> segments, PathProperties props) {
        this.props = props;
        points.clear();
        for (List<Vec4> segment : segments) {
            handleSegment(segment);
        }
        return points;
    }

    Vec4 getPoint(List<Vec4> segment, int index) {
        if (index >= 0 && index < segment.size())
            return segment.get(index);
        return null;
    }


    void addPoint(Vec4 pt, double angle, double amount) {
        points.add(new Vec4(
                pt.x / dc.navigatorState.viewport.width,
                pt.y / dc.navigatorState.viewport.height,
                angle,
                amount
        ));
    }

    void addTriangle(Vec4 pt, double d1, double d2, double shift) {
        addPoint(pt, 0, 0);
        addPoint(pt, d1, 1);
        addPoint(pt, d2, shift);
    }

    void addSector(Vec4 pt, double dFrom, double dTo) {
        double from = Math.min(dFrom, dTo);
        double to = Math.max(dFrom, dTo);
        if (to - from > 180) {
            from += 360;
            // swap variables
            double tmp = from;
            from = to;
            to = tmp;
        }
        for (double i = from; i < to; i += 10) {
            double dir = i + 10;
            dir = dir > to ? to : dir;
            addTriangle(pt, i, dir, 1);
        }
    }

        void join(Vec4 point, double segmentSideDir, double bisectDir, boolean isLeft) {
            if (props.lineJoin == LineJoin.ROUND) {
                addSector(point, segmentSideDir, bisectDir);
            } else if (props.lineJoin != LineJoin.NONE) {
                double deg = angleBetweenDirections(segmentSideDir, bisectDir);
                if (deg > 90) deg = 180 - deg;
                if (deg < -90) deg = - 180 - deg;
                double alpha = Math.toRadians(deg);
                double cosAlpha = Math.cos(alpha);
                if (props.lineJoin == LineJoin.BEVEL) {
                    addTriangle(point, segmentSideDir, bisectDir, cosAlpha);
                }
                if (props.lineJoin == LineJoin.MITER) {
                    double m = 2;
                    if (1/cosAlpha > m) {
                        double sign = isLeft ? 1 : -1;
                        if (sign + Math.signum(alpha) == 0) {
                            sign *= -1;
                        }

                        double x = Math.tan(Math.PI/2 - alpha)*(1/Math.cos(alpha) - m);
                        x = Math.sqrt(x*x + m*m);
                        double d2 = bisectDir - sign * Math.toDegrees(Math.acos(m/x));

                        addTriangle(point, segmentSideDir, d2, x);
                        addPoint(point, 0, 0);
                        addPoint(point, bisectDir, m);
                        addPoint(point, d2, x);

                    } else {
                        addTriangle(point, segmentSideDir, bisectDir, 1/cosAlpha);
                    }
                }
            }
        }

    double lineDir(double ax, double ay, double bx, double by) {
        return Math.toDegrees(Math.atan2(by - ay, bx - ax));
    }

    double ptShiftDir(Double d0, Double d1, Boolean isLeft) {
        double result;
        if (isLeft != null && Math.abs(angleBetweenDirections(d1, d0)) > 45)
            result = (isLeft ? d1 : d0) + 90;
        else if (d0 == null)
            result = d1 + 90;
        else if (d1 == null)
            result = d0 + 90;
        else {
            d0 *= Math.PI / 180;
            d1 *= Math.PI / 180;
            double x = Math.cos(d0) + Math.cos(d1);
            double y = Math.sin(d0) + Math.sin(d1);
            result = lineDir(0,0, x, y) + 90;
        }
        return normalizeAngle(result);
    }

    double invertDir(double d) {
        return d > 0 ? d - 180 : d + 180;
    }

    double angleBetweenDirections(double d0, double d1) {
        return normalizeAngle(d1 - d0);
    }

    double normalizeAngle(double angle) {
        if (angle < -180)
            return angle + 360;
        if (angle > 180)
            return angle - 360;
        return angle;
    }


    void handleLineJoin(double seg0dir, double seg1dir, Vec4 point, double ptLeftDir,
                        double ptRightDir, double maxAngle, boolean isLeft) {
        double slope = angleBetweenDirections(seg0dir, seg1dir);
        if (Math.abs(slope) > maxAngle) {
            double direction = ptShiftDir(seg1dir, seg0dir, false);
            if (slope > maxAngle) join(point, ptLeftDir, direction, isLeft);
            if (slope < -maxAngle) join(point, ptRightDir, invertDir(direction), isLeft);
        }
    }

        void handleSegment(List<Vec4> segment) {
            int end = segment.size() - 2;
            for (int i = 0; i <= end; i++) {
                Vec4 p = getPoint(segment, i - 1); // previous line left point (null for first line in segment)
                Vec4 s = getPoint(segment, i);     // current line start point
                Vec4 e = getPoint(segment, i + 1); // current line end point
                Vec4 n = getPoint(segment, i + 2); // next line right point (null for last line in segment)

                double pd = lineDir(p, s); // previous line direction  (null for first line in segment)
                double cd = lineDir(s, e); // current line direction
                double nd = lineDir(e, n); // next line direction (null for last line in segment)

                // calc points shift direction in angles (shifting performed by shader)
                double sl = ptShiftDir(pd, cd, true);  // start left point shift direction
                double sr = invertDir(sl);             // start right point shift direction
                double el = ptShiftDir(cd, nd, false); // end left point shift direction
                double er = invertDir(el);             // end right point shift direction

                if (i != end) handleLineJoin(nd, cd, e, sl, er, 45, true);  // el  el er
                addPoint(s, sl, 1); addPoint(s, sr, 1); addPoint(e, el, 1);  //  *   *-*
                                                                             //  |\   \|
                addPoint(s, sr, 1); addPoint(e, el, 1); addPoint(e, er, 1);  //  *-*   *
                                                                             // sl sr  sr
                if (i != 0) handleLineJoin(cd, pd, s, el, sr, 45, false);
            }
        }
    }
}
