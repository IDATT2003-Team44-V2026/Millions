package edu.ntnu.idi.idatt2003.controller;

import edu.ntnu.idi.idatt2003.navigation.Navigator;
import edu.ntnu.idi.idatt2003.navigation.Route;
import edu.ntnu.idi.idatt2003.view.PlaceholderView;

/**
 * Controller for the temporary placeholder screen.
 */
public class PlaceholderController {

    private final PlaceholderView view;
    private final Navigator navigator;

    /**
     * Creates a controller for the placeholder screen.
     *
     * @param view the placeholder view
     * @param navigator the navigator used to switch routes
     */
    public PlaceholderController(PlaceholderView view, Navigator navigator) {
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
        view.setOnBack(event -> navigator.navigateTo(Route.START));
    }
}
