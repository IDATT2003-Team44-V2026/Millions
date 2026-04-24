package edu.ntnu.idi.idatt2003;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Entry point for the Millions stock trading application.
 *
 * <p>Launches the JavaFX runtime and shows the primary stage maximized
 * to fill the available screen (normal window, not exclusive fullscreen).
 * Further UI will be built out under del 3 (MVC, views, and controllers).</p>
 */
public class Main extends Application {

    private static final double DEFAULT_WIDTH = 960;
    private static final double DEFAULT_HEIGHT = 600;

    @Override
    public void start(Stage primaryStage) {
        Label placeholder = new Label("Millions");
        StackPane root = new StackPane(placeholder);
        Scene scene = new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        primaryStage.setTitle("Millions");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    /**
     * Starts the JavaFX application.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
