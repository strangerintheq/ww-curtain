import java.awt.*;
import java.awt.image.BufferedImage;

import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.pick.PickedObject;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Offset;
import gov.nasa.worldwind.render.ScreenImage;
import gov.nasa.worldwind.render.Size;

public class CurtainControlLayer extends RenderableLayer implements SelectListener {

    private static final BufferedImage knobImage = FileUtil.readImage("images/knob.png");
    private static final BufferedImage curtainImage = FileUtil.readImage("images/curtain.png");

    private ScreenImage curtain = curtain();
    private ScreenImage knob1 = knob(1.);
    private ScreenImage knob2 = knob(0.);
    private Point curtainDragStartPoint;
    private Double knob1DragStartOffset;
    private Double knob2DragStartOffset;

    @Override
    public void preRender(DrawContext drawContext) {
        super.preRender(drawContext);
        reconfigureCurtain(drawContext);
    }

    public void selected(SelectEvent event) {
        if (event.getEventAction().equals(SelectEvent.DRAG_END)) {
            curtainDragStartPoint = null;
            knob1DragStartOffset = null;
            knob2DragStartOffset = null;
            return;
        }
        if (!event.getEventAction().equals(SelectEvent.DRAG)) return;
        ScreenImage dragged = null;
        for (PickedObject po : event.getObjects()) {
            if (po.getObject().equals(curtain)) dragged = curtain;
            if (po.getObject().equals(knob1)) dragged = knob1;
            if (po.getObject().equals(knob2)) dragged = knob2;
        }
        if (dragged == null) return;
        event.consume();
        if (dragged.equals(curtain)) {
            moveAll(event);
        } else {
            moveKnob(event, dragged);
        }
    }

    private void moveAll(SelectEvent event) {
        if (null == curtainDragStartPoint) {
            curtainDragStartPoint = event.getPickPoint();
            knob1DragStartOffset = knob1.getScreenOffset().getX();
            knob2DragStartOffset = knob2.getScreenOffset().getX();
            return;
        }
        WorldWindowGLCanvas ww = (WorldWindowGLCanvas) event.getSource();
        double dx = ((double) event.getPickPoint().x - (double) curtainDragStartPoint.x)/ww.getWidth();
        translateX(knob1, knob1DragStartOffset + dx);
        translateX(knob2, knob2DragStartOffset + dx);
    }

    private void translateX(ScreenImage image, double dx) {
        Offset offset = image.getScreenOffset();
        offset.setX(dx);
        image.setScreenOffset(offset);
    }

    private void reconfigureCurtain(DrawContext drawContext) {
        Double x1 = knob1.getScreenOffset().getX();
        Double x2 = knob2.getScreenOffset().getX();
        curtain.setScreenOffset(Offset.fromFraction(x1/2 + x2/2, 0.5));
        int height = drawContext.getDrawableHeight() - knobImage.getHeight();
        x1 *= drawContext.getDrawableWidth();
        x2 *= drawContext.getDrawableWidth();
        double alpha = Math.atan((x2 - x1) / height);
        curtain.setRotation(180*alpha/Math.PI);
        curtain.setSize(Size.fromPixels(8, (int)(height / Math.cos(alpha))));
    }

    private void moveKnob(SelectEvent event, ScreenImage dragged) {
        WorldWindowGLCanvas ww = (WorldWindowGLCanvas) event.getSource();
        double x = event.getPickPoint().getX() / ww.getWidth();
        Offset moveTo = Offset.fromFraction(x, dragged.getScreenOffset().getY());
        dragged.setScreenOffset(moveTo);
    }

    private ScreenImage curtain() {
        ScreenImage image = image(curtainImage);
        image.setImageOffset(Offset.fromFraction(0.5, 0.5));
        image.setRotationOffset(Offset.fromFraction(0.5, 0.5));
        return image;
    }

    private ScreenImage knob(double y) {
        ScreenImage knob = image(knobImage);
        knob.setScreenOffset(Offset.fromFraction(0.5, y));
        knob.setImageOffset(Offset.fromFraction(0.5, y));
        return knob;
    }

    private ScreenImage image(BufferedImage image) {
        ScreenImage knob = new ScreenImage();
        knob.setImageSource(image);
        addRenderable(knob);
        return knob;
    }
}
