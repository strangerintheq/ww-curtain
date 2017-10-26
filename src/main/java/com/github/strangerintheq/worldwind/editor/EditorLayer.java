package com.github.strangerintheq.worldwind.editor;

import java.awt.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;

import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.pick.PickedObject;
import gov.nasa.worldwind.pick.PickedObjectList;
import gov.nasa.worldwind.util.Logging;
import com.github.strangerintheq.worldwind.common.CursorManager;
import com.github.strangerintheq.worldwind.common.MouseInteraction;
import com.github.strangerintheq.worldwind.common.WwMouseAdapter;
import com.github.strangerintheq.worldwind.editor.helpers.BoundsHelper;
import com.github.strangerintheq.worldwind.editor.helpers.EditorHelper;
import com.github.strangerintheq.worldwind.editor.helpers.RotationHelper;
import com.github.strangerintheq.worldwind.editor.points.AnchorPoint;
import com.github.strangerintheq.worldwind.editor.points.CenterPoint;

//@Creatable
@Singleton
public class EditorLayer extends RenderableLayer implements MouseInteraction {
	
	@Inject
	WorldWindow wwd;

	@Inject
	RotationHelper rotationHelper;

	@Inject
	EditorHelper editorHelper;

	@Inject
	BoundsHelper boundsHelper;

	@Inject
	CursorManager cursorManager;
	
	WwMouseAdapter mouseAdapter;
	Point mouseDownScreenPt;
	AnchorPoint mouseOverPt;
	AnchorPoint mouseDragPt;
	CenterPoint centerPt;
	Position mouseDownPos;
	boolean isRightButton = false;
	
	List<AnchorPoint> anchorPoints = new ArrayList<AnchorPoint>();

	@Override
	public void onMouseUp(Point point) {
		if (canAdd(point)) add();
		mouseDownScreenPt = null;
		mouseDragPt = null;
		rotationHelper.hide(this);
		isRightButton = false;
	}

	private boolean canAdd(Point point) {
		return null == mouseDragPt && null == mouseOverPt &&
			point.equals(mouseDownScreenPt) && !isRightButton;
	}

	private void add() {
		Position pos = wwd.getCurrentPosition();
		if (null == pos)
			return;
		AnchorPoint anchorPoint = new AnchorPoint(pos);
		addRenderable(anchorPoint);
		anchorPoints.add(anchorPoint);
		reconfigurePrimitives();
		updateCenterPoint();		
	}

	private void reconfigurePrimitives() {
		editorHelper.redraw(this, getPositions());
		boundsHelper.redraw(this, getPositions());
	}

	@Override
	public boolean onMouseDrag(Point point, boolean controlDown) {
		if (null == mouseDragPt)
			return false;
		Position currentPosition = wwd.getCurrentPosition();
		if (null == currentPosition)
			return true;
		if (!mouseDragPt.isAnchorPoint()) {
			if (!isRightButton) moveAnchorPoints();			
			mouseDragPt.setPosition(currentPosition);
		} else if (!isRightButton) {
			mouseDragPt.setPosition(currentPosition);
			updateCenterPoint();
		} else if (controlDown) {
			scale();
		} else {
			rotate();
		} 
		reconfigurePrimitives();
		return true;
	}
	
	private void rotate() {
		Position p = wwd.getCurrentPosition();
	    Position center = centerPt.getPosition();
	    double angle = LatLon.greatCircleAzimuth(center, p).radians 
	    		- LatLon.greatCircleAzimuth(center, mouseDragPt.getPosition()).radians;
	    anchorPoints.stream().forEach(anchorPoint -> {
	        double azimuth = LatLon.greatCircleAzimuth(center, anchorPoint.getPosition()).radians + angle;
	        double distance = LatLon.greatCircleDistance(center, anchorPoint.getPosition()).radians;
	        LatLon newPos = LatLon.greatCircleEndPosition(center, azimuth, distance);
	        anchorPoint.setPosition(new Position(newPos, 0));
	    });
	    rotationHelper.redraw(this, centerPt, mouseDownPos, mouseDragPt.getPosition());
	}

	private void scale() {
		Position center = centerPt.getPosition();
		Position prev  = mouseDragPt.getPosition();
		Position curr = wwd.getCurrentPosition();
	    double delta = LatLon.greatCircleDistance(center, curr).radians/
	    		LatLon.greatCircleDistance(center, prev).radians;
	    anchorPoints.stream().forEach(anchorPoint -> {
	    	LatLon newPos = interpolateGreatCircle(delta, center, anchorPoint.getPosition());
	        anchorPoint.setPosition(new Position(newPos, 0));
	    });
	}
	
	private LatLon interpolateGreatCircle(double amount, LatLon value1, LatLon value2) {
        if (value1 == null || value2 == null) {
            String message = Logging.getMessage("nullValue.LatLonIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (LatLon.equals(value1, value2))
            return value1;
        
        Angle azimuth = LatLon.greatCircleAzimuth(value1, value2);
        Angle distance = LatLon.greatCircleDistance(value1, value2);
        Angle pathLength = Angle.fromDegrees(amount * distance.degrees);

        return LatLon.greatCircleEndPosition(value1, azimuth, pathLength);
	}

	private void moveAnchorPoints() {
		Position p = wwd.getCurrentPosition();
		double dLat = p.latitude.degrees - mouseDragPt.getPosition().latitude.degrees;
		double dLon = p.longitude.degrees - mouseDragPt.getPosition().longitude.degrees;
		anchorPoints.stream().forEach(ap -> ap.setPosition(Position.fromDegrees(
			ap.getPosition().latitude.degrees + dLat, 
			ap.getPosition().longitude.degrees + dLon
		)));
	}

	@Override
	public void onMouseDown(Point point, boolean isRightButton) {
		mouseDownScreenPt = point;
		this.isRightButton = isRightButton;
		if (null == mouseOverPt) 
			return;
		mouseDragPt = mouseOverPt;
		mouseDownPos = wwd.getCurrentPosition();
	}

	private void updateCenterPoint() {
		int size = anchorPoints.size();
		if (size < 2)
			return;
		double[] ds = anchorPoints.stream()
				.map(ap -> ap.getPosition().asDegreesArray())
				.reduce((a, b) -> {
					a[0] += b[0];
					a[1] += b[1];
					return a;
				}).get();
		Position p = Position.fromDegrees(ds[0]/size, ds[1]/size, 1);
		if (null == centerPt) {
			centerPt = new CenterPoint(p);
			addRenderable(centerPt);
		} else {
			centerPt.setPosition(p);
		} 
	}

	private List<Position> getPositions() {
		return anchorPoints.stream().map(ap->ap.getPosition()).collect(Collectors.toList());
	}

	public void clear() {
		anchorPoints.clear();
		centerPt = null;
		rotationHelper.hide(this);
		editorHelper.hide(this);
		boundsHelper.hide(this);
		removeAllRenderables();
	}
	
	@Override
	public void mouseOver(PickedObjectList objects) {
		if (null != mouseDragPt)
			return; 
		if (mouseOverPt != null)
			mouseOverPt.highlightOff();
		PickedObject topPickedObject = objects.getTopPickedObject();
		if (topPickedObject == null) {
			mouseOverPt = null;
			cursorManager.arrow();
			return;
		}
		Object pickedObject = topPickedObject.getObject();
		if (pickedObject instanceof AnchorPoint) {
			mouseOverPt = (AnchorPoint) pickedObject;
			mouseOverPt.highlightOn();
			cursorManager.hand();
		} else {
			cursorManager.arrow();
			mouseOverPt = null;
		}
	}

	@Override
	public WwMouseAdapter getAdapter() {
		if (null == mouseAdapter)
			mouseAdapter = new WwMouseAdapter(this);
		return mouseAdapter;
	}
}
