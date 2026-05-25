package edu.ntnu.idi.idatt2003.controller;

import edu.ntnu.idi.idatt2003.io.GameRepository;
import edu.ntnu.idi.idatt2003.io.GameSave;
import edu.ntnu.idi.idatt2003.navigation.Navigator;
import edu.ntnu.idi.idatt2003.navigation.Route;
import edu.ntnu.idi.idatt2003.service.GameSession;
import edu.ntnu.idi.idatt2003.view.LoadGameView;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

/**
 * Controller for the load-game screen.
 */
public class LoadGameController {

  private final LoadGameView view;
  private final Navigator navigator;
  private final Consumer<GameSession> sessionConsumer;

  /**
   * Creates a controller for the load-game screen and immediately populates the save list.
   *
   * @param view            the load-game view; must not be {@code null}
   * @param navigator       the navigator; must not be {@code null}
   * @param sessionConsumer receives the reconstructed session when the user loads a save
   */
  public LoadGameController(
      LoadGameView view,
      Navigator navigator,
      Consumer<GameSession> sessionConsumer
  ) {
    if (view == null) {
      throw new IllegalArgumentException("View cannot be null");
    }
    if (navigator == null) {
      throw new IllegalArgumentException("Navigator cannot be null");
    }
    if (sessionConsumer == null) {
      throw new IllegalArgumentException("Session consumer cannot be null");
    }

    this.view = view;
    this.navigator = navigator;
    this.sessionConsumer = sessionConsumer;

    initializeHandlers();
    loadSaves();
  }

  private void initializeHandlers() {
    view.setOnBack(event -> navigator.navigateTo(Route.START));
    view.setOnLoad(event -> loadSelected());
    view.setOnDelete(save -> {
      try {
        GameRepository.delete(save);
        loadSaves();
      } catch (java.io.IOException e) {
        view.showMessage("Could not delete save: " + e.getMessage());
      }
    });
  }

  private void loadSaves() {
    try {
      List<GameSave> saves = GameRepository.listSaves();
      if (saves.isEmpty()) {
        view.showMessage("No saved games found.");
      } else {
        view.setSaves(saves);
      }
    } catch (IOException e) {
      view.showMessage("Could not read saves: " + e.getMessage());
    }
  }

  private void loadSelected() {
    GameSave selected = view.getSelectedSave();
    if (selected == null) {
      return;
    }
    try {
      GameSession session = GameRepository.load(selected);
      sessionConsumer.accept(session);
      navigator.navigateTo(Route.GAME);
    } catch (Exception e) {
      view.showMessage("Failed to load save: " + e.getMessage());
    }
  }
}
