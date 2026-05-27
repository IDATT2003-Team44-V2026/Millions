package edu.ntnu.idi.idatt2003;

import javafx.application.Application;
import javafx.css.PseudoClass;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * JavaFX application entry point for the Millions stock-trading game.
 */
public class Main extends Application {

  private static final double DEFAULT_WIDTH = 960;
  private static final double DEFAULT_HEIGHT = 600;
  private static final PseudoClass KEYBOARD_NAVIGATION =
      PseudoClass.getPseudoClass("keyboard-navigation");
  private boolean keyboardNavigationActive;

  /**
   * Launches the JavaFX application.
   *
   * @param args command-line arguments forwarded to the JavaFX launcher
   */
  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    ApplicationResources.loadFonts();

    Scene scene = new Scene(new StackPane(), DEFAULT_WIDTH, DEFAULT_HEIGHT);
    ApplicationResources.applyStylesheets(scene);
    configureFocusVisibility(scene);

    AppBootstrap appBootstrap = new AppBootstrap(scene);
    appBootstrap.start();

    primaryStage.setTitle("Millions");
    primaryStage.setScene(scene);
    primaryStage.setMaximized(true);
    primaryStage.show();
  }

  private void configureFocusVisibility(Scene scene) {
    scene.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
      keyboardNavigationActive = false;
      updateKeyboardNavigationClass(scene.getRoot());
    });

    scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
      if (isKeyboardNavigationKey(event.getCode())) {
        keyboardNavigationActive = true;
        updateKeyboardNavigationClass(scene.getRoot());
      }
    });

    scene.rootProperty().addListener((observable, oldRoot, newRoot) ->
        updateKeyboardNavigationClass(newRoot)
    );
  }

  private boolean isKeyboardNavigationKey(KeyCode keyCode) {
    return keyCode == KeyCode.TAB;
  }

  private void updateKeyboardNavigationClass(Parent root) {
    if (root == null) {
      return;
    }

    root.pseudoClassStateChanged(KEYBOARD_NAVIGATION, keyboardNavigationActive);
  }
}
