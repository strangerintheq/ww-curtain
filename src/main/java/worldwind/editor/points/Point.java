package worldwind.editor.points;

import java.awt.Color;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.pick.PickSupport;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.PointPlacemark;
import gov.nasa.worldwind.util.OGLStackHandler;
import gov.nasa.worldwind.util.OGLUtil;

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
