package worldwind.editor;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.WorldWindowGLAutoDrawable;
import worldwind.common.ExampleBoilerplate;
import worldwind.common.WwMouseAdapter;

/**
 * Created by Balashev Konstantin @ 26.10.2017.
 */
public class Example extends ExampleBoilerplate {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(WorldWindow.class).to(WorldWindowGLAutoDrawable.class);
            }
        });

        EditorLayer editorLayer = injector.getInstance(EditorLayer.class);
        Example example = new Example();
        example.layer(editorLayer);
        WwMouseAdapter mouseAdapter = editorLayer.getAdapter();
        example.getInputHandler().addMouseListener(mouseAdapter);
        example.getInputHandler().addMouseMotionListener(mouseAdapter);
        example.addSelectListener(mouseAdapter);
    }
}
