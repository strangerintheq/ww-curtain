package com.github.strangerintheq.worldwind.common;

import java.awt.Point;

import gov.nasa.worldwind.pick.PickedObjectList;

public interface MouseInteraction {

	void onMouseUp(Point point);

	boolean onMouseDrag(Point point, boolean controlDown);

	void onMouseDown(Point point, boolean isRightButton);

	void mouseOver(PickedObjectList objects);

	WwMouseAdapter getAdapter();
}
