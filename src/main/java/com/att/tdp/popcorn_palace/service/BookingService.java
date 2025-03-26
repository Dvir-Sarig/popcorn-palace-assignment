package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.dto.BookingDTO;
import com.att.tdp.popcorn_palace.model.Booking;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.repository.BookingRepository;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BookingService {
    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);
    private final BookingRepository bookingRepository;
    private final ShowtimeRepository showtimeRepository;

    public BookingService(BookingRepository bookingRepository, ShowtimeRepository showtimeRepository) {
        this.bookingRepository = bookingRepository;
        this.showtimeRepository = showtimeRepository;
    }

    @Transactional
    public Booking createBooking(BookingDTO bookingDTO) {
        logger.info("Attempting to create booking for showtime ID: {}, seat: {}, user: {}",
                bookingDTO.getShowtimeId(), bookingDTO.getSeatNumber(), bookingDTO.getUserId());

        Showtime showtime = showtimeRepository.findById(bookingDTO.getShowtimeId()).orElse(null);
        if (showtime == null) {
            throw new EntityNotFoundException("Showtime not found with ID: " + bookingDTO.getShowtimeId());
        }

        // Check if seat is already booked
        if (bookingRepository.existsByShowtimeIdAndSeatNumber(bookingDTO.getShowtimeId(), bookingDTO.getSeatNumber())) {
            throw new IllegalArgumentException("Seat " + bookingDTO.getSeatNumber() + " is already booked for this showtime");
        }

        Booking booking = new Booking(bookingDTO);
        Booking savedBooking = bookingRepository.save(booking);
        logger.info("Booking created successfully: {}", savedBooking);
        return savedBooking;
    }
} 