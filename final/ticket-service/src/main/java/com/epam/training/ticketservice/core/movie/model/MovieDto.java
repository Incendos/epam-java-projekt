package com.epam.training.ticketservice.core.movie.model;

import com.epam.training.ticketservice.core.movie.persistence.Movie;

import java.time.Duration;

public record MovieDto(String title, String genre, Duration length) {

    public MovieDto(Movie movie) {
        this(movie.getTitle(), movie.getGenre(), movie.getLength());
    }

    @Override
    public String toString() {
        return title
                + " ("
                + genre
                + ", "
                + length.toMinutes()
                + " minutes)";
    }
}