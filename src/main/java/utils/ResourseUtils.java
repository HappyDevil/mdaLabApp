package utils;

import javafx.scene.image.Image;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;

public class ResourseUtils {

    private static Image microwaveEmpty;
    private static Image microwaveReadyToCook;
    private static Image microwaveCooking;
    private static Image microwaveInterrupted;
    private static Image microwaveCookingComplete;

    private static FileInputStream getRecourse(String name) {
        FileInputStream is;
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            URL resource = classloader.getResource(name);
            File file = new File(resource.toURI());
            is = new FileInputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return is;
    }

    private static Image lazyInitImage(Image image, String name) {
        if (image == null) {
            FileInputStream is = ResourseUtils.getRecourse(name);
            if (is != null)
                return new Image(is);
        }
        return image;
    }

    public static Image getMicrowaveInterrupted() {
        microwaveInterrupted = lazyInitImage(microwaveInterrupted, "microwave_interrupted.jpg");
        return microwaveInterrupted;
    }

    public static Image getMicrowaveEmpty() {
        microwaveEmpty = lazyInitImage(microwaveEmpty, "microwave_empty.jpg");
        return microwaveEmpty;
    }

    public static Image getMicrowaveCooking() {
        microwaveCooking = lazyInitImage(microwaveCooking,"microwave.gif");
        return microwaveCooking;
    }

    public static Image getMicrowaveToReadyToCook() {
        microwaveReadyToCook = lazyInitImage(microwaveReadyToCook,"microwave_ready.jpg");
        return microwaveReadyToCook;
    }

    public static Image getMicrowaveToCookingComplete() {
        microwaveCookingComplete = lazyInitImage(microwaveCookingComplete,"microwave_complete.jpg");
        return microwaveCookingComplete;
    }
}
