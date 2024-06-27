package dev.makeev.coworking_service_app.in;

/**
 * An interface defining methods for user input in a console application.
 * */
public interface Input {
    /**
     * Reads and returns an integer input from the user within the specified range.
     *
     * @param minValue The minimum allowed integer value.
     * @param maxValue The maximum allowed integer value.
     * @return The integer input from the user.
     */
    int getInt(int minValue, int maxValue);

    /**
     * Reads and returns a string input from the user.
     *
     * @return The string input from the user.
     */
    String getString();

    /**
     * Reads and returns an integer input from the user within the specified length and range.
     *
     * @param maxLength The maximum length of the input.
     * @param min       The minimum allowed integer value.
     * @param max       The maximum allowed integer value.
     * @return The integer input from the user.
     */
    Integer getInteger(int maxLength, int min, int max);
}
