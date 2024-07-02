package dev.makeev.coworking_service_app.enums;

/**
 * Enum representing various SQL requests used in the coworking service application.
 */
public enum SQLRequest {
    ADD_USER_SQL ("INSERT INTO non_public.users (login, password, admin) VALUES (?,?,?)"),

    GET_USER_BY_LOGIN_SQL ("SELECT * FROM non_public.users WHERE login=? "),

    ADD_SPACE_SQL("""
            INSERT INTO non_public.spaces \
            (name, hour_of_beginning_working_day, hour_of_ending_working_day) VALUES (?,?,?)"""),
    GET_ALL_SPACES_SQL("SELECT * FROM non_public.spaces"),
    GET_SPACE_BY_NAME_SQL(GET_ALL_SPACES_SQL.query + " WHERE name=?"),
    GET_WORKING_HOURS_OF_SPACE_BY_NAME_SQL("SELECT hour_of_beginning_working_day, hour_of_ending_working_day FROM non_public.spaces WHERE name=?"),

    ADD_SLOTS_SQL("""
            INSERT INTO non_public.slots_for_booking \
            (name_of_space, date, hour, booking_id) VALUES (?,?,?,?)"""),

    BOOK_SLOTS_SQL("UPDATE non_public.slots_for_booking SET booking_id=? WHERE name_of_space=? AND date=? AND hour=?"),

    UPDATE_SLOTS_SQL("UPDATE non_public.slots_for_booking SET booking_id=? WHERE booking_id=?"),

    GET_SLOTS_BY_SPACE_NAME_SQL("""
            SELECT date, hour, booking_id \
            FROM non_public.slots_for_booking WHERE name_of_space=?"""),

    DELETE_SPACE_SQL("DELETE FROM non_public.spaces WHERE name=?"),

    DELETE_BOOKING_FOR_SPACE_SQL("DELETE FROM non_public.bookings WHERE name_of_space=?"),

    DELETE_SLOTS_FOR_SPACE_SQL("DELETE FROM non_public.slots_for_booking WHERE name_of_space=?"),

    ADD_BOOKING_SQL("""
            INSERT INTO non_public.bookings \
            (login_of_user, name_of_space, \
            beginning_booking_date, beginning_booking_hour, \
            ending_booking_date, ending_booking_hour) \
            VALUES (?,?,?,?,?,?)"""),
    GET_ALL_BOOKINGS_SQL("SELECT * FROM non_public.bookings"),

    GET_ALL_BOOKINGS_FOR_USER_SQL(GET_ALL_BOOKINGS_SQL.query + " WHERE login_of_user=?"),

    DELETE_BOOKING_SQL("DELETE FROM non_public.bookings WHERE id=?");


    private final String query;

    /**
     * Constructor to initialize SQL query.
     *
     * @param query SQL query
     */
    SQLRequest(String query) {
        this.query = query;
    }

    /**
     * Returns the SQL query.
     *
     * @return SQL query
     */
    public String getQuery() {
        return query;
    }
}
