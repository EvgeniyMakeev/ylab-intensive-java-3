package dev.makeev.coworking_service_app.out.implementation;

import dev.makeev.coworking_service_app.out.Output;

/**
 * An implementation of the {@link Output} interface for displaying String information to the console.
 */
public class ConsoleOutput implements Output<String> {

    /**
     * {@inheritdoc}
     */
    @Override
    public void output(String s){
        System.out.println(s);
    }
}