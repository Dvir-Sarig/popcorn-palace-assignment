package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.dto.MovieDTO;
import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class MovieService {
    private static final Logger logger = LoggerFactory.getLogger(MovieService.class);
    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Transactional
    public Movie createMovie(MovieDTO movieDTO) {
        if (movieRepository.existsByTitle(movieDTO.getTitle())) {
            logger.warn("Movie with title '{}' already exists", movieDTO.getTitle());
            throw new IllegalArgumentException("A movie with this title already exists");
        }

        Movie movie = new Movie(movieDTO);
        logger.info("Creating movie: {}", movie.getTitle());
        return movieRepository.save(movie);
    }

    @Transactional(readOnly = true)
    public List<Movie> getAllMovies() {
        logger.info("Fetching all movies from database");
        List<Movie> movies = movieRepository.findAll();
        logger.info("Found {} movies", movies.size());
        return movies;
    }

    @Transactional(readOnly = true)
    public Movie getMovieByTitle(String title) {
        logger.debug("Fetching movie with title: {}", title);
        return movieRepository.findByTitle(title)
                .orElseThrow(() -> {
                    logger.warn("Movie not found with title: {}", title);
                    return new EntityNotFoundException("Movie not found with title: " + title);
                });
    }

    @Transactional
    public Movie updateMovie(String title, MovieDTO movieDTO) {
        logger.info("Updating movie with title: {}", title);
        Movie existingMovie = getMovieByTitle(title);
        Movie updatedMovie = new Movie(movieDTO);
        updatedMovie.setId(existingMovie.getId());
        logger.info("Movie updated: {}", updatedMovie);
        return movieRepository.save(updatedMovie);
    }

    @Transactional
    public void deleteMovie(String title) {
        logger.info("Deleting movie with title: {}", title);
        if (!movieRepository.existsByTitle(title)) {
            logger.warn("Attempted to delete non-existing movie with title: {}", title);
            throw new EntityNotFoundException("Movie not found with title: " + title);
        }
        movieRepository.deleteByTitle(title);
        logger.info("Movie deleted successfully: {}", title);
    }
} 