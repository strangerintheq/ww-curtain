package worldwind.editor.points;

import java.awt.Color;

import gov.nasa.worldwind.geom.Position;

public class CenterPoint extends AnchorPoint {

	public CenterPoint(Position position) {
		super(position);
		getAttributes().setScale(0.9d);
		getAttributes().setImageColor(Color.ORANGE);
	}

	@Override
	public boolean isAnchorPoint() {
		return false;
	}
	
}
