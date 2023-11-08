package com.epam.training.ticketservice.core.movie.service;

import com.epam.training.ticketservice.core.movie.model.MovieDto;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public interface MovieService {
    Optional<MovieDto> createMovie(String title, String genre, Duration length);

    List<MovieDto> listMovies();

    Optional<MovieDto> updateMovie(String title, String genre, Duration length);

    boolean deleteMovie(String title);
}
