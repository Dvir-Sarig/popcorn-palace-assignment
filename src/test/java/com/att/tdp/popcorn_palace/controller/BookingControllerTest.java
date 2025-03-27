package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.dto.BookingDTO;
import com.att.tdp.popcorn_palace.exceptions.GlobalExceptionHandler;
import com.att.tdp.popcorn_palace.model.Booking;
import com.att.tdp.popcorn_palace.service.BookingService;
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

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookingService bookingService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        BookingController controller = new BookingController(bookingService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createBooking_WithValidData_ShouldReturnCreatedBooking() throws Exception {
        BookingDTO bookingDTO = createBookingDTO();
        Booking booking = createBooking(UUID.randomUUID(), bookingDTO);
        when(bookingService.createBooking(any(BookingDTO.class))).thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(booking.getId().toString()));
    }

    @Test
    void createBooking_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        BookingDTO bookingDTO = new BookingDTO(); // Empty DTO with no required fields

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.showtimeId").value("Showtime ID is required"))
                .andExpect(jsonPath("$.seatNumber").value("Seat number is required"))
                .andExpect(jsonPath("$.userId").value("User ID is required"));
    }

    @Test
    void createBooking_WithInvalidShowtimeId_ShouldReturnBadRequest() throws Exception {
        BookingDTO bookingDTO = createBookingDTO();
        bookingDTO.setShowtimeId(0L); // Invalid ID (less than 1)

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.showtimeId").value("Showtime ID must be positive"));
    }

    @Test
    void createBooking_WithInvalidSeatNumber_ShouldReturnBadRequest() throws Exception {
        BookingDTO bookingDTO = createBookingDTO();
        bookingDTO.setSeatNumber(0); // Invalid seat number (less than 1)

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.seatNumber").value("Seat number must be positive"));
    }

    private BookingDTO createBookingDTO() {
        BookingDTO dto = new BookingDTO();
        dto.setShowtimeId(1L);
        dto.setSeatNumber(1);
        dto.setUserId("user123");
        return dto;
    }

    private Booking createBooking(UUID id, BookingDTO dto) {
        Booking booking = new Booking(dto);
        booking.setId(id);
        return booking;
    }
} 