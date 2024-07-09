# Coworking Service Application

The Coworking Service Application is a Java-based program designed to manage bookings and spaces in a coworking environment. It allows users to book spaces, view available slots, cancel bookings, and manage space availability. The application is built with simplicity and ease of use in mind, providing a seamless experience for both users and administrators.
## Features

- **User Authentication**: Users can log in securely using their credentials or register for a new account if they are not already registered.
- **User and Admin Roles**: The application supports different roles for users and administrators, each with its own set of permissions and functionalities.
- **Space Management**: Admins can add, update, and delete coworking spaces, specifying their opening and closing hours, and availability for booking.
- **Booking Management**: Users can view available spaces, book them for specific dates and times, and cancel their bookings if necessary.
- **Logging User Actions**: Admins can view logs of user actions for auditing and tracking purposes.

## REST API
The application is now a REST API that accepts requests and returns responses in JSON format.

### UserServlet
Handles user registration, login, and authentication. This servlet ensures that users can securely register for new accounts and log in with their credentials.

### SpaceServlet
Manages coworking spaces, including adding, updating, and deleting spaces. Administrators use this servlet to configure the availability and details of coworking spaces.

### BookingServlet
Manages bookings, including viewing available slots, making reservations, and canceling bookings. Users interact with this servlet to book spaces and manage their reservations.

### SlotsForBookingServlet
Provides information about available slots for booking in specific coworking spaces. This servlet responds to requests for checking available booking times for a given space.

### LogServlet
Manages logs of user actions. This servlet allows administrators to view logs of user activities, which is useful for auditing and tracking purposes.

## Installation

To run the Coworking Service Application, follow these steps:

1. Clone this repository to your local machine.
2. 
   ```bash
   git clone https://github.com/EvgeniyMakeev/ylab-intensive-java-3
   ```
   
2. Ensure you have Java installed on your system.
3. Navigate to the project directory in your terminal or command prompt.

   ```bash
   cd coworking-service-app
   ```
4. Compile the Java files using the following command:

  ```bash
  javac -d out src/**/*.java
  ```

## Usage

The application exposes a set of RESTful endpoints. Here are some examples of how to interact with the main features:

- **Login or Register**:  Use the "/api/v1/users" endpoint to register for an account or log in with your existing credentials.
- **View Available Spaces**: Use the /api/v1/spaces endpoint to see a list of available coworking spaces and their details.
- **Book a Space**: Use the "/api/v1/bookings" endpoint to select a space, choose a date and time, and make a booking.
- **Cancel Booking**: Use the "/api/v1/bookings" endpoint to view your existing bookings and cancel them if needed.
- **Admin Functions**: Administrators have additional options to manage spaces, view bookings, view logs, and perform other administrative tasks.

## Contributing

Contributions to the Coworking Service Application are welcome! If you'd like to contribute code, report bugs, or suggest new features, please feel free to submit a pull request or open an issue on GitHub.

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.

## Credits

The Coworking Service Application was developed by Evgeniy Makeev.
