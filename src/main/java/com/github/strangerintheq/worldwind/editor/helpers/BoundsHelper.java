package com.github.strangerintheq.worldwind.editor.helpers;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import javax.inject.Singleton;


import gov.nasa.worldwind.avlist.AVKey;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;

import gov.nasa.worldwind.render.SurfacePolyline;


@Singleton
//@Creatable
public class BoundsHelper extends Helper {
	
	private SurfacePolyline primitive;

	public void redraw(RenderableLayer layer, List<Position> points) {
		if (points.size() < 2)
			return;
		
		if (null == primitive) {
			primitive = helperPolyline(Color.CYAN);
			primitive.setPathType(AVKey.RHUMB_LINE);
		}
			
		ensure(layer, primitive);
		
		double minLat = 90;
		double minLon = 180;
		double maxLat = -90;
		double maxLon = -180;
		
		for (Position position : points) {
			minLat = Math.min(minLat, position.getLatitude().degrees);
			minLon = Math.min(minLon, position.getLongitude().degrees);
			maxLat = Math.max(maxLat, position.getLatitude().degrees);
			maxLon = Math.max(maxLon, position.getLongitude().degrees);
		}
		
		primitive.setLocations(Arrays.asList(
			Position.fromDegrees(minLat, minLon, 10),
			Position.fromDegrees(minLat, maxLon, 10),
			Position.fromDegrees(maxLat, maxLon, 10),
			Position.fromDegrees(maxLat, minLon, 10),
			Position.fromDegrees(minLat, minLon, 10)
		));
	}
}
