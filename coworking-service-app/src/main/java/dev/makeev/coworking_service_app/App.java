package dev.makeev.coworking_service_app;

import dev.makeev.coworking_service_app.exceptions.SpaceIsNotAvailablException;
import dev.makeev.coworking_service_app.ui.AppUi;

public class App {
    public static void main(String[] args) throws SpaceIsNotAvailablException {
        new AppUi().run();
    }
}
