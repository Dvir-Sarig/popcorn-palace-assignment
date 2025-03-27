package com.att.tdp.popcorn_palace.repository;

import com.att.tdp.popcorn_palace.model.Booking;
import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.model.Showtime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Booking Repository Tests")
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Test data constants
    private static final String MATRIX_TITLE = "The Matrix";
    private static final String MATRIX_GENRE = "Sci-Fi";
    private static final int MATRIX_DURATION = 136;
    private static final double MATRIX_RATING = 8.7;
    private static final int MATRIX_RELEASE_YEAR = 1999;

    private static final String THEATER = "Theater 1";
    private static final double PRICE = 12.99;
    private static final LocalDateTime START_TIME = LocalDateTime.now().plusDays(1);
    private static final LocalDateTime END_TIME = START_TIME.plusMinutes(MATRIX_DURATION);

    private static final UUID USER_ID = UUID.fromString("84438967-f68f-4fa0-b620-0f08217e76af");
    private static final Integer SEAT_NUMBER = 1;

    private Showtime testShowtime;

    @BeforeEach
    void setUp() {
        initializeTestData();
    }

    private void initializeTestData() {
        Movie movie = createMovie();
        movie = movieRepository.save(movie);

        testShowtime = createShowtime(movie.getId());
        testShowtime = showtimeRepository.save(testShowtime);

        Booking booking1 = createBooking(testShowtime.getId(), USER_ID, SEAT_NUMBER);
        Booking booking2 = createBooking(testShowtime.getId(), USER_ID, 2);
        Booking booking3 = createBooking(testShowtime.getId(), UUID.randomUUID(), 3);

        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
        bookingRepository.save(booking3);
    }

    private Movie createMovie() {
        Movie movie = new Movie();
        movie.setTitle(BookingRepositoryTest.MATRIX_TITLE);
        movie.setGenre(BookingRepositoryTest.MATRIX_GENRE);
        movie.setDuration(BookingRepositoryTest.MATRIX_DURATION);
        movie.setRating(BookingRepositoryTest.MATRIX_RATING);
        movie.setReleaseYear(BookingRepositoryTest.MATRIX_RELEASE_YEAR);
        return movie;
    }

    private Showtime createShowtime(Long movieId) {
        Showtime showtime = new Showtime();
        showtime.setMovieId(movieId);
        showtime.setTheater(BookingRepositoryTest.THEATER);
        showtime.setStartTime(BookingRepositoryTest.START_TIME);
        showtime.setEndTime(BookingRepositoryTest.END_TIME);
        showtime.setPrice(BookingRepositoryTest.PRICE);
        return showtime;
    }

    private Booking createBooking(Long showtimeId, UUID userId, Integer seatNumber) {
        Booking booking = new Booking();
        booking.setShowtimeId(showtimeId);
        booking.setUserId(userId.toString());
        booking.setSeatNumber(seatNumber);
        return booking;
    }

    @Nested
    @DisplayName("Exists By Showtime ID And Seat Number Tests")
    class ExistsByShowtimeIdAndSeatNumberTests {

        @Test
        @DisplayName("Should return true when booking exists")
        void shouldReturnTrue_WhenBookingExists() {
            Long showtimeId = testShowtime.getId();
            boolean exists = bookingRepository.existsByShowtimeIdAndSeatNumber(showtimeId, SEAT_NUMBER);
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("Should return false when booking does not exist")
        void shouldReturnFalse_WhenBookingDoesNotExist() {
            Long showtimeId = testShowtime.getId();
            Integer seatNumber = 999;
            boolean exists = bookingRepository.existsByShowtimeIdAndSeatNumber(showtimeId, seatNumber);
            assertThat(exists).isFalse();
        }
    }
} 