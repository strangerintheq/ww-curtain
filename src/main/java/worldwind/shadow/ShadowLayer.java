package worldwind.shadow;

import java.awt.Point;

import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.terrain.SectorGeometry;

public class ShadowLayer extends RenderableLayer {

    private final Layer layer;

    public ShadowLayer(Layer layer) {
        if (layer == null) throw new IllegalArgumentException();
        this.layer = layer;
    }

    private void renderShadowMap(DrawContext drawContext) {
        for (SectorGeometry sectorGeometry : drawContext.getSurfaceGeometry()) {
            sectorGeometry.renderBoundingVolume(drawContext);
        }
        System.out.println("sectorGeometry = " + drawContext.getSurfaceGeometry().size());

    }


    @Override
    public void preRender(DrawContext drawContext) {
        layer.preRender(drawContext);
    }

    @Override
    public void render(DrawContext drawContext) {
        renderShadowMap(drawContext);
        layer.render(drawContext);
    }


    @Override
    public void pick(DrawContext drawContext, Point point) {
        layer.pick(drawContext, point);
    }
}
