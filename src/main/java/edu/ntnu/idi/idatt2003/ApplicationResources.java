package edu.ntnu.idi.idatt2003;

import java.io.InputStream;
import java.net.URL;
import java.util.logging.Logger;
import javafx.scene.Scene;
import javafx.scene.text.Font;

/**
 * Loads shared JavaFX resources used by the application.
 */
public final class ApplicationResources {

    private static final Logger LOGGER = Logger.getLogger(ApplicationResources.class.getName());
    private static final String[] FONT_PATHS = {
        "/fonts/Inter_18pt-Regular.ttf",
        "/fonts/Inter_18pt-Medium.ttf",
        "/fonts/Inter_18pt-SemiBold.ttf",
        "/fonts/Inter_18pt-Bold.ttf"
    };
    private static final String APPLICATION_STYLESHEET = "/css/application.css";

    private ApplicationResources() {
        // Utility class; do not instantiate.
    }

    /**
     * Loads all application fonts.
     */
    public static void loadFonts() {
        for (String fontPath : FONT_PATHS) {
            loadFont(fontPath);
        }
    }

    /**
     * Applies the shared application stylesheet to the given scene.
     *
     * @param scene the scene to style
     */
    public static void applyStylesheets(Scene scene) {
        if (scene == null) {
            throw new IllegalArgumentException("Scene cannot be null");
        }

        URL stylesheet = ApplicationResources.class.getResource(APPLICATION_STYLESHEET);
        if (stylesheet == null) {
            LOGGER.warning(() -> "Could not find stylesheet: " + APPLICATION_STYLESHEET);
            return;
        }

        scene.getStylesheets().add(stylesheet.toExternalForm());
    }

    private static void loadFont(String path) {
        InputStream stream = ApplicationResources.class.getResourceAsStream(path);
        if (stream == null) {
            LOGGER.warning(() -> "Could not find font: " + path);
            return;
        }

        Font font = Font.loadFont(stream, 14);
        if (font == null) {
            LOGGER.warning(() -> "Could not load font: " + path);
        }
    }
}
