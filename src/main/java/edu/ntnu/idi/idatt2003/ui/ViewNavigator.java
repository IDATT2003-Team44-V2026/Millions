package edu.ntnu.idi.idatt2003.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

/**
 * Global navigation controller for the entire application.
 *
 * <p>This is the single entry point for switching views. It avoids passing navigation objects
 * through controllers.
 */
public final class ViewNavigator {

  private static ViewNavigator instance;

  private final StackPane contentArea;

  /**
   * Creates the navigator (called once from MainController).
   *
   * @param contentArea the UI container for views
   */
  private ViewNavigator(StackPane contentArea) {
    this.contentArea = contentArea;
  }

  /**
   * Initializes the global navigator.
   *
   * @param contentArea the main content container
   */
  public static void init(StackPane contentArea) {
    instance = new ViewNavigator(contentArea);
  }

  /**
   * Returns global navigator instance.
   *
   * @return navigator
   */
  public static ViewNavigator get() {
    if (instance == null) {
      throw new IllegalStateException("AppNavigator not initialized");
    }
    return instance;
  }

  /**
   * Switches the main view.
   *
   * @param fxml path to FXML file
   */
  public void show(String fxml) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));

      Parent view = loader.load();

      contentArea.getChildren().setAll(view);

    } catch (Exception e) {
      throw new RuntimeException("Failed to load view: " + fxml, e);
    }
  }
}
