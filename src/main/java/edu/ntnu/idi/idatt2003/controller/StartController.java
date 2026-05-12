package edu.ntnu.idi.idatt2003.controller;

import edu.ntnu.idi.idatt2003.navigation.Navigator;
import edu.ntnu.idi.idatt2003.navigation.Route;
import edu.ntnu.idi.idatt2003.view.StartView;

/**
 * Controller for the start menu.
 */
public class StartController {

    private final StartView view;
    private final Navigator navigator;

    /**
     * Creates a controller for the start menu.
     *
     * @param view the start menu view
     * @param navigator the navigator used to switch routes
     */
    public StartController(StartView view, Navigator navigator) {
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
        view.setOnNewGame(event -> {
            view.clearMessage();
            navigator.navigateTo(Route.NEW_GAME);
        });
        view.setOnContinueGame(event -> view.showMessage("Continue game is not available yet."));
        view.setOnLeaderboard(event -> view.showMessage("Leaderboard is not available yet."));
    }
}