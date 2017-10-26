package worldwind.common;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;

public class WwMouseAdapter extends MouseAdapter implements SelectListener {
	
	private MouseInteraction interaction;
	
	public WwMouseAdapter(MouseInteraction interaction) {
		this.interaction = interaction;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		interaction.onMouseDown(e.getPoint(), e.getButton() == MouseEvent.BUTTON3);
	}
	

	public void mouseDragged(MouseEvent e) {
		if (interaction.onMouseDrag(e.getPoint(), e.isControlDown())) 
			e.consume();
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		interaction.onMouseUp(e.getPoint());
	}


	public void selected(SelectEvent event) {
		if (!event.getEventAction().equals(SelectEvent.ROLLOVER))
			return;
		interaction.mouseOver(event.getObjects());
	}
}
