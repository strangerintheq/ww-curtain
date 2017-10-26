package com.github.strangerintheq.worldwind.common;

import com.jogamp.common.util.IOUtil;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import java.net.URL;

import javax.imageio.ImageIO;

public class FileUtil {

    static final String N = "\n";

    public static BufferedImage readImage(String filename) {
        try {
            return ImageIO.read(getFile(filename));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String readText(String filename) {
        StringBuilder src = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(getFile(filename)));
            String line;
            while ((line = reader.readLine()) != null) {
                src.append(line).append(N);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtil.close(reader, false);
        }
        return src.toString();
    }

    private static File getFile(String filename){
        ClassLoader classLoader = FileUtil.class.getClassLoader();
        URL resource = classLoader.getResource(filename);
        return new File(resource.getFile());
    }
}
