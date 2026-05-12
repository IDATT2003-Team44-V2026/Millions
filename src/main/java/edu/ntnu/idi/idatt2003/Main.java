package edu.ntnu.idi.idatt2003;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    private static final double DEFAULT_WIDTH = 960;
    private static final double DEFAULT_HEIGHT = 600;

    @Override
    public void start(Stage primaryStage) {
        ApplicationResources.loadFonts();

        Scene scene = new Scene(new StackPane(), DEFAULT_WIDTH, DEFAULT_HEIGHT);
        ApplicationResources.applyStylesheets(scene);

        AppBootstrap appBootstrap = new AppBootstrap(scene);
        appBootstrap.start();

        primaryStage.setTitle("Millions");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
