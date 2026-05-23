package edu.ntnu.idi.idatt2003;

import edu.ntnu.idi.idatt2003.controller.NewGameController;
import edu.ntnu.idi.idatt2003.controller.PlaceholderController;
import edu.ntnu.idi.idatt2003.controller.StartController;
import edu.ntnu.idi.idatt2003.io.ExchangeCsvHandler;
import edu.ntnu.idi.idatt2003.navigation.Navigator;
import edu.ntnu.idi.idatt2003.navigation.Route;
import edu.ntnu.idi.idatt2003.service.GameSession;
import edu.ntnu.idi.idatt2003.service.GameSessionFactory;
import edu.ntnu.idi.idatt2003.view.NewGameView;
import edu.ntnu.idi.idatt2003.view.PlaceholderView;
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

    /**
     * Registers routes, wires controllers, and shows the initial route.
     */
    public void start() {
        StartView startView = new StartView();
        PlaceholderView placeholderView = new PlaceholderView();
        ExchangeCsvHandler csvHandler = new ExchangeCsvHandler();
        GameSessionFactory gameSessionFactory = new GameSessionFactory();

        navigator.register(Route.START, startView::getRoot);
        navigator.register(Route.NEW_GAME, () -> createNewGameRoot(csvHandler, gameSessionFactory));
        navigator.register(Route.PLACEHOLDER, placeholderView::getRoot);
        
        new StartController(startView, navigator);
        new PlaceholderController(placeholderView, navigator);

        navigator.navigateTo(Route.START);
    }

    private Parent createNewGameRoot(
        ExchangeCsvHandler csvHandler,
        GameSessionFactory gameSessionFactory
    ) {
        NewGameView newGameView = new NewGameView();
        new NewGameController(newGameView, navigator, csvHandler, gameSessionFactory, this::setActiveSession);
        return newGameView.getRoot();
    }

    private void setActiveSession(GameSession gameSession) {
        activeSession = gameSession;
    }
}
