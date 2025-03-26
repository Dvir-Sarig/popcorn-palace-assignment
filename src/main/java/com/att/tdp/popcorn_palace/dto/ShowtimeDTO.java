package com.att.tdp.popcorn_palace.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Data
public class ShowtimeDTO {
    @NotNull(message = "Movie ID is required")
    @JsonProperty("movieId")
    private Long movieId;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    @JsonProperty("price")
    private Double price;

    @NotBlank(message = "Theater is required")
    @JsonProperty("theater")
    private String theater;

    @NotNull(message = "Start time is required")
    @JsonProperty("startTime")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    @JsonProperty("endTime")
    private LocalDateTime endTime;
} 