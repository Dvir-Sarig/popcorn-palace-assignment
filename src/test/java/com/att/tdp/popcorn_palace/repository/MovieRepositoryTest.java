package com.att.tdp.popcorn_palace.repository;

import com.att.tdp.popcorn_palace.model.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Movie Repository Tests")
class MovieRepositoryTest {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String MATRIX_TITLE = "The Matrix";
    private static final String MATRIX_GENRE = "Sci-Fi";
    private static final int MATRIX_DURATION = 136;
    private static final double MATRIX_RATING = 8.7;
    private static final int MATRIX_RELEASE_YEAR = 1999;

    private static final String NON_EXISTENT_TITLE = "Non-existent Movie";

    @BeforeEach
    void setUp() {
        initializeTestData();
    }

    private void initializeTestData() {
        Movie matrix = createMovie(MATRIX_TITLE, MATRIX_GENRE, MATRIX_DURATION, MATRIX_RATING, MATRIX_RELEASE_YEAR);
        Movie pulpFiction = createMovie("Pulp Fiction", "Crime", 154, 8.9, 1994);
        Movie fightClub = createMovie("Fight Club", "Drama", 139, 8.4, 1999);

        movieRepository.save(matrix);
        movieRepository.save(pulpFiction);
        movieRepository.save(fightClub);
    }

    private Movie createMovie(String title, String genre, int duration, double rating, int releaseYear) {
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setGenre(genre);
        movie.setDuration(duration);
        movie.setRating(rating);
        movie.setReleaseYear(releaseYear);
        return movie;
    }

    @Nested
    @DisplayName("Find By Title Tests")
    class FindByTitleTests {

        @Test
        @DisplayName("Should return movie when title exists")
        void shouldReturnMovie_WhenTitleExists() {

            Optional<Movie> result = movieRepository.findByTitle(MATRIX_TITLE);

            assertThat(result).isPresent();
            Movie movie = result.get();
            assertThat(movie.getTitle()).isEqualTo(MATRIX_TITLE);
            assertThat(movie.getGenre()).isEqualTo(MATRIX_GENRE);
            assertThat(movie.getDuration()).isEqualTo(MATRIX_DURATION);
            assertThat(movie.getRating()).isEqualTo(MATRIX_RATING);
            assertThat(movie.getReleaseYear()).isEqualTo(MATRIX_RELEASE_YEAR);
        }

        @Test
        @DisplayName("Should return empty when title does not exist")
        void shouldReturnEmpty_WhenTitleDoesNotExist() {
            Optional<Movie> result = movieRepository.findByTitle(NON_EXISTENT_TITLE);
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Exists By Title Tests")
    class ExistsByTitleTests {

        @Test
        @DisplayName("Should return true when title exists")
        void shouldReturnTrue_WhenTitleExists() {
            boolean exists = movieRepository.existsByTitle(MATRIX_TITLE);
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("Should return false when title does not exist")
        void shouldReturnFalse_WhenTitleDoesNotExist() {
            boolean exists = movieRepository.existsByTitle(NON_EXISTENT_TITLE);
            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("Delete By Title Tests")
    class DeleteByTitleTests {

        @Test
        @DisplayName("Should delete movie when title exists")
        void shouldDeleteMovie_WhenTitleExists() {
            String title = MATRIX_TITLE;
            assertThat(movieRepository.existsByTitle(title)).isTrue();
            movieRepository.deleteByTitle(title);
            assertThat(movieRepository.existsByTitle(title)).isFalse();
            assertThat(movieRepository.findByTitle(title)).isEmpty();
        }

        @Test
        @DisplayName("Should not throw exception when deleting non-existent title")
        void shouldNotThrowException_WhenDeletingNonExistentTitle() {
            String title = NON_EXISTENT_TITLE;
            assertThat(movieRepository.existsByTitle(title)).isFalse();
            assertThat(movieRepository.findByTitle(title)).isEmpty();
            movieRepository.deleteByTitle(title); // Should not throw exception
        }
    }
}