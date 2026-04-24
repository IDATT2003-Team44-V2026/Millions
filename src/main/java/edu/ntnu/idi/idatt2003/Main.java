package edu.ntnu.idi.idatt2003;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Entry point for the Stock application.
 *
 * <p>This class bootstraps the JavaFX runtime and loads the primary user interface. The application
 * uses a single-stage architecture where all views are dynamically rendered inside a shared root
 * layout.
 */
public class Main extends Application {

  /**
   * Starts the JavaFX application and initializes the primary stage.
   *
   * @param stage the main application window provided by the JavaFX runtime
   * @throws Exception if the FXML layout cannot be loaded
   */
  @Override
  public void start(Stage stage) throws Exception {

    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/main/MainView.fxml"));

    BorderPane root = loader.load();

    Scene scene = new Scene(root, 900, 600);

    stage.setTitle("Stock Application");
    stage.setScene(scene);
    stage.show();
  }

  /**
   * Launches the JavaFX application.
   *
   * @param args command-line arguments passed to the application
   */
  public static void main(String[] args) {
    launch();
  }
}
