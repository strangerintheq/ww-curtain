package com.github.strangerintheq.worldwind.curtain;

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
import com.github.strangerintheq.worldwind.common.FileUtil;

public class CurtainControlLayer extends RenderableLayer implements SelectListener {

    private static final BufferedImage knobImage = FileUtil.readImage("images/circle.png");
    private static final BufferedImage curtainImage = FileUtil.readImage("images/curtain.png");
    private static Cursor resizeCursor = new Cursor(Cursor.E_RESIZE_CURSOR);
    private static Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
    private ScreenImage curtain = curtain();
    private ScreenImage knob1 = knob(1.);
    private ScreenImage knob2 = knob(0.);
    private Point curtainDragStartPoint;
    private Double knob1DragStartOffset;
    private Double knob2DragStartOffset;

    private boolean isHorizontal = true;

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
        ScreenImage image = null;
        if (event.getObjects() != null)
        for (PickedObject po : event.getObjects()) {
            if (po.getObject().equals(curtain)) image = curtain;
            if (po.getObject().equals(knob1)) image = knob1;
            if (po.getObject().equals(knob2)) image = knob2;
        }
        if (image == null) {
            WorldWindowGLCanvas ww = (WorldWindowGLCanvas) event.getSource();
            ww.setCursor(defaultCursor);
            return;
        }
        handleDrag(event, image);
        handleRollover(event);
    }

    private void handleRollover(SelectEvent event) {
        if (!event.getEventAction().equals(SelectEvent.ROLLOVER)) return;
        event.consume();
        WorldWindowGLCanvas ww = (WorldWindowGLCanvas) event.getSource();
        ww.setCursor(resizeCursor);
    }

    private void handleDrag(SelectEvent event, ScreenImage image) {
        if (!event.getEventAction().equals(SelectEvent.DRAG)) return;
        event.consume();
        if (image.equals(curtain)) {
            moveAll(event);
        } else {
            moveKnob(event, image);
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
        double x1 = knob1DragStartOffset + dx;
        double x2 = knob2DragStartOffset + dx;
        if (x1 < 0 || x2 < 0 || x1 > 1 || x2 > 1) return;
        translateX(knob1, x1);
        translateX(knob2, x2);
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
        int height = drawContext.getDrawableHeight();
        x1 *= drawContext.getDrawableWidth();
        x2 *= drawContext.getDrawableWidth();
        double alpha = Math.atan((x2 - x1) / height);
        curtain.setRotation(180*alpha/Math.PI);
        curtain.setSize(Size.fromPixels(8, (int)(height / Math.cos(alpha))-knobImage.getHeight() + 2));
    }

    private void moveKnob(SelectEvent event, ScreenImage dragged) {
        WorldWindowGLCanvas ww = (WorldWindowGLCanvas) event.getSource();
        double x = event.getPickPoint().getX() / ww.getWidth();
//        double y = 1 - event.getPickPoint().getY() / ww.getHeight();
        Offset offset = dragged.getScreenOffset();

        if (x < 0) x = 0;
        if (x > 1) x = 1;

        Offset moveTo = Offset.fromFraction(x, offset.getY());
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
//        knob.setImageOffset(Offset.fromFraction(0.5, y));
        return knob;
    }

    private ScreenImage image(BufferedImage image) {
        ScreenImage knob = new ScreenImage();
        knob.setImageSource(image);
        addRenderable(knob);
        return knob;
    }

    public float getAngle() {
        return curtain.getRotation().floatValue();
    }

    public float getCx() {
        return curtain.getScreenOffset().getX().floatValue();
    }
}
