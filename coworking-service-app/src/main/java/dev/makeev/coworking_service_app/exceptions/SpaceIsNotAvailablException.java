package dev.makeev.coworking_service_app.exceptions;

public class SpaceIsNotAvailablException extends Exception {

    @Override
    public String getMessage() {
        return """
                The space is not available for booking on these dates and times.
                "Please choose other dates and times.""";
    }
}