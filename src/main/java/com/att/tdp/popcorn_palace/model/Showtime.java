package com.att.tdp.popcorn_palace.model;

import com.att.tdp.popcorn_palace.dto.ShowtimeDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "showtimes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Showtime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;

    @Column(nullable = false)
    @JsonProperty("price")
    private Double price;

    @Column(name = "movie_id", nullable = false)
    @JsonProperty("movieId")
    private Long movieId;

    @Column(nullable = false)
    @JsonProperty("theater")
    private String theater;

    @Column(name = "start_time", nullable = false)
    @JsonProperty("startTime")
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    @JsonProperty("endTime")
    private LocalDateTime endTime;

    public Showtime(ShowtimeDTO dto) {
        this.movieId = dto.getMovieId();
        this.price = dto.getPrice();
        this.theater = dto.getTheater();
        this.startTime = dto.getStartTime();
        this.endTime = dto.getEndTime();
    }
} 