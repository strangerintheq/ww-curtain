import java.awt.Point;
import java.awt.image.BufferedImage;

import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.pick.PickedObject;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.ScreenImage;

public class CurtainControlLayer extends RenderableLayer implements SelectListener {

    private static final BufferedImage knobImage = FileUtil.readImage("images/knob.png");
    private static final BufferedImage curtainImage = FileUtil.readImage("images/curtain.png");

    private ScreenImage knob1;
    private ScreenImage knob2;
    private ScreenImage curtain;

    public CurtainControlLayer() {
        setPickEnabled(true);
    }

    @Override
    protected void doPreRender(DrawContext drawContext) {
        super.doPreRender(drawContext);
        if (null == knob1) knob1 = knob(knobImage.getHeight() / 2);
        if (null == knob2) knob2 = knob(drawContext.getDrawableHeight() - knobImage.getHeight() / 2);
        if (null == curtain) curtain = image(curtainImage);
        curtain.setRotation(45.);
    }

    ScreenImage knob(int y) {
        ScreenImage knob = image(knobImage);
        knob.setScreenLocation(new Point(400, y));
        return knob;
    }

    private ScreenImage image(BufferedImage knobImage) {
        ScreenImage knob = new ScreenImage();
        knob.setImageSource(knobImage);
        addRenderable(knob);
        return knob;
    }

    public void selected(SelectEvent event) {
        if (!event.getEventAction().equals(SelectEvent.DRAG)) return;
        for (PickedObject po : event.getObjects()) {
            ScreenImage dragged = null;
            if (po.getObject().equals(knob1)) dragged = knob1;
            if (po.getObject().equals(knob2)) dragged = knob2;
            if (dragged == null) continue;
            int x = event.getPickPoint().x;
            if (x < 10) x = 10;
            if (x > 790) x = 790;
            Point moveTo = new Point(x, dragged.getScreenLocation().y);
            dragged.setScreenLocation(moveTo);
            event.consume();
        }
    }
}
