package worldwind.primitives;

import java.awt.Color;

public class LineProperties {
    double opacity = 1;
    double width = 2;
    boolean enabled = true;
    boolean depth;
    boolean simplify;
    double expirationPeriod = (1 + Math.random()) * 150;
    Color color = Color.WHITE;
    LineJoin lineJoin = LineJoin.NONE;
    double miterLimit = 3;
}
