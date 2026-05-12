package edu.ntnu.idi.idatt2003.navigation;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * Handles route-based navigation by replacing the root node of the application scene.
 */
public class Navigator {

    private final Scene scene;
    private final Map<Route, Supplier<Parent>> routes = new EnumMap<>(Route.class);

    /**
     * Creates a navigator for the given scene.
     *
     * @param scene the scene whose root should be replaced when navigating
     */
    public Navigator(Scene scene) {
        if (scene == null) {
            throw new IllegalArgumentException("Scene cannot be null");
        }
        this.scene = scene;
    }

    /**
     * Registers the view factory used for a route.
     *
     * @param route the route to register
     * @param rootFactory factory returning the root node for the route
     */
    public void register(Route route, Supplier<Parent> rootFactory) {
        if (route == null) {
            throw new IllegalArgumentException("Route cannot be null");
        }
        if (rootFactory == null) {
            throw new IllegalArgumentException("Root factory cannot be null");
        }
        routes.put(route, rootFactory);
    }

    /**
     * Navigates to the given route.
     *
     * @param route the route to show
     */
    public void navigateTo(Route route) {
        Supplier<Parent> rootFactory = routes.get(route);
        if (rootFactory == null) {
            throw new IllegalArgumentException("No view registered for route: " + route);
        }
        scene.setRoot(rootFactory.get());
    }
}
