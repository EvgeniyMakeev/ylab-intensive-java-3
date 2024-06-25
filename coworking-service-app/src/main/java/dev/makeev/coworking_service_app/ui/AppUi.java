package dev.makeev.coworking_service_app.ui;

import dev.makeev.coworking_service_app.dao.SpaceDAO;
import dev.makeev.coworking_service_app.dao.UserDAO;
import dev.makeev.coworking_service_app.dao.impl.BookingDAOInMemory;
import dev.makeev.coworking_service_app.dao.impl.SpaceDAOInMemory;
import dev.makeev.coworking_service_app.dao.impl.UserDAOInMemory;
import dev.makeev.coworking_service_app.exceptions.SpaceIsNotAvailableException;
import dev.makeev.coworking_service_app.in.impl.ConsoleInput;
import dev.makeev.coworking_service_app.in.Input;
import dev.makeev.coworking_service_app.model.Space;
import dev.makeev.coworking_service_app.out.impl.ConsoleOutput;
import dev.makeev.coworking_service_app.service.BookingService;
import dev.makeev.coworking_service_app.service.SpaceService;
import dev.makeev.coworking_service_app.service.UserService;
import dev.makeev.coworking_service_app.util.InitDb;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

/**
 * The main user interface class for the coworking service application.
 */
public final class AppUi {

    private final UserDAO userDAO = new UserDAOInMemory();
    private final UserService userService = new UserService(userDAO);
    private final SpaceDAO spaceDAO = new SpaceDAOInMemory();
    private final BookingService bookingService = new BookingService(new BookingDAOInMemory(), spaceDAO);
    private final SpaceService spaceService = new SpaceService(spaceDAO, bookingService);
    private final InitDb initDb = new InitDb(userDAO, spaceService, bookingService);

    private final Input input = new ConsoleInput();
    private final Messages console = new Messages(new ConsoleOutput());
    private String loginOfCurrentUser = "login failed";

    private Optional<Space> currentSpace = Optional.empty();

    /**
     * Runs the application user interface.
     *
     * @throws SpaceIsNotAvailableException if the space is not available for booking
     */
    public void run() throws SpaceIsNotAvailableException {
        initDb.initDb();

        while (true) {
            loginMenu();
            while (!loginOfCurrentUser.equals("login failed")) {
                if (userService.isAdmin(loginOfCurrentUser)) {
                    adminMenu();
                } else {
                    userMenu();
                }
            }
        }
    }

    /**
     * Displays the login menu and handles user login and registration.
     */
    private void loginMenu() {
        console.welcomeMessage();
        while (loginOfCurrentUser.equals("login failed")) {
            String login = input.getString();
            if (userService.existByLogin(login)) {
                console.passwordMessage();
                if (userService.checkCredentials(login, input.getString())) {
                    console.print("Access is allowed!");
                    loginOfCurrentUser = login;
                } else {
                    console.print("Wrong password!");
                    break;
                }
            } else {
                console.notRegisteredMassage();
                if (input.getInt(1, 2) == 1) {
                    console.newPasswordMessage();
                    userService.addUser(login, input.getString());
                    console.print("Account was created!");
                    loginOfCurrentUser = login;
                } else {
                    break;
                }
            }
        }
    }

    /**
     * Displays the user menu and handles user actions.
     */
    private void userMenu() {
        console.greetingMessage(loginOfCurrentUser);
        console.showUserMenu();
        switch (input.getInt(0, 6)) {
            case 1 -> console.printSpaces(spaceService.getSpaces());
            case 2 -> showAvailableSlots();
            case 3 -> makeBooking();
            case 4 -> console.printBookings(bookingService.getAllBookingsForUser(loginOfCurrentUser));
            case 5 -> bookingCancellation();
            case 6 -> logOut();
            case 0 -> System.exit(0);
        }
    }

    /**
     * Displays the available slots for booking.
     */
    private void showAvailableSlots() {
        List<Space> spaces = spaceService.getSpaces();
        console.printSpaces(spaces);
        console.print("Choose space from list.");
        currentSpace = Optional.ofNullable(spaceService.getSpaces().get(input.getInt(0, spaces.size()) - 1));
        console.printAvailableSlotsForBooking(currentSpace.orElseThrow());
    }

    /**
     * Handles the process of making a booking.
     */
    private void makeBooking() {
        showAvailableSlots();

        console.print("Book space from date:");
        LocalDate dateBookingFrom = getDate();
        console.print("and hour:");
        int hourBookingFrom = input.getInt(0, 24);

        console.print("Book space to:");
        LocalDate dateBookingTo = getDate();
        console.print("and hour:");
        int hourBookingTo = input.getInt(0, 24);

        try {
            bookingService.addBooking(loginOfCurrentUser, currentSpace.orElseThrow().name(),
                    dateBookingFrom, hourBookingFrom,
                    dateBookingTo, hourBookingTo);
            console.successfulBooking(currentSpace.orElseThrow().name(), dateBookingFrom, dateBookingTo,
                    hourBookingFrom, hourBookingTo);
        } catch (SpaceIsNotAvailableException e) {
            console.print(e.getMessage());
        }
    }

    /**
     * Handles the process of booking cancellation.
     */
    private void bookingCancellation() {
        console.printBookings(bookingService.getAllBookingsForUser(loginOfCurrentUser));
        console.deleteBookingMessage();
        int numberOfBookingForDelete = input.getInt(0, bookingService.getAllBookingsForUser(loginOfCurrentUser).size());
        if (numberOfBookingForDelete != 0) {
            bookingService.deleteBookingByIndex(loginOfCurrentUser,
                    numberOfBookingForDelete - 1);
            console.print("Booking has been cancelled.");
        }
    }

    /**
     * Logs out the current user.
     */
    private void logOut() {
        console.print("Goodbye! You are logged out.\n");
        loginOfCurrentUser = "login failed";
    }

    /**
     * Displays the admin menu and handles admin actions.
     */
    private void adminMenu() {
        console.showAdminMenu();
        switch (input.getInt(0, 3)) {
            case 1 -> spacesMenu();
            case 2 -> showBookings();
            case 3 -> logOut();
            case 0 -> System.exit(0);
        }
    }

    /**
     * Displays the spaces menu and handles space-related actions.
     */
    private void spacesMenu() {
        List<Space> spaces = spaceService.getSpaces();
        console.printSpaces(spaces);
        console.selectionActionWithSpaces();
        switch (input.getInt(0, 3)) {
            case 1 -> showAvailableSlots();
            case 2 -> addNewSpace();
            case 3 -> deleteSpace(spaces);
            case 0 -> adminMenu();
        }
    }

    /**
     * Handles the process of adding a new space.
     */
    private void addNewSpace() {
        console.print("Enter the name of the new space:");
        String nameOfSpace = input.getString();

        console.print("Enter opening hour:");
        int hourOfStartWorkingDay = input.getInt(0, 24);

        int hourOfEndWorkingDay;
        do {
            console.print("Enter closing hour:");
            hourOfEndWorkingDay = input.getInt(0, 24);
            if (hourOfEndWorkingDay <= hourOfStartWorkingDay) {
                console.errorWhenEnteringHours();
            }
        } while (hourOfEndWorkingDay <= hourOfStartWorkingDay);

        console.print("Enter the number of days available for booking:");
        int numberOfDaysAvailableForBooking = input.getInt(0, Integer.MAX_VALUE);

        spaceService.addAndUpdateSpace(nameOfSpace, hourOfStartWorkingDay, hourOfEndWorkingDay, numberOfDaysAvailableForBooking);
        console.print(nameOfSpace + " added.");
    }

    /**
     * Handles the process of deleting a space.
     *
     * @param spaces the list of spaces
     */
    private void deleteSpace(List<Space> spaces) {
        console.printSpaces(spaces);
        console.deleteSpaceMessage();
        int numberOfSpaceForDelete = input.getInt(0, spaces.size());
        if (numberOfSpaceForDelete != 0) {
            String nameOfSpaceForDelete = spaces.get(numberOfSpaceForDelete - 1).name();
            spaceService.deleteSpace(nameOfSpaceForDelete);
            console.print(nameOfSpaceForDelete + " has been cancelled.");
        }
    }

    /**
     * Displays the bookings menu and handles booking-related actions.
     */
    private void showBookings() {
        console.selectionOfBookingSorting();
        switch (input.getInt(1, 3)) {
            case 1 -> console.printBookings(bookingService.getAllBookingsSortedByUser());
            case 2 -> console.printBookings(bookingService.getAllBookingsSortedByDate());
            case 3 -> console.printBookings(bookingService.getAllBookingsSortedBySpace());
        }
    }

    /**
     * Retrieves the date from the user.
     *
     * @return the selected date.
     */
    private LocalDate getDate() {
        console.setYearMessage();
        int year;
        do {
            year = input.getInteger(4, 0, 9999);
        } while (year < 0);

        console.setMonthMessage();
        int month;
        do {
            month = input.getInteger(2, 1, 12);
        } while (month <= 0);

        console.setDayMessage();
        int day;
        int daysInMonth = YearMonth.of(year, month).lengthOfMonth();
        do {
            day = input.getInteger(2, 1, daysInMonth);
        } while (day <= 0);

        return LocalDate.of(year, month, day);
    }
}
