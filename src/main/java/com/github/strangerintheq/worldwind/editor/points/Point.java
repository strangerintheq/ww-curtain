package com.github.strangerintheq.worldwind.editor.points;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.pick.PickSupport;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.PointPlacemark;

public class Point extends PointPlacemark {

	public Point(Position position) {
		super(position);
	}

	@Override
	protected void drawOrderedRenderable(DrawContext dc) {
		super.drawOrderedRenderable(dc);
	}

	/***
	 * 	purpose of override is
	 *	gl.glDepthFunc(GL.GL_ALWAYS);
	 */
	@Override
	protected void drawPoint(DrawContext dc, PickSupport pickCandidates) {
		super.drawPoint(dc, pickCandidates);
	}
}
