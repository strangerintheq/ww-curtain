package com.github.strangerintheq.worldwind.editor.helpers;

import java.awt.Color;
import java.util.List;
import javax.inject.Singleton;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.SurfacePolyline;

@Singleton
//@Creatable
public class EditorHelper extends Helper {

	private SurfacePolyline primitive;

	public void redraw(RenderableLayer layer, List<Position> points) {
		if (null == primitive)
			primitive = helperPolyline(Color.ORANGE);
		ensure(layer, primitive);		
		primitive.setLocations(points);
	}
}
