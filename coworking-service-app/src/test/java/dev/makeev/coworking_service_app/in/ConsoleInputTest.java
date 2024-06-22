package dev.makeev.coworking_service_app.in;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the {@link ConsoleInput} class.
 */
@DisplayName("ConsoleInput Test")
public class ConsoleInputTest {

    @Test
    @DisplayName("ConsoleInput test: Get int from console - Should get integer not higher max value")
    void getInt_validInput_returnsInteger() {
        String inputString = "5\n";
        InputStream in = new ByteArrayInputStream(inputString.getBytes());
        System.setIn(in);
        Input input = new ConsoleInput();

        int result = input.getInt(0, 9);

        assertThat(result).isEqualTo(5);
    }

    @Test
    @DisplayName("ConsoleInput test: Get string from console - Should get string")
    void getString_validInput_returnsString() {
        String inputString = "Hello\n";
        InputStream in = new ByteArrayInputStream(inputString.getBytes());
        System.setIn(in);
        Input input = new ConsoleInput();

        String result = input.getString();

        assertThat(result).isEqualTo("Hello");
    }

    @Test
    @DisplayName("ConsoleInput test: Get double from console - Should get double")
    void getDouble_validInput_returnsDouble() {
        String inputString = "3.14\n";
        InputStream in = new ByteArrayInputStream(inputString.getBytes());
        System.setIn(in);
        Input input = new ConsoleInput();

        double result = input.getDouble();

        assertThat(result).isEqualTo(3.14);
    }

    @Test
    @DisplayName("ConsoleInput test: Get a integer with a given minimum, maximum value and a certain number of characters" +
            " - Should get integer")
    void getInteger_validInput_returnsInteger() {
        String inputString = "12345\n";
        InputStream in = new ByteArrayInputStream(inputString.getBytes());
        System.setIn(in);
        Input input = new ConsoleInput();

        int result = input.getInteger(5, 1000, 99999);

        assertThat(result).isEqualTo(12345);
    }
}