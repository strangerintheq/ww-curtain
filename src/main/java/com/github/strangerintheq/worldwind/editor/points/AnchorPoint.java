package com.github.strangerintheq.worldwind.editor.points;

import java.awt.Color;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.Offset;
import gov.nasa.worldwind.render.PointPlacemarkAttributes;

public class AnchorPoint extends Point {

	//private static BufferedImage pt = FileUtil.readImage("images/circle.png");

	public AnchorPoint(Position position) {
		super(position);
		PointPlacemarkAttributes attributes = new PointPlacemarkAttributes();
		attributes.setImageOffset(Offset.CENTER);
		attributes.setUsePointAsDefaultImage(true);
		attributes.setScale(.6);
		setAttributes(attributes);
		setHighlightAttributes(attributes);
	}

	
	public void highlightOn() {
		getAttributes().setImageColor(Color.YELLOW);
		getHighlightAttributes().setImageColor(Color.YELLOW);
	}

	public void highlightOff() {
		getAttributes().setImageColor(Color.ORANGE);
		getHighlightAttributes().setImageColor(Color.ORANGE);
	}
	
	public boolean isAnchorPoint() {
		return true;
	}

}
