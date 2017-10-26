package worldwind.editor.helpers;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.SurfacePolyline;
import worldwind.editor.points.CenterPoint;

//@Creatable
@Singleton
public class RotationHelper extends Helper {

	@Inject
    WorldWindow wwd;
	
	private SurfacePolyline angle;
	private SurfacePolyline arc1;
	private SurfacePolyline arc2;
	
	protected List<Position> computeArc(double radius, LatLon center, LatLon from, LatLon to) {
        if (radius == 0)
            return null;
        double heading = LatLon.greatCircleAzimuth(center, from).radians;
        double totalAngle = LatLon.greatCircleAzimuth(center, to).radians - heading;
        if (totalAngle < 0) totalAngle += Math.PI * 2;
        int numLocations= 2 + (int) Math.abs(Math.toDegrees(totalAngle))/10;
        double da = totalAngle / (numLocations - 1);
        double globeRadius = wwd.getView().getGlobe().getRadiusAt(center.getLatitude(), center.getLongitude());
        Position[] locations = new Position[numLocations];
        
        for (int i = 0; i < numLocations; i++){
            double angle = i * da;
            double xLength = radius * Math.cos(angle);
            double yLength = radius * Math.sin(angle);
            double distance = Math.sqrt(xLength * xLength + yLength * yLength);
            // azimuth runs positive clockwise from north and through 360 degrees.
            double azimuth =  heading + Math.acos(xLength / distance) * Math.signum(yLength);
            locations[i] = new Position(LatLon.greatCircleEndPosition(center, azimuth, distance / globeRadius),1);
        }
        System.out.println(System.currentTimeMillis() + " heading: " + Math.toDegrees(heading));
        System.out.println(System.currentTimeMillis() + " totalAngle: " + Math.toDegrees(totalAngle));
        return Arrays.asList(locations);
    }

	public void redraw(RenderableLayer layer, CenterPoint center, Position from, Position to) {
		if (null == angle)
		    angle = helperPolyline(Color.YELLOW);
		ensure(layer, angle);		
		angle.setLocations(Arrays.asList(from, center.getPosition(), to));

	    double arcRadius = wwd.getView().computePixelSizeAtDistance(center.getDistanceFromEye()) * 30;
	    
		if (null == arc1)
			arc1 = helperPolyline(Color.YELLOW);
		ensure(layer, arc1);
		arc1.setLocations(computeArc(arcRadius, center.getPosition(), from, to));

		if (null == arc2)
			arc2 = helperPolyline(Color.YELLOW);
		ensure(layer, arc2);
		arc2.setLocations(computeArc(arcRadius * 1.3, center.getPosition(), from, to));
	}
}
