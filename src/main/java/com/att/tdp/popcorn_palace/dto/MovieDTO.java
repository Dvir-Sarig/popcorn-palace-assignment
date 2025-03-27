package com.att.tdp.popcorn_palace.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class MovieDTO {
    @NotBlank(message = "Title is required")
    @JsonProperty("title")
    private String title;

    @NotBlank(message = "Genre is required")
    @JsonProperty("genre")
    private String genre;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration is required and must be at least 1")
    @JsonProperty("duration")
    private int duration;

    @NotNull(message = "Rating is required")
    @Min(value = 0, message = "Rating is required and must be at least 0")
    @JsonProperty("rating")
    private Double rating;

    @NotNull(message = "Release year is required")
    @Min(value = 1, message = "Year release is required and must be at least 1")
    @Max(value = 2025, message = "Release year cannot exceed 2025")
    @JsonProperty("releaseYear")
    private int releaseYear;
}
