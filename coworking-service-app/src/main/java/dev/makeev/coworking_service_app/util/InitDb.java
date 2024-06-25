package dev.makeev.coworking_service_app.util;

import dev.makeev.coworking_service_app.dao.UserDAO;
import dev.makeev.coworking_service_app.exceptions.SpaceIsNotAvailableException;
import dev.makeev.coworking_service_app.model.User;
import dev.makeev.coworking_service_app.service.BookingService;
import dev.makeev.coworking_service_app.service.SpaceService;

import java.time.LocalDate;

/**
 * Utility class for initializing the database with default values.
 */
public final class InitDb {

    private final UserDAO userDAO;
    private final SpaceService spaceService;
    private final BookingService bookingService;

    /**
     * Constructs an InitDb object with the specified user DAO, space service, and booking service.
     *
     * @param userDAO       the user DAO
     * @param spaceService  the space service
     * @param bookingService the booking service
     */
    public InitDb(UserDAO userDAO, SpaceService spaceService, BookingService bookingService) {
        this.userDAO = userDAO;
        this.spaceService = spaceService;
        this.bookingService = bookingService;
    }

    /**
     * Initializes the database with default values.
     *
     * @throws SpaceIsNotAvailableException if a space is not available for booking
     */
    public void initDb() throws SpaceIsNotAvailableException {
        userDAO.add(new User("admin", "1234", true));
        userDAO.add(new User("User1", "pass1", false));
        userDAO.add(new User("User2", "pass2", false));

        spaceService.addAndUpdateSpace("Workplace No. 1", 8, 20, 20);
        spaceService.addAndUpdateSpace("Conference hall", 10, 18, 15);

        bookingService.addBooking("User1", "Workplace No. 1",
                LocalDate.now(), 8, LocalDate.now().plusDays(2), 15);
        bookingService.addBooking("User1", "Workplace No. 1",
                LocalDate.now().plusDays(5), 10, LocalDate.now().plusDays(5), 18);
        bookingService.addBooking("User1", "Conference hall",
                LocalDate.now().plusDays(1), 11, LocalDate.now().plusDays(4), 15);

        bookingService.addBooking("User2", "Workplace No. 1",
                LocalDate.now().plusDays(3), 11, LocalDate.now().plusDays(3), 14);
        bookingService.addBooking("User2", "Workplace No. 1",
                LocalDate.now().plusDays(11), 8, LocalDate.now().plusDays(13), 20);
        bookingService.addBooking("User2", "Conference hall",
                LocalDate.now().plusDays(8), 11, LocalDate.now().plusDays(12), 15);
    }
}
