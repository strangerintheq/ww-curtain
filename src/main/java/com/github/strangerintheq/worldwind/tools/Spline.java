package com.github.strangerintheq.worldwind.tools;

import java.util.ArrayList;
import java.util.List;

/**
 * A Cardinal spline (basically a Catmull-Rom with a tension option) implementatio
 */
public class Spline {

    private int rPos = 0;
    private List<Double> pts;
    private int numOfSeg;
    private double[] res;
    
    /**
     * Calculates an array containing points representing a cardinal spline through given point array.
     * Points must be arranged as: [x1, y1, x2, y2, ..., xn, yn].
     *
     * There must be a minimum of two points in the input array but the function
     * is only useful where there are three points or more.
     *
     * The points for the cardinal spline are returned as a new array.
     *
     * @param points - point array
     * @param tension - tension. Typically between [0.0, 1.0] but can be exceeded
     * @param numOfSeg - number of segments between two points (line resolution)
     * @param close - Close the ends making the line continuous
     * @return New array with the calculated points that was added to the path
     */
    double[] calc(double[] points, double tension, int numOfSeg, boolean close) {
        if (points == null || points.length < 2)
            return new double[0];
        tension = tension == 0 ? 0.5 : tension;
        this.pts = new ArrayList<>();
        this.numOfSeg = numOfSeg == 0 ? 25 : numOfSeg;
        int i = 1;
        int l = points.length;
        int rLen = (l-2) * numOfSeg + 2 + (close ? 2 * numOfSeg: 0);
        this.res = new double[rLen];
        double[] cache = new double[(numOfSeg + 2) << 2];
        int cachePtr = 4;
        if (close) {
            pts.add(points[l - 2]);	 // insert end point as first point
            pts.add(points[l - 1]);
            for (double d : points)
                pts.add(d);
            pts.add(points[0]);
            pts.add(points[1]);  // first point as last point
        } else {
            pts.add(points[0]);	 // copy 1. point and insert at beginning
            pts.add(points[1]);
            for (double d : points)
                pts.add(d);
            pts.add(points[l - 2]); // insert end point as first point
            pts.add(points[l - 1]); // duplicate end-points
        }
        // cache inner-loop calculations as they are based on t alone
        cache[0] = 1;	 // 1,0,0,0
        for (; i < numOfSeg; i++) {
            double st = i / numOfSeg,
                    st2 = st * st,
                    st3 = st2 * st,
                    st23 = st3 * 2,
                    st32 = st2 * 3;
            cache[cachePtr++] =	st23 - st32 + 1;	 // c1
            cache[cachePtr++] =	st32 - st23;		 // c2
            cache[cachePtr++] =	st3 - 2 * st2 + st;  // c3
            cache[cachePtr++] =	st3 - st2;			 // c4
        }
        cache[++cachePtr] = 1; // 0,1,0,0
        // calc. points
        parse(cache, l, tension);
        if (close) {
            //l = points.length;
            pts.clear();
            pts.add(points[l - 4]);
            pts.add(points[l - 3]);
            pts.add(points[l - 2]);
            pts.add(points[l - 1]);  // second last and last
            pts.add(points[0]);
            pts.add(points[1]);
            pts.add(points[2]);
            pts.add(points[3]);  // first and second
            parse(cache, 4, tension);
        }
        // add last point
        l = close ? 0 : points.length - 2;
        res[rPos++] = points[l++];
        res[rPos] = points[l];
        return res;
    }

    private void parse(double[] cache, int l, double tension) {
        for (int i = 2, t; i < l; i += 2) {
            Double pt1 = pts.get(i),
                    pt2 = pts.get(i+1),
                    pt3 = pts.get(i+2),
                    pt4 = pts.get(i+3);
            double t1x = (pt3 - pts.get(i-2)) * tension,
                    t1y = (pt4 - pts.get(i-1)) * tension,
                    t2x = (pts.get(i+4) - pt1) * tension,
                    t2y = (pts.get(i+5) - pt2) * tension,
                    c1, c2, c3, c4;
            int c = 0;
            for (t = 0; t < numOfSeg; t++) {
                c1 = cache[c++];
                c2 = cache[c++];
                c3 = cache[c++];
                c4 = cache[c++];
                res[rPos++] = c1 * pt1 + c2 * pt3 + c3 * t1x + c4 * t2x;
                res[rPos++] = c1 * pt2 + c2 * pt4 + c3 * t1y + c4 * t2y;
            }
        }
    }
}
