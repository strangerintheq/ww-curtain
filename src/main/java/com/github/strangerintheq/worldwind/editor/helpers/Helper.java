package com.github.strangerintheq.worldwind.editor.helpers;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.SurfacePolyline;

public class Helper {
	
	protected List<Renderable> activeRenderables = new ArrayList<Renderable>();
	
	private ShapeAttributes helperAttribues;
	
	public void hide(RenderableLayer layer) {
		for (Renderable renderable : activeRenderables) {
			layer.removeRenderable(renderable);
		}
		activeRenderables.clear();
	}

	public void ensure(RenderableLayer layer, Renderable helper) {
		if (activeRenderables.contains(helper))
			return;
		activeRenderables.add(helper);
		layer.addRenderable(helper);		
	}
	
	protected ShapeAttributes getAttributes(Color c) {
		if (null == helperAttribues)
			helperAttribues = createAttributes(c);
		return helperAttribues;
	}
	
	public SurfacePolyline helperPolyline(Color c) {
		ShapeAttributes attrs = getAttributes(c);
		SurfacePolyline helper = new SurfacePolyline();
//		helper.setFollowTerrain(true);
//		helper.setEnableDepthOffset(true);
		helper.setAttributes(attrs);
		helper.setHighlightAttributes(attrs);
		return helper;
	}
	
	public static ShapeAttributes createAttributes(Color c) {
		Material material = new Material(c);
		ShapeAttributes attributes = new BasicShapeAttributes();
		attributes.setInteriorOpacity(0.5);
		attributes.setOutlineMaterial(material);
		attributes.setInteriorMaterial(material);
		attributes.setOutlineWidth(5);
		return attributes;
	}
}
