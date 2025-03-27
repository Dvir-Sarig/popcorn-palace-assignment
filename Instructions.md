# Popcorn Palace - Movie Theater Management System

## Overview
Popcorn Palace is a Spring Boot application that manages a movie theater system. It allows users to manage movies, showtimes, and bookings.

## Prerequisites
- Java 21 or later
- Maven 3.6 or later

## Project Structure
```
popcorn-palace/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/att/tdp/popcorn_palace/
│   │   │       ├── controller/    # REST controllers
│   │   │       ├── service/       # Business logic
│   │   │       ├── repository/    # Data access layer
│   │   │       ├── model/         # Entity classes
│   │   │       ├── dto/           # Data Transfer Objects
│   │   │       └── exceptions/    # Exception handling
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/att/tdp/popcorn_palace/
│               ├── controller/    # Controller tests
│               └── service/       # Service tests
└── pom.xml
```

## Building the Project

### Using Maven Command Line
1. Navigate to the project root directory:
   ```bash
   cd popcorn-palace
   ```

2. Clean and build the project:
   ```bash
   mvn clean install
   ```

### Using an IDE (IntelliJ IDEA)
1. Open the project in IntelliJ IDEA
2. Right-click on the project in the Project Explorer
3. Select "Maven" -> "Reload Project"
4. Wait for Maven to download dependencies
5. Build the project using the Maven tool window or by pressing Ctrl+F9

## Running the Application

### Using Maven Command Line
```bash
mvn spring:boot run
```

### Using an IDE (IntelliJ IDEA)
1. Find the main application class (`PopcornPalaceApplication.java`)
2. Right-click on the class
3. Select "Run 'PopcornPalaceApplication'"

## Testing the Application

### Running All Tests
```bash
mvn test
```

### Running Specific Test Classes
```bash
# Run MovieControllerTest
mvn test -Dtest=MovieControllerTest

# Run ShowtimeControllerTest
mvn test -Dtest=ShowtimeControllerTest

# Run BookingControllerTest
mvn test -Dtest=BookingControllerTest
```

### Running Tests in an IDE
1. Open the test class you want to run
2. Right-click on the class or individual test method
3. Select "Run" or "Debug"

## API Endpoints

### Movies
- `GET /movies/all` - Get all movies
- `POST /movies` - Create a new movie
- `POST /movies/update/{title}` - Update a movie
- `DELETE /movies/{title}` - Delete a movie

### Showtimes
- `POST /showtimes` - Create a new showtime
- `GET /showtimes/{showtimeId}` - Get a showtime by ID
- `POST /showtimes/update/{showtimeId}` - Update a showtime
- `DELETE /showtimes/{showtimeId}` - Delete a showtime

### Bookings
- `POST /bookings` - Create a new booking

## Database Configuration
The application uses H2 in-memory database for development and testing. The database is automatically configured and initialized when the application starts. No additional setup is required.

The H2 console is available at `http://localhost:8080/h2-console` when the application is running. You can use it to:
- View the database schema
- Execute SQL queries
- Monitor database contents

Default H2 console credentials:
- JDBC URL: `jdbc:h2:mem:popcorn_palace`
- Username: `sa`
- Password: ``

