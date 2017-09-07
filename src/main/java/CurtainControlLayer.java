import javax.imageio.ImageIO;

import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.ScreenImage;

public class CurtainControlLayer extends RenderableLayer {

    private final Object knobImage = FileUtil.readImage("knob.png");

    public CurtainControlLayer() {
        addRenderable(knob(25));
        addRenderable(knob(575));
    }

    ScreenImage knob(int y) {
        ScreenImage image = new ScreenImage();
        image.setImageSource(knobImage);
        return image;
    }
}
