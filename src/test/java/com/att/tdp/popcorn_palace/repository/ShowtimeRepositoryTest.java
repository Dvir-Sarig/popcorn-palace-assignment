package com.att.tdp.popcorn_palace.repository;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Showtime Repository Tests")
class ShowtimeRepositoryTest {

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String MATRIX_TITLE = "The Matrix";
    private static final String MATRIX_GENRE = "Sci-Fi";
    private static final int MATRIX_DURATION = 136;
    private static final double MATRIX_RATING = 8.7;
    private static final int MATRIX_RELEASE_YEAR = 1999;

    private static final String THEATER = "Theater 1";
    private static final double PRICE = 12.99;
    private static final LocalDateTime START_TIME = LocalDateTime.now().plusDays(1);
    private static final LocalDateTime END_TIME = START_TIME.plusMinutes(MATRIX_DURATION);

    @BeforeEach
    void setUp() {
        initializeTestData();
    }

    private void initializeTestData() {
        Movie testMovie = createMovie();
        testMovie = movieRepository.save(testMovie);

        Showtime showtime1 = createShowtime(testMovie.getId(), THEATER, START_TIME, END_TIME);
        Showtime showtime2 = createShowtime(testMovie.getId(), THEATER, START_TIME.plusHours(3), END_TIME.plusHours(3));
        Showtime showtime3 = createShowtime(testMovie.getId(), "Theater 2", START_TIME.plusDays(1), END_TIME.plusDays(1));

        showtimeRepository.save(showtime1);
        showtimeRepository.save(showtime2);
        showtimeRepository.save(showtime3);
    }

    private Movie createMovie() {
        Movie movie = new Movie();
        movie.setTitle(ShowtimeRepositoryTest.MATRIX_TITLE);
        movie.setGenre(ShowtimeRepositoryTest.MATRIX_GENRE);
        movie.setDuration(ShowtimeRepositoryTest.MATRIX_DURATION);
        movie.setRating(ShowtimeRepositoryTest.MATRIX_RATING);
        movie.setReleaseYear(ShowtimeRepositoryTest.MATRIX_RELEASE_YEAR);
        return movie;
    }

    private Showtime createShowtime(Long movieId, String theater, LocalDateTime startTime, LocalDateTime endTime) {
        Showtime showtime = new Showtime();
        showtime.setMovieId(movieId);
        showtime.setTheater(theater);
        showtime.setStartTime(startTime);
        showtime.setEndTime(endTime);
        showtime.setPrice(ShowtimeRepositoryTest.PRICE);
        return showtime;
    }

    @Nested
    @DisplayName("Find Overlapping Showtimes Tests")
    class FindOverlappingShowtimesTests {

        @Test
        @DisplayName("Should return overlapping showtimes when they exist")
        void shouldReturnOverlappingShowtimes_WhenTheyExist() {
            LocalDateTime searchStartTime = START_TIME.minusMinutes(30);
            LocalDateTime searchEndTime = START_TIME.plusHours(4);

            List<Showtime> result = showtimeRepository.findOverlappingShowtimes(THEATER, searchStartTime, searchEndTime);

            assertThat(result).hasSize(2);
            assertThat(result).allMatch(showtime -> showtime.getTheater().equals(THEATER));
        }

        @Test
        @DisplayName("Should return empty list when no overlapping showtimes exist")
        void shouldReturnEmptyList_WhenNoOverlappingShowtimesExist() {
            LocalDateTime searchStartTime = START_TIME.plusDays(2);
            LocalDateTime searchEndTime = END_TIME.plusDays(2);

            List<Showtime> result = showtimeRepository.findOverlappingShowtimes(THEATER, searchStartTime, searchEndTime);

            assertThat(result).isEmpty();
        }
    }
} 