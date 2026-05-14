package edu.ntnu.idi.idatt2003;

import edu.ntnu.idi.idatt2003.controller.NewGameController;
import edu.ntnu.idi.idatt2003.controller.PlaceholderController;
import edu.ntnu.idi.idatt2003.controller.StartController;
import edu.ntnu.idi.idatt2003.io.ExchangeCsvHandler;
import edu.ntnu.idi.idatt2003.navigation.Navigator;
import edu.ntnu.idi.idatt2003.navigation.Route;
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

    /**
     * Creates a bootstrapper for the given scene.
     *
     * @param scene the scene used by the application
     */
    public AppBootstrap(Scene scene) {
        navigator = new Navigator(scene);
    }

    /**
     * Registers routes, wires controllers, and shows the initial route.
     */
    public void start() {
        StartView startView = new StartView();
        PlaceholderView placeholderView = new PlaceholderView();
        ExchangeCsvHandler csvHandler = new ExchangeCsvHandler();

        navigator.register(Route.START, startView::getRoot);
        navigator.register(Route.NEW_GAME, () -> createNewGameRoot(csvHandler));
        navigator.register(Route.PLACEHOLDER, placeholderView::getRoot);

        new StartController(startView, navigator);
        new PlaceholderController(placeholderView, navigator);

        navigator.navigateTo(Route.START);
    }

    private Parent createNewGameRoot(ExchangeCsvHandler csvHandler) {
        NewGameView newGameView = new NewGameView();
        new NewGameController(newGameView, navigator, csvHandler);
        return newGameView.getRoot();
    }
}
