package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.dto.MovieDTO;
import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    @Test
    void createMovie_WhenTitleDoesNotExist_ShouldCreateMovie() {
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setTitle("New Movie");
        movieDTO.setGenre("Action");
        movieDTO.setDuration(120);
        movieDTO.setRating(8.5);
        movieDTO.setReleaseYear(2024);

        when(movieRepository.existsByTitle(anyString())).thenReturn(false);
        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Movie result = movieService.createMovie(movieDTO);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(movieDTO.getTitle());
        assertThat(result.getGenre()).isEqualTo(movieDTO.getGenre());
        assertThat(result.getDuration()).isEqualTo(movieDTO.getDuration());
        assertThat(result.getRating()).isEqualTo(movieDTO.getRating());
        assertThat(result.getReleaseYear()).isEqualTo(movieDTO.getReleaseYear());
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void createMovie_WhenTitleExists_ShouldThrowException() {
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setTitle("Existing Movie");
        when(movieRepository.existsByTitle(anyString())).thenReturn(true);

        assertThatThrownBy(() -> movieService.createMovie(movieDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A movie with this title already exists");
        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void updateMovie_WhenMovieExists_ShouldUpdateMovie() {
        String title = "Existing Movie";
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setTitle(title);
        movieDTO.setGenre("Updated Genre");
        movieDTO.setDuration(150);
        movieDTO.setRating(9.0);
        movieDTO.setReleaseYear(2025);

        Movie existingMovie = new Movie();
        existingMovie.setTitle(title);
        existingMovie.setGenre("Old Genre");
        existingMovie.setDuration(120);
        existingMovie.setRating(8.0);
        existingMovie.setReleaseYear(2020);

        when(movieRepository.findByTitle(title)).thenReturn(Optional.of(existingMovie));
        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Movie result = movieService.updateMovie(title, movieDTO);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(movieDTO.getTitle());
        assertThat(result.getGenre()).isEqualTo(movieDTO.getGenre());
        assertThat(result.getDuration()).isEqualTo(movieDTO.getDuration());
        assertThat(result.getRating()).isEqualTo(movieDTO.getRating());
        assertThat(result.getReleaseYear()).isEqualTo(movieDTO.getReleaseYear());
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void updateMovie_WhenMovieDoesNotExist_ShouldThrowException() {
        String title = "Non-existent Movie";
        MovieDTO movieDTO = new MovieDTO();
        when(movieRepository.findByTitle(title)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movieService.updateMovie(title, movieDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Movie not found with title: " + title);
        verify(movieRepository, never()).save(any(Movie.class));
    }
} 