package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.dto.BookingDTO;
import com.att.tdp.popcorn_palace.model.Booking;
import com.att.tdp.popcorn_palace.model.Showtime;
import com.att.tdp.popcorn_palace.repository.BookingRepository;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ShowtimeRepository showtimeRepository;

    private BookingService bookingService;

    @BeforeEach
    void setup() {
        bookingService = new BookingService(bookingRepository, showtimeRepository);
    }

    @Test
    void createBooking_WithValidData_ShouldCreateBooking() {
        BookingDTO bookingDTO = createBookingDTO();
        Showtime showtime = createShowtime();
        Booking booking = createBooking(UUID.randomUUID(), bookingDTO);

        when(showtimeRepository.findById(bookingDTO.getShowtimeId())).thenReturn(Optional.of(showtime));
        when(bookingRepository.existsByShowtimeIdAndSeatNumber(bookingDTO.getShowtimeId(), bookingDTO.getSeatNumber())).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking result = bookingService.createBooking(bookingDTO);

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(bookingDTO.getShowtimeId(), result.getShowtimeId());
        assertEquals(bookingDTO.getSeatNumber(), result.getSeatNumber());
        assertEquals(bookingDTO.getUserId(), result.getUserId());

        verify(showtimeRepository).findById(bookingDTO.getShowtimeId());
        verify(bookingRepository).existsByShowtimeIdAndSeatNumber(bookingDTO.getShowtimeId(), bookingDTO.getSeatNumber());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createBooking_WithNonExistentShowtime_ShouldThrowException() {
        BookingDTO bookingDTO = createBookingDTO();
        when(showtimeRepository.findById(bookingDTO.getShowtimeId())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookingService.createBooking(bookingDTO));
        assertEquals("Showtime not found with ID: " + bookingDTO.getShowtimeId(), exception.getMessage());

        verify(showtimeRepository).findById(bookingDTO.getShowtimeId());
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void createBooking_WithAlreadyBookedSeat_ShouldThrowException() {
        BookingDTO bookingDTO = createBookingDTO();
        Showtime showtime = createShowtime();

        when(showtimeRepository.findById(bookingDTO.getShowtimeId())).thenReturn(Optional.of(showtime));
        when(bookingRepository.existsByShowtimeIdAndSeatNumber(bookingDTO.getShowtimeId(), bookingDTO.getSeatNumber())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(bookingDTO));
        assertEquals("Seat " + bookingDTO.getSeatNumber() + " is already booked for this showtime", exception.getMessage());

        verify(showtimeRepository).findById(bookingDTO.getShowtimeId());
        verify(bookingRepository).existsByShowtimeIdAndSeatNumber(bookingDTO.getShowtimeId(), bookingDTO.getSeatNumber());
        verifyNoMoreInteractions(bookingRepository);
    }

    private BookingDTO createBookingDTO() {
        BookingDTO dto = new BookingDTO();
        dto.setShowtimeId(1L);
        dto.setSeatNumber(1);
        dto.setUserId("user123");
        return dto;
    }

    private Showtime createShowtime() {
        Showtime showtime = new Showtime();
        showtime.setId(1L);
        showtime.setMovieId(1L);
        showtime.setTheater("Theater 1");
        showtime.setPrice(12.99);
        showtime.setStartTime(LocalDateTime.now().plusDays(1));
        showtime.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));
        return showtime;
    }

    private Booking createBooking(UUID id, BookingDTO dto) {
        Booking booking = new Booking(dto);
        booking.setId(id);
        return booking;
    }
} 