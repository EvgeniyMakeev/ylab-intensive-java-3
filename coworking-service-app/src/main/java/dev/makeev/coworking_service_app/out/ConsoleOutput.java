package dev.makeev.coworking_service_app.out;

/**
 * An implementation of the {@link Output} interface for displaying String information to the console.
 */
public class ConsoleOutput implements Output<String> {

    @Override
    public void output(String s){
        System.out.println(s);
    }
}