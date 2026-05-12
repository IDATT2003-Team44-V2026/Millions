package edu.ntnu.idi.idatt2003.controller;

import edu.ntnu.idi.idatt2003.navigation.Navigator;
import edu.ntnu.idi.idatt2003.navigation.Route;
import edu.ntnu.idi.idatt2003.view.NewGameView;

/**
 * Controller for the new game setup screen.
 */
public class NewGameController {

    private final NewGameView view;
    private final Navigator navigator;

    /**
     * Creates a controller for the new game setup screen.
     *
     * @param view the new game setup view
     * @param navigator the navigator used to switch routes
     */
    public NewGameController(NewGameView view, Navigator navigator) {
        if (view == null) {
            throw new IllegalArgumentException("View cannot be null");
        }
        if (navigator == null) {
            throw new IllegalArgumentException("Navigator cannot be null");
        }

        this.view = view;
        this.navigator = navigator;
        initializeHandlers();
    }

    private void initializeHandlers() {
        view.setOnStart(event -> navigator.navigateTo(Route.PLACEHOLDER));
        view.setOnBack(event -> {
            view.clearError();
            navigator.navigateTo(Route.START);
        });
    }
}
