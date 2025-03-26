package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.dto.ShowtimeDTO;
import com.att.tdp.popcorn_palace.model.Movie;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class ShowtimeService {
    private static final Logger logger = LoggerFactory.getLogger(ShowtimeService.class);
    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;

    public ShowtimeService(ShowtimeRepository showtimeRepository, MovieRepository movieRepository) {
        this.showtimeRepository = showtimeRepository;
        this.movieRepository = movieRepository;
    }

    @Transactional
    public Showtime createShowtime(ShowtimeDTO showtimeDTO) {
        Movie movie = movieRepository.findById(showtimeDTO.getMovieId())
                .orElseThrow(() -> new EntityNotFoundException("Movie not found with ID: " + showtimeDTO.getMovieId()));

        List<Showtime> overlappingShowtimes = showtimeRepository.findOverlappingShowtimes(
                showtimeDTO.getTheater(),
                showtimeDTO.getStartTime(),
                showtimeDTO.getEndTime()
        );

        if (!overlappingShowtimes.isEmpty()) {
            throw new IllegalArgumentException("There are overlapping showtimes in this theater");
        }

        Showtime showtime = new Showtime(showtimeDTO);
        logger.info("Creating showtime for movie: {} in theater: {} at: {}", 
            movie.getTitle(), showtimeDTO.getTheater(), showtimeDTO.getStartTime());
            
        return showtimeRepository.save(showtime);
    }

    @Transactional(readOnly = true)
    public Showtime getShowtimeById(Long id) {
        logger.info("Fetching showtime with ID: {}", id);
        return showtimeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Showtime not found with ID: " + id));
    }

    @Transactional
    public Showtime updateShowtime(Long id, ShowtimeDTO showtimeDTO) {
        Showtime existingShowtime = getShowtimeById(id);
        Movie movie = movieRepository.findById(showtimeDTO.getMovieId())
                .orElseThrow(() -> new EntityNotFoundException("Movie not found with ID: " + showtimeDTO.getMovieId()));
        List<Showtime> overlappingShowtimes = showtimeRepository.findOverlappingShowtimes(
                showtimeDTO.getTheater(),
                showtimeDTO.getStartTime(),
                showtimeDTO.getEndTime()
        );
        if (!overlappingShowtimes.isEmpty() && !overlappingShowtimes.get(0).getId().equals(id)) {
            throw new IllegalArgumentException("There are overlapping showtimes in this theater");
        }
        Showtime updatedShowtime = new Showtime(showtimeDTO);
        updatedShowtime.setId(existingShowtime.getId());
        logger.info("Updating showtime with ID: {} for movie: {} in theater: {}", 
            id, movie.getTitle(), showtimeDTO.getTheater());
        return showtimeRepository.save(updatedShowtime);
    }

    @Transactional
    public void deleteShowtime(Long id) {
        logger.info("Deleting showtime with ID: {}", id);
        if (!showtimeRepository.existsById(id)) {
            throw new EntityNotFoundException("Showtime not found with ID: " + id);
        }
        showtimeRepository.deleteById(id);
    }
} 