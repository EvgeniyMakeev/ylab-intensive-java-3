package dev.makeev.coworking_service_app;

import dev.makeev.coworking_service_app.ui.AppUi;

/**
 * Entry point for the coworking service application.
 */
public class App {

    /**
     * Main method to start the application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        AppUi application = new AppUi();
        application.initDb();
        application.run();
    }
}
