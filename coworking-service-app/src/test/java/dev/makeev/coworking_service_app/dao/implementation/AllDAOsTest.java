package dev.makeev.coworking_service_app.dao.implementation;

import dev.makeev.coworking_service_app.dao.BookingDAO;
import dev.makeev.coworking_service_app.dao.SpaceDAO;
import dev.makeev.coworking_service_app.dao.UserDAO;
import dev.makeev.coworking_service_app.model.Booking;
import dev.makeev.coworking_service_app.model.BookingRange;
import dev.makeev.coworking_service_app.model.Space;
import dev.makeev.coworking_service_app.model.User;
import dev.makeev.coworking_service_app.model.WorkingHours;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest
@DisplayName("Tests for all DAO")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AllDAOsTest {

    private static final String TEST_LOGIN_1 = "TestUser1";
    private static final String TEST_LOGIN_2 = "TestUser2";
    private static final String TEST_PASSWORD = "TestPassword";
    private static final String WRONG_LOGIN = "WrongLogin";
    private static final String TEST_SPACE_NAME = "TestSpace";
    private static Space TEST_SPACE;
    private static final Booking TEST_BOOKING_1 = new Booking(TEST_LOGIN_1, TEST_SPACE_NAME,
            new BookingRange(LocalDate.of(2024, 7, 2), 12,
                    LocalDate.of(2024, 7, 5), 20));
    private static final Booking TEST_BOOKING_2 = new Booking(TEST_LOGIN_2, TEST_SPACE_NAME,
            new BookingRange(LocalDate.of(2024, 7, 9), 10,
                    LocalDate.of(2024, 7, 9), 20));

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:16.2");

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private SpaceDAO spaceDAO;

    @Autowired
    private BookingDAO bookingDAO;

    @BeforeAll
    static void setUpAll() {
        postgresContainer.start();
        TEST_SPACE = initNewSpace();
    }

    private static Space initNewSpace() {
        int hourOfBeginningWorkingDay = 10;
        int hourOfEndingWorkingDay = 20;
        int numberOfDaysAvailableForBooking = 14;
        LocalDate nowDate = LocalDate.now();

        Map<LocalDate, Map<Integer, Long>> bookingSlots = new HashMap<>();

        Map<Integer, Long> slots = new HashMap<>();
        long freeSlot = 0L;
        for (int i = hourOfBeginningWorkingDay; i < hourOfEndingWorkingDay; i++) {
            slots.put(i, freeSlot);
        }

        for (int i = 0; i < numberOfDaysAvailableForBooking; i++) {
            bookingSlots.put(nowDate.plusDays(i), slots);
        }

        return new Space(TEST_SPACE_NAME,
                new WorkingHours(hourOfBeginningWorkingDay, hourOfEndingWorkingDay),
                bookingSlots);
    }

    @AfterAll
    static void afterAll() {
        postgresContainer.stop();
    }

    @Test
    @Order(1)
    @DisplayName("UserDAOInBd test: Add User - Should add new user")
    void add_shouldAddUser() {
        Optional<User> userBeforeAdd = userDAO.getByLogin(TEST_LOGIN_1);
        userDAO.add(new User(TEST_LOGIN_1, TEST_PASSWORD, false));

        assertTrue(userBeforeAdd.isEmpty());
        assertThat(userDAO.getByLogin(TEST_LOGIN_1)).isPresent();
    }

    @Test
    @Order(2)
    @DisplayName("UserDAOInBd test: Get User by login - Success")
    void getBy_shouldGetUser_whenExists() {
        Optional<User> user = userDAO.getByLogin(TEST_LOGIN_1);

        assertThat(user).isPresent();
        assertThat(user.get().password()).isEqualTo(TEST_PASSWORD);
    }

    @Test
    @Order(3)
    @DisplayName("UserDAOImpl test: Get By Login - Should return empty optional if user does not exist")
    void getBy_shouldReturnEmptyOptionalIfUserDoesNotExist() {
        Optional<User> result = userDAO.getByLogin(WRONG_LOGIN);

        assertThat(result).isEmpty();
    }

    @Test
    @Order(4)
    @DisplayName("SpaceDAOInBd test: Add Space - Should add new space")
    void add_shouldAddSpace() {
        List<String> spacesBeforeAdd = spaceDAO.getNamesOfSpaces();
        spaceDAO.add(TEST_SPACE);
        List<String> spacesAfterAdd = spaceDAO.getNamesOfSpaces();

        assertThat(spacesAfterAdd.size())
                .isEqualTo(spacesBeforeAdd.size() + 1);
        assertTrue(spaceDAO.getSpaceByName(TEST_SPACE_NAME).isPresent());
    }

    @Test
    @Order(5)
    @DisplayName("SpaceDAOInBd test: Get Space by name - Should get Space by name")
    void getSpaceByName_shouldGetSpace_whenExists() {
        Optional<Space> space = spaceDAO.getSpaceByName(TEST_SPACE_NAME);

        assertTrue(space.isPresent());
        assertThat(space.get().name()).isEqualTo(TEST_SPACE_NAME);
    }

    @Test
    @Order(6)
    @DisplayName("SpaceDAOInBd test: Get By Type - Get All Spaces - Should return all names of spaces")
    void getNamesOfSpaces() {
        List<String> namesOfSpaces = spaceDAO.getNamesOfSpaces();

        assertFalse(namesOfSpaces.isEmpty());
        assertThat(namesOfSpaces).contains(TEST_SPACE_NAME);
    }

    @Test
    @Order(7)
    @DisplayName("SpaceDAOInBd test: Get Space by name - Should return null if space does not exist")
    void getSpaceByName_shouldReturnNullIfSpaceDoesNotExist() {
        Optional<Space> space = spaceDAO.getSpaceByName("NonExistentSpace");

        assertTrue(space.isEmpty());
    }

    @Test
    @Order(8)
    @DisplayName("BookingDAOInBd test: Add Booking - Should add new booking for user")
    void add_shouldAddBooking() {
        List<Booking> bookingsBeforeAdd = bookingDAO.getAllForUser(TEST_LOGIN_1);
        bookingDAO.add(TEST_BOOKING_1);

        List<Booking> bookingsAfterAdd = bookingDAO.getAllForUser(TEST_LOGIN_1);
        assertTrue(bookingsBeforeAdd.isEmpty());
        assertFalse(bookingsAfterAdd.isEmpty());
        assertThat(bookingsAfterAdd.get(0).login()).isEqualTo(TEST_BOOKING_1.login());
        assertThat(bookingsAfterAdd.get(0).nameOfBookingSpace()).isEqualTo(TEST_BOOKING_1.nameOfBookingSpace());
        assertThat(bookingsAfterAdd.get(0).bookingRange()).isEqualTo(TEST_BOOKING_1.bookingRange());
    }

    @Test
    @Order(9)
    @DisplayName("BookingDAOInBd test: Get All Bookings for User - Should return all bookings for a user")
    void getAllForUser_shouldReturnAllBookingsForUser() {
        List<Booking> bookings = bookingDAO.getAllForUser(TEST_LOGIN_1);

        assertFalse(bookings.isEmpty());
        assertThat(bookings.get(0).login()).isEqualTo(TEST_LOGIN_1);
        assertThat(bookings.get(0).nameOfBookingSpace()).isEqualTo(TEST_BOOKING_1.nameOfBookingSpace());
        assertThat(bookings.get(0).bookingRange()).isEqualTo(TEST_BOOKING_1.bookingRange());
    }

    @Test
    @Order(10)
    @DisplayName("BookingDAOInBd test: Get All Bookings - Should return all bookings")
    void getAll_shouldReturnAllBookings() {
        List<Booking> allBookingsBeforeAdd = bookingDAO.getAll();
        userDAO.add(new User(TEST_LOGIN_2, TEST_PASSWORD, true));
        bookingDAO.add(TEST_BOOKING_2);

        List<Booking> allBookingsAfterAdd = bookingDAO.getAll();

        assertThat(allBookingsAfterAdd.size()).isEqualTo(allBookingsBeforeAdd.size() + 1);
        assertThat(allBookingsAfterAdd).usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(TEST_BOOKING_1, TEST_BOOKING_2);
    }
}
