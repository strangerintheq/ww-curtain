package com.github.strangerintheq.examples;

import com.github.strangerintheq.worldwind.common.ExampleBoilerplate;
import com.github.strangerintheq.worldwind.editor.EditorLayer;

public class EditorExample extends ExampleBoilerplate {

    EditorExample() {
        EditorLayer editorLayer = createDefaultInjector().getInstance(EditorLayer.class);
        layer(editorLayer);
        getInputHandler().addMouseListener(editorLayer.getAdapter());
        getInputHandler().addMouseMotionListener(editorLayer.getAdapter());
        addSelectListener(editorLayer.getAdapter());
    }

    public static void main(String[] args) {
        new EditorExample();
    }
}
