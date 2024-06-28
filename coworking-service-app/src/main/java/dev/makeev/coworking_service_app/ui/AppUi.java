package dev.makeev.coworking_service_app.ui;

import dev.makeev.coworking_service_app.dao.SpaceDAO;
import dev.makeev.coworking_service_app.dao.UserDAO;
import dev.makeev.coworking_service_app.dao.implementation.BookingDAOInBd;
import dev.makeev.coworking_service_app.dao.implementation.SpaceDAOInBd;
import dev.makeev.coworking_service_app.dao.implementation.UserDAOInBd;
import dev.makeev.coworking_service_app.exceptions.NoSlotsException;
import dev.makeev.coworking_service_app.exceptions.SpaceAlreadyExistsException;
import dev.makeev.coworking_service_app.exceptions.SpaceIsNotAvailableException;
import dev.makeev.coworking_service_app.in.Input;
import dev.makeev.coworking_service_app.in.implementation.ConsoleInput;
import dev.makeev.coworking_service_app.out.implementation.ConsoleOutput;
import dev.makeev.coworking_service_app.service.BookingService;
import dev.makeev.coworking_service_app.service.SpaceService;
import dev.makeev.coworking_service_app.service.UserService;
import dev.makeev.coworking_service_app.util.ConnectionManager;
import dev.makeev.coworking_service_app.util.implementation.ConnectionManagerImpl;
import dev.makeev.coworking_service_app.util.InitDb;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * The main user interface class for the coworking service application.
 */
public final class AppUi {

    private final ConnectionManager connectionManager = new ConnectionManagerImpl();
    private final InitDb initDB = new InitDb(connectionManager);
    private final UserDAO userDAO = new UserDAOInBd(connectionManager);
    private final UserService userService = new UserService(userDAO);
    private final SpaceDAO spaceDAO = new SpaceDAOInBd(connectionManager);
    private final BookingService bookingService = new BookingService(new BookingDAOInBd(connectionManager), spaceDAO);
    private final SpaceService spaceService = new SpaceService(spaceDAO);
    private final Input input = new ConsoleInput();
    private final Messages console = new Messages(new ConsoleOutput());

    private String loginOfCurrentUser = "login failed";
    private String nameOfCurrentSpace = "none";

    /**
     * Initializes the database.
     */
    public void initDb(){
        initDB.initDb();
    }

    /**
     * Runs the application user interface.
     */
    public void run() {
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
        int numberOfSpace = getNumberOfSpace();
        if (numberOfSpace != 0) {
            nameOfCurrentSpace = spaceService.getSpaces().get(numberOfSpace - 1);
            console.printAvailableSlotsForBooking(spaceService.getSpaceByName(nameOfCurrentSpace).orElseThrow());
        }
    }

    private int getNumberOfSpace() {
        List<String> nameOfSpace = spaceService.getSpaces();
        if (nameOfSpace.isEmpty()) {
            try {
                throw new NoSlotsException();
            } catch (NoSlotsException e) {
                console.print(e.getMessage());
            }
        }
        console.printSpaces(nameOfSpace);
        console.chooseSpaceMessage();
        return input.getInt(0, nameOfSpace.size());
    }

    /**
     * Handles the process of making a booking.
     */
    private void makeBooking() {
        int numberOfSpace = getNumberOfSpace();
        if (numberOfSpace != 0) {
            nameOfCurrentSpace = spaceService.getSpaces().get(numberOfSpace - 1);
            console.printAvailableSlotsForBooking(spaceService.getSpaceByName(nameOfCurrentSpace).orElseThrow());

            console.print("Book space from date:");
            LocalDate dateBookingFrom = getDate();
            console.print("and hour:");
            int hourBookingFrom = input.getInt(0, 24);

            console.print("Book space to:");
            LocalDate dateBookingTo = getDate();
            console.print("and hour:");
            int hourBookingTo = input.getInt(0, 24);

            try {
                bookingService.addBooking(loginOfCurrentUser, nameOfCurrentSpace,
                        dateBookingFrom, hourBookingFrom,
                        dateBookingTo, hourBookingTo);
                console.successfulBooking(nameOfCurrentSpace, dateBookingFrom, dateBookingTo,
                        hourBookingFrom, hourBookingTo);
            } catch (SpaceIsNotAvailableException e) {
                console.print(e.getMessage());
            }
        }
    }

    /**
     * Handles the process of booking cancellation.
     */
    private void bookingCancellation() {
        List<String> bookingList = bookingService.getAllBookingsForUser(loginOfCurrentUser);
        console.printBookings(bookingList);
        console.deleteBookingMessage();
        int numberOfBookingForDelete = input.getInt(0, bookingList.size());
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
        List<String> namesOfSpaces = spaceService.getSpaces();
        console.printSpaces(namesOfSpaces);
        console.selectionActionWithSpaces();
        switch (input.getInt(0, 3)) {
            case 1 -> showAvailableSlots();
            case 2 -> addNewSpace();
            case 3 -> deleteSpace(namesOfSpaces);
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

        try {
            spaceService.addSpace(nameOfSpace, hourOfStartWorkingDay, hourOfEndWorkingDay, numberOfDaysAvailableForBooking);
            console.print(nameOfSpace + " added.");
        } catch (SpaceAlreadyExistsException e) {
            console.print(e.getMessage());
        }
    }

    /**
     * Handles the process of deleting a space.
     *
     * @param namesOfSpaces the list of names of spaces
     */
    private void deleteSpace(List<String> namesOfSpaces) {
        console.printSpaces(namesOfSpaces);
        console.deleteSpaceMessage();
        int numberOfSpaceForDelete = input.getInt(0, namesOfSpaces.size());
        if (numberOfSpaceForDelete != 0) {
            String nameOfSpaceForDelete = namesOfSpaces.get(numberOfSpaceForDelete - 1);
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
