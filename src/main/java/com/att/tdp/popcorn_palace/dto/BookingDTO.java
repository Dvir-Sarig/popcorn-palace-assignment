package com.att.tdp.popcorn_palace.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class BookingDTO {
    @NotNull(message = "Showtime ID is required")
    @Min(value = 1, message = "Showtime ID must be positive")
    @JsonProperty("showtimeId")
    private Long showtimeId;

    @NotNull(message = "Seat number is required")
    @Min(value = 1, message = "Seat number must be positive")
    @JsonProperty("seatNumber")
    private Integer seatNumber;

    @NotBlank(message = "User ID is required")
    @JsonProperty("userId")
    private String userId;
} 