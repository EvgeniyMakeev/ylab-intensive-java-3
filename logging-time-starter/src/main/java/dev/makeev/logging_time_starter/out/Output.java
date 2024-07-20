package dev.makeev.logging_time_starter.out;

/**
 * An interface defining a generic output method for displaying information of type T.
 *
 * @param <T> The type of information to be output.
 */
public interface Output<T> {

    /**
     * Outputs information of type T.
     *
     * @param t The information to be output.
     */
    void output(T t);
}
