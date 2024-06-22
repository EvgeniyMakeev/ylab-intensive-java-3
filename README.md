# Coworking Service Application

The Coworking Service Application is a Java-based program designed to manage bookings and spaces in a coworking environment. It allows users to book spaces, view available slots, cancel bookings, and manage space availability. The application is built with simplicity and ease of use in mind, providing a seamless experience for both users and administrators.

## Features

- **User Authentication**: Users can log in securely using their credentials or register for a new account if they are not already registered.
- **User and Admin Roles**: The application supports different roles for users and administrators, each with its own set of permissions and functionalities.
- **Space Management**: Admins can add, update, and delete coworking spaces, specifying their opening and closing hours, and availability for booking.
- **Booking Management**: Users can view available spaces, book them for specific dates and times, and cancel their bookings if necessary.
- **Flexible Booking System**: The booking system is flexible and intuitive, allowing users to easily find available slots and make reservations according to their preferences.
- **Interactive User Interface**: The application features an interactive command-line interface (CLI) that guides users through the booking process and provides helpful prompts and messages.

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
5. Run the compiled program using the following command:

  ```bash
  java -cp out dev.makeev.coworking_service_app.App
  ```

## Usage

Once the application is running, users can interact with it using the command-line interface. Here's how to use some of the main features:

- **Login or Register**: If you're a new user, register for an account. Otherwise, log in with your existing credentials.
- **View Available Spaces**: See a list of available coworking spaces and their details.
- **Book a Space**: Select a space, choose a date and time, and make a booking.
- **Cancel Booking**: View your existing bookings and cancel them if needed.
- **Admin Functions**: Administrators have additional options to manage spaces, view bookings, and perform other administrative tasks.

## Contributing

Contributions to the Coworking Service Application are welcome! If you'd like to contribute code, report bugs, or suggest new features, please feel free to submit a pull request or open an issue on GitHub.

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.

## Credits

The Coworking Service Application was developed by Evgeniy Makeev.
