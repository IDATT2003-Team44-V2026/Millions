package edu.ntnu.idi.idatt2003;

import edu.ntnu.idi.idatt2003.controller.GameController;
import edu.ntnu.idi.idatt2003.controller.NewGameController;
import edu.ntnu.idi.idatt2003.controller.StartController;
import edu.ntnu.idi.idatt2003.io.ExchangeCsvHandler;
import edu.ntnu.idi.idatt2003.navigation.Navigator;
import edu.ntnu.idi.idatt2003.navigation.Route;
import edu.ntnu.idi.idatt2003.service.GameSession;
import edu.ntnu.idi.idatt2003.service.GameSessionFactory;
import edu.ntnu.idi.idatt2003.view.GameView;
import edu.ntnu.idi.idatt2003.view.NewGameView;
import edu.ntnu.idi.idatt2003.view.StartView;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * Wires together views, controllers, and navigation for the application.
 */
public class AppBootstrap {

  private final Navigator navigator;
  private GameSession activeSession;

  /**
   * Creates a bootstrapper for the given scene.
   *
   * @param scene the scene used by the application
   */
  public AppBootstrap(Scene scene) {
    navigator = new Navigator(scene);
  }

  /**
   * Returns the active game session.
   *
   * @return the active game session, or {@code null} if no session has been created
   */
  public GameSession getActiveSession() {
    return activeSession;
  }

  private void setActiveSession(GameSession gameSession) {
    activeSession = gameSession;
  }

  /**
   * Registers routes, wires controllers, and shows the initial route.
   */
  public void start() {
    StartView startView = new StartView();
    ExchangeCsvHandler csvHandler = new ExchangeCsvHandler();
    GameSessionFactory gameSessionFactory = new GameSessionFactory();

    navigator.register(Route.START, startView::getRoot);
    navigator.register(Route.NEW_GAME, () -> createNewGameRoot(csvHandler, gameSessionFactory));
    navigator.register(Route.GAME, this::createGameRoot);

    new StartController(startView, navigator);

    navigator.navigateTo(Route.START);
  }

  private Parent createNewGameRoot(
      ExchangeCsvHandler csvHandler,
      GameSessionFactory gameSessionFactory
  ) {
    NewGameView newGameView = new NewGameView();
    new NewGameController(newGameView, navigator, csvHandler, gameSessionFactory,
        this::setActiveSession);
    return newGameView.getRoot();
  }

  private Parent createGameRoot() {
    if (activeSession == null) {
      throw new IllegalStateException("Cannot open game route before a session is created");
    }

    GameView gameView = new GameView();
    new GameController(gameView, activeSession, navigator);
    return gameView.getRoot();
  }
}
