package com.epam.training.ticketservice.core.movie.service;

import com.epam.training.ticketservice.core.movie.model.MovieDto;
import com.epam.training.ticketservice.core.movie.persistence.Movie;
import com.epam.training.ticketservice.core.movie.persistence.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    @Override
    public Optional<MovieDto> createMovie(String title, String genre, Duration length) {
        if (movieRepository.findByTitle(title).isPresent()) {
            return Optional.empty();
        }
        Movie movie = new Movie(title, genre, length);
        movieRepository.save(movie);
        return Optional.of(new MovieDto(movie.getTitle(), movie.getGenre(), movie.getLength()));
    }

    @Override
    public List<MovieDto> listMovies() {
        return movieRepository.findAll().stream()
                .map((movie -> new MovieDto(movie.getTitle(), movie.getGenre(), movie.getLength())))
                .toList();
    }

    @Override
    public Optional<MovieDto> updateMovie(String title, String genre, Duration length) {
        Optional<Movie> movie = movieRepository.findByTitle(title);
        if (movie.isPresent()) {
            movie.get().setGenre(genre);
            movie.get().setLength(length);
            movieRepository.save(movie.get());
            return Optional.of(new MovieDto(movie.get().getTitle(), movie.get().getGenre(), movie.get().getLength()));
        }
        return Optional.empty();
    }

    @Override
    public boolean deleteMovie(String title) {
        Optional<Movie> movie = movieRepository.findByTitle(title);
        if (movie.isPresent()) {
            movieRepository.delete(movie.get());
            return true;
        }
        return false;
    }
}