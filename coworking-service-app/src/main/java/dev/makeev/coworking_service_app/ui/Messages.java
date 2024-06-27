package dev.makeev.coworking_service_app.ui;

import dev.makeev.coworking_service_app.model.Space;
import dev.makeev.coworking_service_app.out.Output;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Represents various messages and menus displayed to the user.
 */
public class Messages {
    private final Output<String> console;

    public Messages(Output<String> console) {
        this.console = console;
    }

    public void print(String s) {
        console.output(s);
    }

    public void welcomeMessage() {
        console.output("""
                Welcome to Coworking-Service!
                Please enter your login:""");
    }


    public void passwordMessage() {
        console.output("Please enter password:");
    }

    public void notRegisteredMassage() {
        console.output("""
                You are not registered yet.
                1. Create a new account.
                2. Back.""");
    }

    public void newPasswordMessage() {
        console.output("To create an account, please create a password:");
    }

    public void greetingMessage(String loginOfCurrentUser) {
        console.output("\nWelcome, " + loginOfCurrentUser + "!");
    }

    public void showUserMenu() {
        console.output("""
                ================ USER MENU ================
                1. Available spaces.
                2. Available slots for booking.
                3. Booking a workplace or conference room.
                4. Show my bookings.
                5. Cancel booking.
                6. Log out.

                0. Exit""");
    }

    public void showAdminMenu() {
        console.output("""
                ================ ADMIN MENU ================
                1. Space management.
                2. View bookings.
                3. Log out.

                0. Exit""");
    }

    public void setYearMessage() {
        console.output("Please enter year (4 digits):");
    }
    public void setMonthMessage() {
        console.output("Please enter month (2 digits):");
    }
    public void setDayMessage() {
        console.output("Please enter day (2 digits):");
    }

    public void printSpaces(List<String> namesOfSpaces) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Available spaces:");
        int numberOfSpace = 1;
        for (String nameOfSpace : namesOfSpaces) {
            stringBuilder.append("\n")
                    .append(numberOfSpace)
                    .append(". ")
                    .append(nameOfSpace);
            numberOfSpace++;
        }
        console.output(stringBuilder.toString());
    }

    public void printAvailableSlotsForBooking(Space space) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(space.name())
                .append(" - available slots for booking:");
        long freeSlot = 0L;
        space.bookingSlots().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEachOrdered(dateEntry -> {
                    stringBuilder.append("\n")
                            .append(dateEntry.getKey())
                            .append("\n");

                    dateEntry.getValue().keySet().stream()
                            .filter(integer -> dateEntry.getValue().get(integer) == freeSlot)
                            .sorted(Comparator.comparingInt(Integer::intValue))
                            .forEachOrdered(hour -> stringBuilder.append(timeFormatter(hour))
                                    .append(" | "));
                });

        console.output(stringBuilder.toString());
    }

    private String timeFormatter(int hour) {
        return String.format("%02d:00-%02d:00", hour, hour + 1);
    }

    public void successfulBooking(String nameOfSpace, LocalDate dateBookingFrom, LocalDate dateBookingTo,
                                  int hourBookingFrom, int hourBookingTo) {
        console.output(String.format("You have successfully booked %s from %02d:00 - %s to %02d:00 - %s",
                nameOfSpace, hourBookingFrom, dateBookingFrom, hourBookingTo, dateBookingTo));
    }

    public void printBookings(List<String> bookings) {
        StringBuilder stringBuilder = new StringBuilder();
        bookings.forEach(stringBuilder::append);
        console.output(stringBuilder.toString());
    }

    public void deleteBookingMessage() {
        console.output("""
                        Enter the number of booking you want to cancel.
                        To cancel deleting, press 0.""");
    }

    public void chooseSpaceMessage() {
        console.output("""
                        Enter the number of space you want to cancel.
                        To cancel deleting, press 0.""");
    }

    public void selectionOfBookingSorting() {
        console.output("""
                Select sorting:
                1. Sorted by user.
                2. Sorted by date.
                3. Sorted by space.""");
    }

    public void selectionActionWithSpaces() {
        console.output("""
                
                Choose an action:
                1. Available slots for booking.
                2. Add a new space.
                3. Delete space.
                0. Back.""");
    }

    public void deleteSpaceMessage() {
        console.output("""
                        Enter the space number you want to cancel.
                        To cancel deleting, press 0.""");
    }

    public void errorWhenEnteringHours() {
        console.output("The endingBookingHour time of the working day cannot be less than the beginning of the working day.");
    }
}