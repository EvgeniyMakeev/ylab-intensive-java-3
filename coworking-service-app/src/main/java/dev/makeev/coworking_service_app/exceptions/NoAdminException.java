package dev.makeev.coworking_service_app.exceptions;

public class NoAdminException extends RuntimeException {

    @Override
    public String getMessage() {
        return "No administrator rights.";
    }
}

