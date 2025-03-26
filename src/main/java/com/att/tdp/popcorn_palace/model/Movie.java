package com.att.tdp.popcorn_palace.model;

import com.att.tdp.popcorn_palace.dto.MovieDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(nullable = false, unique = true)
    @JsonProperty("title")
    private String title;

    @Column(nullable = false)
    @JsonProperty("genre")
    private String genre;

    @Column(nullable = false)
    @JsonProperty("duration")
    private int duration;

    @Column(nullable = false)
    @JsonProperty("rating")
    private Double rating;

    @Column(name = "release_year", nullable = false)
    @JsonProperty("releaseYear")
    private int releaseYear;

    public Movie(MovieDTO movieDTO) {
        this.title = movieDTO.getTitle();
        this.genre = movieDTO.getGenre();
        this.duration = movieDTO.getDuration();
        this.rating = movieDTO.getRating();
        this.releaseYear = movieDTO.getReleaseYear();
    }
}
