package com.att.tdp.popcorn_palace.model;

import com.att.tdp.popcorn_palace.dto.BookingDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "booking_id")
    @JsonProperty("bookingId")
    private UUID id;

    @Column(name = "showtime_id", nullable = false)
    @JsonIgnore
    private Long showtimeId;

    @Column(name = "seat_number", nullable = false)
    @JsonIgnore
    private Integer seatNumber;

    @Column(name = "user_id", nullable = false)
    @JsonIgnore
    private String userId;

    public Booking(BookingDTO dto) {
        this.showtimeId = dto.getShowtimeId();
        this.seatNumber = dto.getSeatNumber();
        this.userId = dto.getUserId();
    }
} 