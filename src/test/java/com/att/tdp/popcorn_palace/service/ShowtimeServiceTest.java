package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.dto.ShowtimeDTO;
import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Showtime Service Tests")
class ShowtimeServiceTest {

    @Mock
    private ShowtimeRepository showtimeRepository;

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private ShowtimeService showtimeService;

    private static final Long MOVIE_ID = 1L;
    private static final String MOVIE_TITLE = "The Matrix";
    private static final String THEATER = "Theater 1";
    private static final double PRICE = 12.99;
    private static final LocalDateTime START_TIME = LocalDateTime.now().plusDays(1);
    private static final LocalDateTime END_TIME = START_TIME.plusMinutes(136);

    private Movie testMovie;
    private ShowtimeDTO testShowtimeDTO;

    @BeforeEach
    void setUp() {
        testMovie = createMovie();
        testShowtimeDTO = createShowtimeDTO();
    }

    @Nested
    @DisplayName("Create Showtime Tests")
    class CreateShowtimeTests {

        @Test
        @DisplayName("Should create showtime when valid data is provided")
        void shouldCreateShowtime_WhenValidDataProvided() {
            when(movieRepository.findById(MOVIE_ID)).thenReturn(Optional.of(testMovie));
            when(showtimeRepository.findOverlappingShowtimes(any(), any(), any())).thenReturn(List.of());
            when(showtimeRepository.save(any(Showtime.class))).thenAnswer(invocation -> {
                Showtime showtime = invocation.getArgument(0);
                showtime.setId(1L);
                return showtime;
            });

            Showtime result = showtimeService.createShowtime(testShowtimeDTO);

            assertThat(result).isNotNull();
            assertThat(result.getMovieId()).isEqualTo(MOVIE_ID);
            assertThat(result.getTheater()).isEqualTo(THEATER);
            assertThat(result.getPrice()).isEqualTo(PRICE);
            assertThat(result.getStartTime()).isEqualTo(START_TIME);
            assertThat(result.getEndTime()).isEqualTo(END_TIME);

            verify(movieRepository).findById(MOVIE_ID);
            verify(showtimeRepository).findOverlappingShowtimes(any(), any(), any());
            verify(showtimeRepository).save(any(Showtime.class));
        }

        @Test
        @DisplayName("Should throw exception when movie does not exist")
        void shouldThrowException_WhenMovieDoesNotExist() {
            when(movieRepository.findById(MOVIE_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> showtimeService.createShowtime(testShowtimeDTO))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Movie not found with ID: " + MOVIE_ID);

            verify(movieRepository).findById(MOVIE_ID);
            verify(showtimeRepository, never()).save(any(Showtime.class));
        }

        @Test
        @DisplayName("Should throw exception when overlapping showtimes exist")
        void shouldThrowException_WhenOverlappingShowtimesExist() {
            when(movieRepository.findById(MOVIE_ID)).thenReturn(Optional.of(testMovie));
            when(showtimeRepository.findOverlappingShowtimes(any(), any(), any()))
                    .thenReturn(List.of(new Showtime()));

            assertThatThrownBy(() -> showtimeService.createShowtime(testShowtimeDTO))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("There are overlapping showtimes in this theater");

            verify(movieRepository).findById(MOVIE_ID);
            verify(showtimeRepository).findOverlappingShowtimes(any(), any(), any());
            verify(showtimeRepository, never()).save(any(Showtime.class));
        }
    }

    @Nested
    @DisplayName("Get Showtime Tests")
    class GetShowtimeTests {

        @Test
        @DisplayName("Should return showtime when ID exists")
        void shouldReturnShowtime_WhenIdExists() {
            Long showtimeId = 1L;
            Showtime expectedShowtime = createShowtime(showtimeId);
            when(showtimeRepository.findById(showtimeId)).thenReturn(Optional.of(expectedShowtime));

            Showtime result = showtimeService.getShowtimeById(showtimeId);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(showtimeId);
            assertThat(result.getMovieId()).isEqualTo(MOVIE_ID);
            assertThat(result.getTheater()).isEqualTo(THEATER);
            assertThat(result.getPrice()).isEqualTo(PRICE);
            assertThat(result.getStartTime()).isEqualTo(START_TIME);
            assertThat(result.getEndTime()).isEqualTo(END_TIME);

            verify(showtimeRepository).findById(showtimeId);
        }

        @Test
        @DisplayName("Should throw exception when ID does not exist")
        void shouldThrowException_WhenIdDoesNotExist() {
            Long showtimeId = 999L;
            when(showtimeRepository.findById(showtimeId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> showtimeService.getShowtimeById(showtimeId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Showtime not found with ID: " + showtimeId);

            verify(showtimeRepository).findById(showtimeId);
        }
    }

    @Nested
    @DisplayName("Update Showtime Tests")
    class UpdateShowtimeTests {

        @Test
        @DisplayName("Should update showtime when valid data is provided")
        void shouldUpdateShowtime_WhenValidDataProvided() {
            Long showtimeId = 1L;
            Showtime existingShowtime = createShowtime(showtimeId);
            when(showtimeRepository.findById(showtimeId)).thenReturn(Optional.of(existingShowtime));
            when(movieRepository.findById(MOVIE_ID)).thenReturn(Optional.of(testMovie));
            when(showtimeRepository.findOverlappingShowtimes(any(), any(), any())).thenReturn(List.of());
            when(showtimeRepository.save(any(Showtime.class))).thenReturn(existingShowtime);

            Showtime result = showtimeService.updateShowtime(showtimeId, testShowtimeDTO);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(showtimeId);
            assertThat(result.getMovieId()).isEqualTo(MOVIE_ID);
            assertThat(result.getTheater()).isEqualTo(THEATER);
            assertThat(result.getPrice()).isEqualTo(PRICE);
            assertThat(result.getStartTime()).isEqualTo(START_TIME);
            assertThat(result.getEndTime()).isEqualTo(END_TIME);

            verify(showtimeRepository).findById(showtimeId);
            verify(movieRepository).findById(MOVIE_ID);
            verify(showtimeRepository).findOverlappingShowtimes(any(), any(), any());
            verify(showtimeRepository).save(any(Showtime.class));
        }

        @Test
        @DisplayName("Should throw exception when showtime does not exist")
        void shouldThrowException_WhenShowtimeDoesNotExist() {
            Long showtimeId = 999L;
            when(showtimeRepository.findById(showtimeId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> showtimeService.updateShowtime(showtimeId, testShowtimeDTO))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Showtime not found with ID: " + showtimeId);

            verify(showtimeRepository).findById(showtimeId);
            verify(showtimeRepository, never()).save(any(Showtime.class));
        }

        @Test
        @DisplayName("Should throw exception when movie does not exist")
        void shouldThrowException_WhenMovieDoesNotExist() {
            Long showtimeId = 1L;
            Showtime existingShowtime = createShowtime(showtimeId);
            when(showtimeRepository.findById(showtimeId)).thenReturn(Optional.of(existingShowtime));
            when(movieRepository.findById(MOVIE_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> showtimeService.updateShowtime(showtimeId, testShowtimeDTO))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Movie not found with ID: " + MOVIE_ID);

            verify(showtimeRepository).findById(showtimeId);
            verify(movieRepository).findById(MOVIE_ID);
            verify(showtimeRepository, never()).save(any(Showtime.class));
        }
    }

    @Nested
    @DisplayName("Delete Showtime Tests")
    class DeleteShowtimeTests {

        @Test
        @DisplayName("Should delete showtime when ID exists")
        void shouldDeleteShowtime_WhenIdExists() {
            Long showtimeId = 1L;
            when(showtimeRepository.existsById(showtimeId)).thenReturn(true);

            showtimeService.deleteShowtime(showtimeId);

            verify(showtimeRepository).existsById(showtimeId);
            verify(showtimeRepository).deleteById(showtimeId);
        }

        @Test
        @DisplayName("Should throw exception when ID does not exist")
        void shouldThrowException_WhenIdDoesNotExist() {
            Long showtimeId = 999L;
            when(showtimeRepository.existsById(showtimeId)).thenReturn(false);

            assertThatThrownBy(() -> showtimeService.deleteShowtime(showtimeId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Showtime not found with ID: " + showtimeId);

            verify(showtimeRepository).existsById(showtimeId);
            verify(showtimeRepository, never()).deleteById(any());
        }
    }

    private Movie createMovie() {
        Movie movie = new Movie();
        movie.setId(ShowtimeServiceTest.MOVIE_ID);
        movie.setTitle(ShowtimeServiceTest.MOVIE_TITLE);
        return movie;
    }

    private ShowtimeDTO createShowtimeDTO() {
        ShowtimeDTO dto = new ShowtimeDTO();
        dto.setMovieId(MOVIE_ID);
        dto.setTheater(THEATER);
        dto.setPrice(PRICE);
        dto.setStartTime(START_TIME);
        dto.setEndTime(END_TIME);
        return dto;
    }

    private Showtime createShowtime(Long id) {
        Showtime showtime = new Showtime();
        showtime.setId(id);
        showtime.setMovieId(MOVIE_ID);
        showtime.setTheater(THEATER);
        showtime.setPrice(PRICE);
        showtime.setStartTime(START_TIME);
        showtime.setEndTime(END_TIME);
        return showtime;
    }
} 