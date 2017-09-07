import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import java.net.URL;

import javax.imageio.ImageIO;

public class FileUtil {

    public static Object readImage(String filename) {
        try {
            return ImageIO.read(getFile(filename));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String readText(String filename) {
        String src = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(getFile(filename)));
            String line;
            while ((line = reader.readLine()) != null) {
                src += line + "\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return src;
    }

    static File getFile(String filename){
        ClassLoader classLoader = FileUtil.class.getClassLoader();
        URL resource = classLoader.getResource(filename);
        return new File(resource.getFile());
    }
}
