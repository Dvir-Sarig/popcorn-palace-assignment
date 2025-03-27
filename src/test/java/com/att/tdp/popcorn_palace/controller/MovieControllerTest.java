package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.dto.MovieDTO;
import com.att.tdp.popcorn_palace.exceptions.GlobalExceptionHandler;
import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.service.MovieService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class MovieControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MovieService movieService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        MovieController controller = new MovieController(movieService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAllMovies_ShouldReturnListOfMovies() throws Exception {
        List<Movie> movies = Arrays.asList(
                createMovie("The Matrix", "Action", 120, 8.5, 2024),
                createMovie("Pulp Fiction", "Crime", 150, 8.9, 1994),
                createMovie("Fight Club", "Drama", 139, 8.4, 1999)
        );
        when(movieService.getAllMovies()).thenReturn(movies);

        mockMvc.perform(get("/movies/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].title").value(org.hamcrest.Matchers.hasItems(
                        "The Matrix",
                        "Pulp Fiction",
                        "Fight Club"
                )));
    }

    @Test
    void createMovie_WithValidData_ShouldReturnCreatedMovie() throws Exception {
        MovieDTO movieDTO = createMovieDTO("New Movie", "Action", 120, 8.5, 2024);
        Movie movie = new Movie(movieDTO);
        when(movieService.createMovie(any(MovieDTO.class))).thenReturn(movie);

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(movieDTO.getTitle()))
                .andExpect(jsonPath("$.genre").value(movieDTO.getGenre()))
                .andExpect(jsonPath("$.duration").value(movieDTO.getDuration()))
                .andExpect(jsonPath("$.rating").value(movieDTO.getRating()))
                .andExpect(jsonPath("$.releaseYear").value(movieDTO.getReleaseYear()));
    }

    @Test
    void createMovie_WithMissingRequiredFields_ShouldReturnBadRequest() throws Exception {
        MovieDTO movieDTO = new MovieDTO();

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Title is required"))
                .andExpect(jsonPath("$.genre").value("Genre is required"))
                .andExpect(jsonPath("$.duration").value("Duration is required and must be at least 1"))
                .andExpect(jsonPath("$.rating").value("Rating is required"))
                .andExpect(jsonPath("$.releaseYear").value("Year release is required and must be at least 1"));
    }

    @Test
    void updateMovie_WithValidData_ShouldReturnUpdatedMovie() throws Exception {
        String title = "The Matrix";
        MovieDTO movieDTO = createMovieDTO(title, "Updated Genre", 150, 9.0, 2025);
        Movie movie = new Movie(movieDTO);
        when(movieService.updateMovie(eq(title), any(MovieDTO.class))).thenReturn(movie);

        mockMvc.perform(post("/movies/update/{title}", title)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(movieDTO.getTitle()))
                .andExpect(jsonPath("$.genre").value(movieDTO.getGenre()))
                .andExpect(jsonPath("$.duration").value(movieDTO.getDuration()))
                .andExpect(jsonPath("$.rating").value(movieDTO.getRating()))
                .andExpect(jsonPath("$.releaseYear").value(movieDTO.getReleaseYear()));
    }

    @Test
    void updateMovie_WhenMovieDoesNotExist_ShouldReturnNotFound() throws Exception {
        String title = "Non-existent Movie";
        MovieDTO movieDTO = createMovieDTO(title, "Updated Genre", 150, 9.0, 2025);
        when(movieService.updateMovie(eq(title), any(MovieDTO.class)))
                .thenThrow(new RuntimeException("Movie not found"));

        mockMvc.perform(post("/movies/update/{title}", title)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Movie not found"));
    }

    @Test
    void updateMovie_WithIncompleteData_ShouldReturnBadRequest() throws Exception {
        String title = "The Matrix";
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setTitle(title);

        mockMvc.perform(post("/movies/update/{title}", title)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.genre").value("Genre is required"))
                .andExpect(jsonPath("$.duration").value("Duration is required and must be at least 1"))
                .andExpect(jsonPath("$.rating").value("Rating is required"))
                .andExpect(jsonPath("$.releaseYear").value("Year release is required and must be at least 1"));
    }

    @Test
    void deleteMovie_WhenMovieExists_ShouldDeleteMovie() throws Exception {
        String title = "The Matrix";
        doNothing().when(movieService).deleteMovie(title);

        mockMvc.perform(delete("/movies/{title}", title))
                .andExpect(status().isOk())
                .andExpect(content().string("Movie deleted successfully."));

        verify(movieService, times(1)).deleteMovie(title);
    }

    @Test
    void deleteMovie_WhenMovieDoesNotExist_ShouldReturnNotFound() throws Exception {
        String title = "Non-existent Movie";
        doThrow(new RuntimeException("Movie not found")).when(movieService).deleteMovie(title);

        mockMvc.perform(delete("/movies/{title}", title))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Movie not found"));
    }

    private MovieDTO createMovieDTO(String title, String genre, int duration, double rating, int releaseYear) {
        MovieDTO dto = new MovieDTO();
        dto.setTitle(title);
        dto.setGenre(genre);
        dto.setDuration(duration);
        dto.setRating(rating);
        dto.setReleaseYear(releaseYear);
        return dto;
    }

    private Movie createMovie(String title, String genre, int duration, double rating, int releaseYear) {
        MovieDTO dto = createMovieDTO(title, genre, duration, rating, releaseYear);
        return new Movie(dto);
    }
} 