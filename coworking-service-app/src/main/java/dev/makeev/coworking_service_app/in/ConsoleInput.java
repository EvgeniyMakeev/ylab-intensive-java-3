package dev.makeev.coworking_service_app.in;

import java.util.Scanner;

/**
 * Implementation of the {@link Input} interface for handling user input in a console application.
 */
public class ConsoleInput implements Input {

    /**
     * Service for input stream.
     */
    private final Scanner input = new Scanner(System.in);

    @Override
    public int getInt(int minValue, int maxValue) {
        String optionString;

        int option = -1;

        boolean isValid;
        do {
            optionString = input.nextLine();

            isValid = optionString.matches("[0-9]+")
                    && Integer.parseInt(optionString) <= maxValue
                    && Integer.parseInt(optionString) >= minValue;

            if (isValid) {
                option = Integer.parseInt(optionString);
            } else {
                System.out.printf("Enter only digits %d - %d\n", minValue, maxValue);
            }
        } while (!isValid || optionString.isEmpty());
        return option;
    }

    @Override
    public String getString() {
        return input.nextLine();
    }

    @Override
    public Double getDouble() {
        String str = "";
        double result = -1;
        boolean scan = true;
        while (scan) {
            str = input.nextLine();
            if (str.matches("^\\d+(\\.\\d+)?$") && !str.isEmpty()) {
                result = Double.parseDouble(str);
            } else {
                System.out.println("You can only enter a positive number");
            }
            if (result >= 0) {
                scan = false;
            }
        }
        return result;
    }

    @Override
    public Integer getInteger(int maxLength, int minValue, int maxValue) {
        String str = "";
        int result = -1;
        boolean scan = true;
        while (scan) {
            str = input.nextLine();
            if (str.matches("[0-9]+") && str.length() <= maxLength) {
                result = Integer.parseInt(str);
                if (result >= minValue && result <= maxValue) {
                    scan = false;
                } else {
                    System.out.printf("Enter only digits %d - %d\n", minValue, maxValue);
                }
            } else {
                System.out.printf("The number must be no longer than %d characters\n", maxLength);
            }
        }
        return result;
    }
}