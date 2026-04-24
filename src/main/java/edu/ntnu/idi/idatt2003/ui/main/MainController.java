package edu.ntnu.idi.idatt2003.ui.main;

import edu.ntnu.idi.idatt2003.ui.ViewNavigator;
import edu.ntnu.idi.idatt2003.ui.topbar.TopBarController;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * Root controller of the application shell.
 *
 * <p>Responsible for:
 *
 * <ul>
 *   <li>Managing global layout (TopBar, Sidebar, ContentArea)
 *   <li>Handling sidebar animation
 * </ul>
 */
public class MainController {

  @FXML private StackPane contentArea;
  @FXML private Pane sidebar;
  @FXML private Pane overlay;

  @FXML private TopBarController topBarController;

  private boolean sidebarOpen = false;

  /** Initializes the main application shell. */
  @FXML
  public void initialize() {

    ViewNavigator.init(contentArea);

    topBarController.setOnMenuToggle(this::toggleSidebar);

    // default view
    ViewNavigator.get().show("/ui/stock/StockView.fxml");
  }

  /** Toggles sidebar visibility with animation. */
  private void toggleSidebar() {

    TranslateTransition tt = new TranslateTransition(Duration.millis(200), sidebar);

    if (!sidebarOpen) {

      sidebar.setVisible(true);
      overlay.setVisible(true);

      tt.setFromX(-250);
      tt.setToX(0);

    } else {

      overlay.setVisible(false);

      tt.setFromX(0);
      tt.setToX(-250);

      tt.setOnFinished(e -> sidebar.setVisible(false));
    }

    sidebarOpen = !sidebarOpen;
    tt.play();
  }
}
